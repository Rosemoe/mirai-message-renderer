package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import kotlin.math.min
import net.mamoe.mirai.message.data.Image as MiraiImage

class MessageRenderer(private val params: RenderParams = RenderParams()) {

    fun renderMessage(message: String, contact: Contact? = null, dataProvider: DataProvider): Image {
        return renderMessage(MiraiCode.deserializeMiraiCode(message, contact), dataProvider)
    }

    fun renderMessage(
        message: Message,
        dataProvider: DataProvider,
        avatar: Image? = null,
        nickname: String? = null
    ): Image {
        // Step 1. Collect elements
        val elements = collectMessageSegments(message)
        var x = 0
        var y = 0
        val avatarSize = if (avatar == null) 0 else params.avatarSize + params.commonMargin

        // Step 2. Measure elements & obtain images
        val maxLayoutWidth = params.widthLimit - avatarSize - params.commonMargin * 2
        var totalWidth = 0.0
        val blankImage = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
        val measureGraphics = blankImage.createGraphics()
        measureGraphics.enableAntialias()
        measureGraphics.font = params.messageTypeface
        for (element in elements) {
            if (isMessageDisplayedAsText(element)) {
                val text = if (element is At) dataProvider.getTextForAt(element) else element.contentToString()
                totalWidth += params.messageTypeface.getStringBounds(text, measureGraphics.fontRenderContext).width
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
                    totalWidth += params.messageTypeface.getStringBounds(text, measureGraphics.fontRenderContext).width
                } else {
                    totalWidth += Integer.min(image.getWidth(null), params.imageMaxWidth)
                }
            }
        }

        // Step 3. Determine the width of image
        val layoutWidth = min(maxLayoutWidth, totalWidth.toInt())
        val imageWidth = layoutWidth + params.commonMargin * 2 + avatarSize

        // Step 4. Measure the nickname

        val nicknameHeight = if (nickname == null) {
            0
        } else {
            measureGraphics.font = params.nicknameTypeface
            params.nicknameTypeface.getStringBounds(
                nickname,
                measureGraphics.fontRenderContext
            ).height.toInt() + params.commonMargin / 2
        }

        // Step 5. Determine the height of image as well the position of objects
        x = params.commonMargin + avatarSize
        y = params.commonMargin + nicknameHeight
        measureGraphics.font = params.messageTypeface
        val lineHeight = measureGraphics.fontMetrics.height
        val baselineOffset = lineHeight - measureGraphics.fontMetrics.descent
        val displayList = mutableListOf<DelayedElementRender>()

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
                var begin = 0
                var len = 0
                val length = text.length
                val chars = text.toCharArray()
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
                    len = Integer.max(1, len)

                    val renderNode = DelayedTextRender(
                        x + layoutX,
                        row.bottom() - lineHeight + baselineOffset,
                        text.substring(begin, begin + len),
                        params.messageTypeface,
                        colorForText(element)
                    )
                    displayList.add(renderNode)

                    val textWidth = measureGraphics.fontMetrics.charsWidth(chars, begin, len)
                    if (begin + len < length) {
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
                val renderNode = DelayedImageRender(x + layoutX, row.bottom() - renderHeight, renderWidth, image)
                displayList.add(renderNode)
                row.addElement(renderNode, renderWidth, renderHeight)
                layoutX += renderWidth
            }
        }
        // Step 6. Create actual image and render
        val height =
            Integer.max(y + layoutY + row.height + params.commonMargin, params.commonMargin * 2 + params.avatarSize)
        val image = BufferedImage(imageWidth, height, BufferedImage.TYPE_4BYTE_ABGR)
        val g = image.createGraphics()
        g.enableAntialias()
        g.color = Color.WHITE
        g.fillRect(0, 0, imageWidth, height)
        g.color = Color.BLACK
        if (avatar != null) {
            g.drawImage(
                avatar,
                params.commonMargin / 2,
                params.commonMargin / 2,
                params.avatarSize,
                params.avatarSize,
                null
            )
            if (params.roundAvatar) {
                val area = Area(
                    Rectangle(
                        params.commonMargin / 2,
                        params.commonMargin / 2,
                        params.avatarSize,
                        params.avatarSize
                    )
                )
                val radius = params.avatarSize
                val round = RoundRectangle2D.Double(
                    params.commonMargin / 2.0,
                    params.commonMargin / 2.0,
                    params.avatarSize.toDouble(),
                    params.avatarSize.toDouble(),
                    radius.toDouble(),
                    radius.toDouble()
                )
                area.subtract(Area(round))
                g.color = Color.WHITE
                g.fill(area)
            }
        }
        if (nickname != null) {
            g.font = params.nicknameTypeface
            g.color = Color.BLACK
            g.drawString(nickname, x, 8 + g.fontMetrics.height - g.fontMetrics.descent)
        }
        val v = params.commonMargin
        g.color = Color(0xde, 0xde, 0xde)
        g.fillRoundRect(x - v / 2, y - v / 2, layoutWidth + v * 2 / 3, layoutY + row.height + v, v / 2, v / 2)
        g.color = Color.BLACK
        displayList.forEach {
            it.renderInto(g)
        }
        return image
    }

    private fun colorForText(msg: SingleMessage): Color {
        return if (msg is PlainText) Color.BLACK else Color.BLUE
    }

    private fun Graphics2D.enableAntialias() {
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