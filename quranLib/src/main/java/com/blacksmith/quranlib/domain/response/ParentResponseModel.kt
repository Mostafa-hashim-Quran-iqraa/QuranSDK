package com.blacksmith.quranlib.domain.response

open class ParentResponseModel(
    var success: Boolean = false,
    var error_code:Int = 0,
    var status_code:Int = 0,
    var message: String? = "",
    var code: String? = "",
) {


}