package com.data.user.apapter

import android.widget.ImageView
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.data.user.R
import com.data.user.bean.MusicItem
import com.gildemodule.ImageLoader
import com.yutils.Log

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 适配器
 */
class ListAdapter(res: Int) : BaseQuickAdapter<MusicItem, BaseViewHolder>(res) {
    private val ress = res
    override fun convert(helper: com.chad.library.adapter.base.BaseViewHolder, item: MusicItem) {
        helper.setText(com.data.user.R.id.item_sing_name, mContext.getString(R.string.sing_name) + item.singername)
        helper.setText(com.data.user.R.id.item_song_name, mContext.getString(R.string.song_music) + item.songname)
        ImageLoader.getInstace().displayImage(mContext, item.albumpic_small, helper.getView<ImageView>(R.id.item_small))
        if (R.layout.list_item_play == ress) {
            helper.getView<RadioButton>(R.id.item_select_image).isChecked = item.isSelected
        }
    }


    fun addAll(list: List<MusicItem>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

}