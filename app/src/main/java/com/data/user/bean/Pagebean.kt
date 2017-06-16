package com.data.user.bean

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
class Pagebean {

      var total_song_num: Int = 0

      var ret_code: Int = 0

      var color: Int = 0

      var cur_song_num: Int = 0

      var comment_num: Int = 0

      var currentPage: Int = 0

      var song_begin: Int = 0

      var totalpage: Int = 0

     lateinit  var songlist: List<MusicItem>
}