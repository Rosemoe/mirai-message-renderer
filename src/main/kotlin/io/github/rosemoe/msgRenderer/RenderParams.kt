package io.github.rosemoe.msgRenderer

import java.awt.Color
import java.awt.Font

data class RenderParams(
    val widthLimit: Int = 700,
    val avatarSize: Int = 100,
    val nicknameTypeface: Font = defaultFont().deriveFont(16f),
    val messageTypeface: Font = defaultFont().deriveFont(19f),
    val commonMargin: Int = 16,
    val roundAvatar: Boolean = true,
    val imageMaxWidth: Int = 400,
    val imageMaxHeight: Int = 500,
    // Colors
    val nicknameColor: Color = Color.BLACK,
    val messageTextColor: Color = Color.BLACK,
    val atTextColor: Color = Color.BLUE,
    val backgroundColor: Color = Color.WHITE
)
