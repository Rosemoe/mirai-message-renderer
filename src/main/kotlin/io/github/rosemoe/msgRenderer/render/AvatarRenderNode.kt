package io.github.rosemoe.msgRenderer.render

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.geom.RoundRectangle2D

class AvatarRenderNode(x: Int, y: Int, val size: Int, val round: Boolean, val avatar: Image) : ElementRenderNode(x, y) {


    override fun renderInto(g: Graphics2D) {
        val sizeF = size.toFloat()
        val backupComposite = g.composite
        if (round) {
            g.composite = AlphaComposite.Clear
            g.color = Color.WHITE
            g.fill(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), sizeF, sizeF, sizeF, sizeF))
            g.composite = AlphaComposite.DstAtop
        }
        val scaled = avatar.getScaledInstance(size, size, Image.SCALE_SMOOTH)
        g.drawImage(scaled, x, y, null)
        g.composite = backupComposite
    }

}