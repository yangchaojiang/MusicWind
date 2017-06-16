package com.data.user.bean

import java.io.Serializable

/**
 * Music track model.
 */
class MusicItem : Serializable {

    var seconds: Int? = null

    var albummid: String? = null

    var songid: Int? = null//
    var songname: String? = null//歌曲名称
    var singerid: Int? = null//ge歌曲id

    var albumpic_big: String? = null//北京图

    var albumpic_small: String? = null //专辑头像

    var downUrl: String? = null//下载地址

    var url: String? = null//播放地址

    var singername: String? = null //歌手

    var albumid: Int? = null

    var   isSelected: Boolean = false//是否在选中

}
