package io.github.rosemoe.msgRenderer

import java.awt.Image

data class UserInfo(
    val avatar: Image? = null,
    val nickname: String? = null,
    val title: String? = null
)
