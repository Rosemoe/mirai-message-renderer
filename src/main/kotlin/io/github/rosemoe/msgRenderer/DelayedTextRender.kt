package io.github.rosemoe.msgRenderer

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

class DelayedTextRender(x: Int, baseline: Int, val text: String, private val typeface: Font, val color : Color = Color.BLACK) : DelayedElementRender(x, baseline) {

    override fun renderInto(g: Graphics2D) {
        if (g.font != typeface) {
            g.font = typeface
        }
        g.color = color
        g.drawString(text, x, y)
    }

}