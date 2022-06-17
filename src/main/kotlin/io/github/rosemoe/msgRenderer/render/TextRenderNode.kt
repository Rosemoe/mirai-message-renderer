package io.github.rosemoe.msgRenderer.render

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class TextRenderNode(x: Int, baseline: Int, val text: String, private val typeface: Font, val color : Color, val alignmentToRight: Boolean = false) : ElementRenderNode(x, baseline) {

    override fun renderInto(g: Graphics2D) {
        if (g.font != typeface) {
            g.font = typeface
        }
        g.color = color
        if (alignmentToRight) {
            val width = g.fontMetrics.stringWidth(text)
            g.drawString(text, x - width, y)
        } else {
            g.drawString(text, x, y)
        }
    }

}