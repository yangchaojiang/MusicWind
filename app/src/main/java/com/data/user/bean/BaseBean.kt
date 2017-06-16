package com.data.user.bean

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 数据解析错误
 */

class BaseBean<T :Any> {

    var showapi_res_code: Int = 0

    lateinit var showapi_res_error: String

    lateinit var showapi_res_body:  ShowapiResBody<T>
}
