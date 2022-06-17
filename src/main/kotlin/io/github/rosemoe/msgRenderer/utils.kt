package io.github.rosemoe.msgRenderer

import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage

fun defaultFont() : Font {
    GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts.forEach {
        if (it.name == "Microsoft Yahei UI") {
            return it
        }
    }
    return BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).createGraphics().font
}
