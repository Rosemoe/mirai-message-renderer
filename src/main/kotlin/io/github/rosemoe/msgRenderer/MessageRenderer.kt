package io.github.rosemoe.msgRenderer

import io.github.rosemoe.msgRenderer.render.*
import net.mamoe.mirai.message.data.*
import java.awt.*
import java.awt.image.BufferedImage
import kotlin.math.min
import net.mamoe.mirai.message.data.Image as MiraiImage

class MessageRenderer(private val params: RenderParams = RenderParams()) {

    fun renderMessages(
        messages: List<MessageInfo>,
        dataProvider: DataProvider
    ): Image {
        val layouts = mutableListOf<GeneratedLayout>()
        var height = 0
        messages.forEach {
            val info = it.user
            layouts.add(
                generateGeneralLayout(
                    it.message,
                    dataProvider,
                    info.avatar,
                    info.nickname,
                    info.title,
                    it.isReceivedMessage
                ).also {
                    height += it.height
                })
        }
        val image = BufferedImage(params.multiMsgWidth, height, BufferedImage.TYPE_4BYTE_ABGR)
        val g = image.createGraphics()
        g.enableAntialias()
        var y = 0
        for (i in 0 until layouts.size) {
            val isReceive = messages[i].isReceivedMessage
            val layout = layouts[i]
            if (!isReceive) {
                layout.translate(params.multiMsgWidth - layout.width, y)
            } else {
                layout.translate(0, y)
            }
            y += layout.height
            layout.renderInto(g)
        }
        g.dispose()
        return image
    }

    fun renderMessage(
        message: Message,
        dataProvider: DataProvider,
        userInfo: UserInfo,
        isReceivedMessage: Boolean = true
    ) = renderMessage(message, dataProvider, userInfo.avatar, userInfo.nickname, userInfo.title, isReceivedMessage)

    fun renderMessage(
        message: Message,
        dataProvider: DataProvider,
        avatar: Image? = null,
        nickname: String? = null,
        title: String? = null,
        isReceivedMessage: Boolean = true
    ): Image {
        val layout = generateGeneralLayout(message, dataProvider, avatar, nickname, title, isReceivedMessage)
        val image = BufferedImage(layout.width, layout.height, BufferedImage.TYPE_4BYTE_ABGR)
        val g = image.createGraphics()
        g.enableAntialias()
        layout.renderInto(g)
        g.dispose()
        return image
    }

    private fun generateGeneralLayout(
        message: Message,
        dataProvider: DataProvider,
        avatar: Image? = null,
        nickname: String? = null,
        title: String? = null,
        isReceivedMessage: Boolean
    ): GeneratedLayout {
        // Step 1. Collect elements
        val elements = collectMessageSegments(message)
        var x = 0
        var y = 0
        val avatarSize = if (avatar == null) 0 else params.avatarSize + params.commonMargin

        // Step 2. Measure elements & obtain images
        val maxLayoutWidth = params.widthLimit - avatarSize - params.commonMargin * 2
        var maxRowWidth = 0
        var rowWidth = 0
        val blankImage = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
        val measureGraphics = blankImage.createGraphics()
        measureGraphics.enableAntialias()
        measureGraphics.font = params.messageTypeface
        for (element in elements) {
            if (isMessageDisplayedAsText(element)) {
                val text = if (element is At) dataProvider.getTextForAt(element) else element.contentToString()
                // Compute longest line
                var first = true
                text.lines().forEach {
                    val textWidth = measureGraphics.fontMetrics.stringWidth(it)
                    if (first) {
                        rowWidth += textWidth
                        first = false
                    } else {
                        rowWidth = textWidth
                    }
                    if (maxRowWidth < rowWidth) {
                        maxRowWidth = rowWidth
                    }
                }
            } else {
                val image = if (element is MiraiImage) {
                    dataProvider.getImageForMessage(element)
                } else if (element is Face) {
                    dataProvider.getImageForFace(element)
                } else {
                    null
                }
                if (image == null) {
                    val text = element.contentToString()
                    var first = true
                    text.lines().forEach {
                        val textWidth = measureGraphics.fontMetrics.stringWidth(it)
                        if (first) {
                            rowWidth += textWidth
                            first = false
                        } else {
                            rowWidth = textWidth
                        }
                        if (maxRowWidth < rowWidth) {
                            maxRowWidth = rowWidth
                        }
                    }
                } else {
                    rowWidth += Integer.min(image.getWidth(null), params.imageMaxWidth)
                    if (rowWidth > maxRowWidth) {
                        maxRowWidth = rowWidth
                    }
                }
            }
        }

        // Step 3. Measure the nickname and title
        var nicknameWidth = 0
        val nicknameHeight = if (nickname == null) {
            0
        } else {
            measureGraphics.font = params.nicknameTypeface
            val bounds = params.nicknameTypeface.getStringBounds(
                nickname,
                measureGraphics.fontRenderContext
            )
            nicknameWidth = bounds.width.toInt()
            bounds.height.toInt() + params.commonMargin / 2
        }
        var titleWidth = 0
        var titleRegionWidth = 0
        if (title != null) {
            measureGraphics.font = params.titleTypeface
            titleWidth = measureGraphics.fontMetrics.stringWidth(title)
            titleRegionWidth = titleWidth + params.commonMargin // actual margin is 0.5x
        }

        // Step 4. Determine the width of image
        val layoutWidth = min(maxLayoutWidth, maxRowWidth)
        val imageWidth = Integer.max(layoutWidth, titleRegionWidth + nicknameWidth) + params.commonMargin * 2 + avatarSize

        // Step 5. Determine the height of image as well the position of objects
        // Assume that nickname and avatar are placed at left
        x = params.commonMargin + avatarSize
        y = params.commonMargin + nicknameHeight
        measureGraphics.font = params.messageTypeface
        val lineHeight = measureGraphics.fontMetrics.height
        val baselineOffset = lineHeight - measureGraphics.fontMetrics.descent
        val displayList = mutableListOf<ElementRenderNode>()

        var layoutX = 0
        var layoutY = 0
        var row = Row(y + layoutY, lineHeight)
        for (element in elements) {
            var text: String? = null
            var image: Image? = null
            if (isMessageDisplayedAsText(element)) {
                text = if (element is At) dataProvider.getTextForAt(element) else element.contentToString()
            } else {
                image = if (element is MiraiImage) {
                    dataProvider.getImageForMessage(element)
                } else if (element is Face) {
                    dataProvider.getImageForFace(element)
                } else {
                    null
                }
                if (image == null) {
                    text = element.contentToString()
                }
            }
            if (text != null) {
                // Render the text
                var first = true
                text.lines().forEach { line ->
                    if (first) {
                        first = false
                    } else {
                        layoutY += row.height
                        layoutX = 0
                        row = Row(y + layoutY, lineHeight)
                    }
                    var begin = 0
                    var len = 0
                    val length = line.length
                    val chars = line.toCharArray()
                    var lastWidth = 0
                    while (begin < length) {
                        while (begin + len < length && measureGraphics.fontMetrics.charsWidth(chars, begin, len)
                                .also { lastWidth = it } + layoutX < layoutWidth
                        ) {
                            len++
                        }
                        if (lastWidth + layoutX > layoutWidth) {
                            len--
                        }
                        len = Integer.max(0, len)

                        val renderNode = TextRenderNode(
                            x + layoutX,
                            row.bottom() - lineHeight + baselineOffset,
                            line.substring(begin, begin + len),
                            params.messageTypeface,
                            colorForText(element, isReceivedMessage)
                        )
                        displayList.add(renderNode)

                        val textWidth = measureGraphics.fontMetrics.charsWidth(chars, begin, len)
                        if (begin + len < length) {
                            if (len == 0 && row.width == 0) {
                                // Unable to place any text. Exit
                                break
                            }
                            layoutY += row.height
                            layoutX = 0
                            row = Row(y + layoutY, lineHeight)
                        } else {
                            layoutX += textWidth
                            row.addElement(renderNode, textWidth, lineHeight)
                        }
                        begin += len
                        len = 0
                    }
                }
            }
            if (image != null) {
                // Render image
                val renderWidth = min(min(layoutWidth, image.getWidth(null)), params.imageMaxWidth)
                val renderHeight = (image.getHeight(null) * renderWidth.toFloat() / image.getWidth(null)).toInt()
                if (layoutWidth - row.width < renderWidth) {
                    // New row
                    layoutY += row.height
                    layoutX = 0
                    row = Row(y + layoutY, lineHeight)
                }
                val renderNode = ImageRenderNode(x + layoutX, row.bottom() - renderHeight, renderWidth, image)
                displayList.add(renderNode)
                row.addElement(renderNode, renderWidth, renderHeight)
                layoutX += renderWidth
            }
        }
        // Step 6. Create actual image and render
        val height =
            Integer.max(y + layoutY + row.height + params.commonMargin, params.commonMargin * 2 + params.avatarSize)
        val layout = GeneratedLayout(imageWidth, height)
        layout.addElement(BackgroundRenderNode(0, 0, params.multiMsgWidth, height, params.backgroundColor))
        val originStart = x
        val targetStart = imageWidth - avatarSize - layoutWidth - params.commonMargin
        val delta = targetStart - originStart
        if (avatar != null) {
            if (!isReceivedMessage) {
                // Translate the message elements
                displayList.forEach {
                    it.x += delta
                }
            }

            layout.addElement(
                AvatarRenderNode(
                    if (isReceivedMessage) params.commonMargin / 2 else imageWidth - params.commonMargin / 2 - params.avatarSize,
                    params.commonMargin / 2,
                    params.avatarSize,
                    params.roundAvatar,
                    avatar
                )
            )
        }
        if (nickname != null) {
            measureGraphics.font = params.nicknameTypeface
            layout.addElement(
                TextRenderNode(
                    if (isReceivedMessage) x + titleRegionWidth else imageWidth - avatarSize - params.commonMargin / 2,
                    params.commonMargin / 2 + measureGraphics.fontMetrics.height - measureGraphics.fontMetrics.descent,
                    nickname,
                    params.nicknameTypeface,
                    params.nicknameColor, !isReceivedMessage
                )
            )
        }
        if (title != null) {
            layout.addElement(
                TitleRenderNode(
                    if (isReceivedMessage) x else imageWidth - avatarSize - params.commonMargin - nicknameWidth,
                    params.commonMargin / 2,
                    params.titleBadgePadding,
                    title,
                    isReceivedMessage,
                    params.titleTypeface,
                    params.titleTextColor,
                    params.titleBadgeColor
                )
            )
        }

        val balloonX = if (isReceivedMessage) x else x + delta
        val layoutHeight = layoutY + row.height
        val v = params.commonMargin
        layout.addElement(
            MsgBalloonRenderNode(
                balloonX - v / 2, y - v / 2, layoutWidth + v, layoutHeight + v, v / 2,
                if (isReceivedMessage) params.balloonReceiveColor else params.balloonSendColor
            )
        )
        displayList.forEach {
            layout.addElement(it)
        }
        return layout
    }

    private fun colorForText(msg: SingleMessage, isReceivedMessage: Boolean): Color {
        return if (msg is PlainText) (if (isReceivedMessage) params.receivedMessageTextColor else params.sentMessageTextColor) else params.atTextColor
    }

    private fun Graphics2D.enableAntialias() {
        setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }

    private fun isMessageDisplayedAsText(msg: SingleMessage): Boolean {
        return when (msg) {
            is MiraiImage, is Face -> false
            else -> true
        }
    }

    private fun collectMessageSegments(message: Message): List<SingleMessage> {
        val list = mutableListOf<SingleMessage>()
        if (message is MessageChain) {
            list.addAll(message)
        } else if (message is SingleMessage) {
            // Metadata is ignored
            if (message !is MessageMetadata)
                list.add(message)
        }
        return list
    }

}