package io.github.rosemoe.msgRenderer

import java.awt.Color
import java.awt.Font

data class RenderParams(
    /**
     * The width limitation for a single message piece. This includes the space of margins,
     * avatar, nickname and title
     */
    val widthLimit: Int = 900,
    /**
     * The width of image generated when using [MessageRenderer.renderMessages]
     */
    val multiMsgWidth: Int = 1200,
    /**
     * The width and height of avatar
     */
    val avatarSize: Int = 80,
    /**
     * Typeface for nicknames
     */
    val nicknameTypeface: Font = defaultFont.deriveFont(18f),
    /**
     * Typeface for title texts
     */
    val titleTypeface: Font = defaultFont.deriveFont(17f),
    /**
     * Typeface for message content
     */
    val messageTypeface: Font = defaultFont.deriveFont(20f),
    /**
     * Margin commonly used in whole image
     */
    val commonMargin: Int = 20,
    /**
     * Draw the avatar in a round rectangle
     */
    val roundAvatar: Boolean = true,
    /**
     * The max width for a single image
     */
    val imageMaxWidth: Int = 400,
    val titleBadgePadding: Int = 3,
    // Colors
    val nicknameColor: Color = Color.BLACK,
    val sentMessageTextColor: Color = Color.WHITE,
    val receivedMessageTextColor: Color = Color.BLACK,
    val atTextColor: Color = Color.BLUE,
    val backgroundColor: Color = Color.WHITE,
    val balloonSendColor: Color = Color(0, 99, 255, 99),
    val balloonReceiveColor: Color = Color(0xde, 0xde, 0xde),
    val titleTextColor: Color = Color.WHITE,
    val titleBadgeColor: Color = Color(55, 76, 228)
)
