package com.data.user.service
import android.app.Service
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.cleveroad.audiowidget.AudioWidget
import com.data.user.R
import com.data.user.bean.MusicItem
import com.data.user.ui.MainActivity
import com.data.user.ui.PlayActivity
import java.io.IOException
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask
import jp.wasabeef.glide.transformations.CropCircleTransformation

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 音乐服务的实现
 *
 */
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioWidget.OnControlsClickListener, AudioWidget.OnWidgetStateChangedListener {
    private val items = ArrayList<MusicItem>()
    private var audioWidget: AudioWidget? = null
    private var mediaPlayer: MediaPlayer? = null
    private var preparing: Boolean = false
    private var playingIndex = -1
    private var paused: Boolean = false
    private var timer: Timer? = null
    private var cropCircleTransformation: CropCircleTransformation? = null
    private var preferences: SharedPreferences? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioWidget = AudioWidget.Builder(this)
                .build()
        audioWidget!!.controller().onControlsClickListener(this)
        audioWidget!!.controller().onWidgetStateChangedListener(this)
        cropCircleTransformation = CropCircleTransformation(this)
        regMyMusicBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                ACTION_SET_TRACKS -> {
                    updateTracks()
                }
                ACTION_PLAY_TRACKS -> {
                    selectNewTrack(intent)
                }
                ACTION_CHANGE_STATE -> {
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))) {
                        val show = intent.getBooleanExtra(EXTRA_CHANGE_STATE, false)
                        if (show) {
                            audioWidget!!.show(preferences!!.getInt(KEY_POSITION_X, 100), preferences!!.getInt(KEY_POSITION_Y, 100))
                        } else {
                            audioWidget!!.hide()
                        }
                    } else {
                        Log.w(TAG, "Can't change audio widget state! Device does not have drawOverlays permissions!")
                    }
                }
            }
        }
        return Service.START_STICKY
    }

    private fun selectNewTrack(intent: Intent) {
        if (preparing) {
            return
        }
        val item = intent.getSerializableExtra(EXTRA_SELECT_TRACK) as MusicItem
        if (item == null && playingIndex == -1 || playingIndex != -1 && items[playingIndex] == item) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                audioWidget!!.controller().pause()
            } else {
                mediaPlayer!!.start()
                audioWidget!!.controller().start()
            }
            audioWidget!!.hide()
            return
        }

        for (sing in items) {
            if (sing.songid == item.songid) {
                playingIndex = items.indexOf(sing)
                break
            }
        }
        startCurrentTrack()
    }

    private fun startCurrentTrack() {
        if (mediaPlayer!!.isPlaying || paused) {
            mediaPlayer!!.stop()
            paused = false
        }
        mediaPlayer!!.reset()
        if (playingIndex < 0) {
            return
        }
        try {
            mediaPlayer!!.setDataSource(this, Uri.parse(items[playingIndex].url))
            mediaPlayer!!.prepareAsync()
            preparing = true
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun updateTracks() {
        var playingItem: MusicItem? = null
        if (playingIndex != -1) {
            playingItem = items[playingIndex]
        }


        if (MusicService.tracks != null) {
            if (tracks != items) {
                items.clear()
                items.addAll(tracks!!)
            }
            MusicService.tracks = null
        }
        if (playingItem == null) {
            playingIndex = -1
        } else {
            playingIndex = this.items.indexOf(playingItem)
        }
        if (playingIndex == -1 && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
    }

    override fun onDestroy() {
        audioWidget!!.controller().onControlsClickListener(null)
        audioWidget!!.controller().onWidgetStateChangedListener(null)
        audioWidget!!.hide()
        audioWidget = null
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer!!.reset()
        mediaPlayer!!.release()
        mediaPlayer = null
        stopTrackingPosition()
        cropCircleTransformation = null
        preferences = null
        unregisterReceiver(myMusicBroadcastReceiver)
        super.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer) {
        preparing = false
        mediaPlayer!!.start()
        if (!audioWidget!!.isShown) {
          //  audioWidget!!.show(preferences!!.getInt(KEY_POSITION_X, 100), preferences!!.getInt(KEY_POSITION_Y, 100))
        }
        audioWidget!!.controller().start()
        audioWidget!!.controller().position(0)
        audioWidget!!.controller().duration(mediaPlayer!!.duration)
        stopTrackingPosition()
        startTrackingPosition()
        val size = resources.getDimensionPixelSize(R.dimen.cover_size)
        Glide.with(this)
                .load(items[playingIndex].albumpic_small)
                .asBitmap()
                .override(size, size)
                .centerCrop()
                .transform(cropCircleTransformation!!)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                        if (audioWidget != null) {
                            audioWidget!!.controller().albumCoverBitmap(resource)
                        }
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        if (audioWidget != null) {
                            audioWidget!!.controller().albumCover(null)
                        }
                    }
                })
    }

    override fun onCompletion(mp: MediaPlayer) {
        if (playingIndex == -1) {
            audioWidget!!.controller().stop()
            return
        }
        playingIndex++
        if (playingIndex >= items.size) {
            playingIndex = 0
            if (items.size == 0) {
                audioWidget!!.controller().stop()
                return
            }
        }
        startCurrentTrack()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        preparing = true
        return false
    }

    override fun onPlaylistClicked(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return false
    }

    override fun onPlaylistLongClicked() {
        Log.d(TAG, "playlist long clicked")
    }

    override fun onPreviousClicked() {
        if (items.size == 0)
            return
        playingIndex--
        if (playingIndex < 0) {
            playingIndex = items.size - 1
        }
        startCurrentTrack()
        PlayActivity.setStateUpdate(this,3)
    }

    override fun onPreviousLongClicked() {
        Log.d(TAG, "previous long clicked")
    }

    override fun onPlayPauseClicked(): Boolean {
        if (playingIndex == -1) {
            Toast.makeText(this, R.string.song_not_selected, Toast.LENGTH_SHORT).show()
            return true
        }
        if (mediaPlayer!!.isPlaying) {
            stopTrackingPosition()
            mediaPlayer!!.pause()
            audioWidget!!.controller().start()
            paused = true
        } else {
            startTrackingPosition()
            audioWidget!!.controller().pause()
            mediaPlayer!!.start()
            paused = false
        }
        PlayActivity.setStateUpdate(this,1)
        return false

    }

    override fun onPlayPauseLongClicked() {
        Log.d(TAG, "play/pause long clicked")
    }

    override fun onNextClicked() {
        PlayActivity.setStateUpdate(this,2)
        if (items.size == 0)
            return
        playingIndex++
        if (playingIndex >= items.size) {
            playingIndex = 0
        }
        startCurrentTrack()
    }

    override fun onNextLongClicked() {
        Log.d(TAG, "next long clicked")
    }

    override fun onAlbumClicked() {
        Log.d(TAG, "album clicked")
    }

    override fun onAlbumLongClicked() {
        Log.d(TAG, "album long clicked")
    }

    private fun startTrackingPosition() {
        timer = Timer(TAG)
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val widget = audioWidget
                val player = mediaPlayer
                if (widget != null) {
                    widget.controller().position(player!!.currentPosition)
                }
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL)
    }

    private fun stopTrackingPosition() {
        if (timer == null)
            return
        timer!!.cancel()
        timer!!.purge()
        timer = null
    }

    override fun onWidgetStateChanged(state: AudioWidget.State) {

    }

    override fun onWidgetPositionChanged(cx: Int, cy: Int) {
        preferences!!.edit()
                .putInt(KEY_POSITION_X, cx)
                .putInt(KEY_POSITION_Y, cy)
                .apply()
    }

    val myMusicBroadcastReceiver: MyMusicBroadcastReceiver = MyMusicBroadcastReceiver()
    /****
     * 注册广播
     * ***/
    fun regMyMusicBroadcastReceiver() {
        val intentFilter = IntentFilter(ACTION_MUSIC_UPDATE_STATE)
        registerReceiver(myMusicBroadcastReceiver, intentFilter)
    }

    /****
     * 广播交互
     * ***/
    inner class MyMusicBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "asdasdasd")
            if (intent.action == ACTION_MUSIC_UPDATE_STATE) {
                val state = intent.getIntExtra(ACTION_MUSIC_UPDATE_STATE_NAME, -1)
                when (state) {
                    1 -> {//播放暂停
                        audioWidget!!.controller().pause()
                        onPlayPauseClicked()
                    }
                    2 -> {//下一曲
                        onNextClicked()
                    }
                    3 -> {//上一曲
                        onPreviousClicked()
                    }

                }
            }
        }

    }

    companion object {
        private val TAG = MusicService::class.java.name
        private val ACTION_MUSIC_UPDATE_STATE = "com.action.ACTION_MUSIC_UPDATE_STATE"
        private val ACTION_MUSIC_UPDATE_STATE_NAME = "name"
        private val ACTION_SET_TRACKS = "ACTION_SET_TRACKS"
        private val ACTION_PLAY_TRACKS = "ACTION_PLAY_TRACKS"
        private val ACTION_CHANGE_STATE = "ACTION_CHANGE_STATE"
        private val EXTRA_SELECT_TRACK = "EXTRA_SELECT_TRACK"
        private val EXTRA_CHANGE_STATE = "EXTRA_CHANGE_STATE"
        private val UPDATE_INTERVAL: Long = 1000
        private val KEY_POSITION_X = "position_x"
        private val KEY_POSITION_Y = "position_y"
        private var tracks: List<MusicItem>? = null

        fun setTracks(context: Context, list: List<MusicItem>) {
            val intent = Intent(ACTION_SET_TRACKS, null, context, MusicService::class.java)

            tracks = list
            intent.action = ACTION_SET_TRACKS
            context.startService(intent)
        }

        fun playTrack(context: Context, item: MusicItem) {
            val intent = Intent(ACTION_PLAY_TRACKS, null, context, MusicService::class.java)
            intent.putExtra(EXTRA_SELECT_TRACK, item)
            intent.action = ACTION_PLAY_TRACKS
            context.startService(intent)
        }

        fun setState(context: Context, isShowing: Boolean) {
            val intent = Intent(ACTION_CHANGE_STATE, null, context, MusicService::class.java)
            intent.putExtra(EXTRA_CHANGE_STATE, isShowing)
            intent.action = ACTION_CHANGE_STATE
            context.startService(intent)
        }

        fun setStateUpdate(context: Context, state: Int) {
            val intent = Intent()
            intent.action = ACTION_MUSIC_UPDATE_STATE
            intent.putExtra(ACTION_MUSIC_UPDATE_STATE_NAME, state)
            context.sendBroadcast(intent)
        }

    }


}
