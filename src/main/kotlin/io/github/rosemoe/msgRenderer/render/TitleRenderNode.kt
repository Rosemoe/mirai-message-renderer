package io.github.rosemoe.msgRenderer.render

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class TitleRenderNode(
    x: Int,
    y: Int,// Top of the string
    val wrappingPadding: Int,
    val title: String,
    val ltr: Boolean,
    val font: Font,
    val textColor: Color,
    val badgeColor: Color
) : ElementRenderNode(x, y) {

    override fun renderInto(g: Graphics2D) {
        g.font = font
        val bounds = g.fontMetrics.getStringBounds(title, g)
        val stringWidth = bounds.width.toInt()
        val stringHeight = g.fontMetrics.height
        val offset = if (ltr) x else x - stringWidth - wrappingPadding * 2
        val baseline = y + g.fontMetrics.height - g.fontMetrics.descent
        // Draw rect
        g.color = badgeColor
        g.fillRoundRect(offset - wrappingPadding * 2, y - wrappingPadding, stringWidth + wrappingPadding * 4, stringHeight + wrappingPadding * 2, wrappingPadding, wrappingPadding)
        g.color = textColor
        g.drawString(title, offset, baseline)
    }

}