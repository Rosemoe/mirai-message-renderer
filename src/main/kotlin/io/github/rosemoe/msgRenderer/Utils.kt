package io.github.rosemoe.msgRenderer

import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage

val defaultFont: Font by lazy {
    val font = GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts.find { it.name == "Microsoft Yahei UI" }
    font ?: BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).createGraphics().font
}
