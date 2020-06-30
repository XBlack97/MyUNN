package com.x.myunn.model

data class Notification(
    var userId: String = "",
    var text: String = "",
    var postId: String = "",
    var _isPost: Boolean = false
)