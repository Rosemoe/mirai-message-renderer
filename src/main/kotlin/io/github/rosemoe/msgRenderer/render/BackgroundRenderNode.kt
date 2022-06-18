package io.github.rosemoe.msgRenderer.render

import java.awt.Color
import java.awt.Graphics2D

class BackgroundRenderNode(x: Int, y: Int, val width: Int, val height: Int, val color: Color) : ElementRenderNode(x, y) {

    override fun renderInto(g: Graphics2D) {
        g.color = color
        g.fillRect(x, y, width, height)
    }

    override fun movableX() = false

}