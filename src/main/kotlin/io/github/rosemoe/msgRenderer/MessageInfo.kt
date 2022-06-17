package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.message.data.Message

/**
 * Message for a user. The object can be reused during one rendering action.
 */
data class MessageInfo(
    val message: Message,
    val user: UserInfo,
    val isReceivedMessage: Boolean = true
)