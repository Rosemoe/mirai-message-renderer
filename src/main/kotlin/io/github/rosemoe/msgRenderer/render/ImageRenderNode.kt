package io.github.rosemoe.msgRenderer.render

import java.awt.Graphics2D
import java.awt.Image

class ImageRenderNode(x: Int, y: Int, val width: Int, val image: Image) : ElementRenderNode(x, y) {

    override fun renderInto(g: Graphics2D) {
        if (width != image.getWidth(null)) {
            val scaled = image.getScaledInstance(width, (image.getHeight(null) * width.toFloat() / image.getWidth(null)).toInt(), Image.SCALE_SMOOTH)
            g.drawImage(scaled, x, y, null)
        } else {
            g.drawImage(
                image,
                x,
                y,
                null
            )
        }
    }

}