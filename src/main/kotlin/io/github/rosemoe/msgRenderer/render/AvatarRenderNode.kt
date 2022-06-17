package io.github.rosemoe.msgRenderer.render

import java.awt.Graphics2D
import java.awt.Image
import java.awt.geom.RoundRectangle2D

class AvatarRenderNode(x: Int, y: Int, val size: Int, val round: Boolean, val avatar: Image) : ElementRenderNode(x, y) {


    override fun renderInto(g: Graphics2D) {
        val sizeF = size.toFloat()
        if (round) {
            val clipArea = RoundRectangle2D.Float(x.toFloat(), y.toFloat(), sizeF, sizeF, sizeF, sizeF)
            g.clip = clipArea
        }
        val scaled = avatar.getScaledInstance(size, size, Image.SCALE_SMOOTH)
        g.drawImage(scaled, x, y, null)
        g.clip = null
    }

}