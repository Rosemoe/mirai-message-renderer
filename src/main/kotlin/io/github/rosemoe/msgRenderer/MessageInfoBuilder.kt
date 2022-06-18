package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import java.awt.Image

/**
 * a tool to build messages easily
 *
 * for example:
 *```
 *buildMessageInfo{
 *  "rosemoe" titled “肉丝” send "hello" at left
 *  "rosemoe" send "title"
 * }
 *
 *
 * ```
 */
@Suppress("unused")
class MessageInfoBuilder {
    private val messages: ArrayList<MessageInfo> = arrayListOf()

    val left = 0
    val right = 1

    infix fun String.titled(title: String): UserInfo {
        return UserInfo(nickname = this).titled(title)
    }

    infix fun UserInfo.titled(title: String): UserInfo {
        return UserInfo(this.avatar, this.nickname, title)
    }

    infix fun UserInfo.send(content: Message): MessageInfo {
        val result = MessageInfo(
            content,
            this
        )
        messages.add(result)
        return result
    }

    infix fun MessageInfo.at(side: Int): MessageInfo {
        val new =  MessageInfo(this.message, this.user, side == left)
        messages[messages.indexOf(this)] = new
        return messages[messages.indexOf(new)]
    }

    infix fun UserInfo.avatar(img: Image): UserInfo {
        return UserInfo(img, this.nickname, this.title)
    }

    infix fun UserInfo.send(content: String): MessageInfo {
        val result = MessageInfo(
            PlainText(content),
            this
        )
        messages.add(result)
        return result
    }

    infix fun String.avatar(img: Image): UserInfo {
        return UserInfo(nickname = this).avatar(img)
    }

    infix fun String.send(content: String): MessageInfo {
        return UserInfo(nickname = this).send(content)
    }

    infix fun String.send(content: Message): MessageInfo {
        return UserInfo(nickname = this).send(content)
    }

    fun build(): ArrayList<MessageInfo> {
        return messages
    }
}


fun buildMessageInfo(invoke: MessageInfoBuilder.() -> Unit): ArrayList<MessageInfo> {
    val builder = MessageInfoBuilder()
    builder.invoke()
    return builder.build()
}