package io.github.rosemoe.msgRenderer

import java.awt.Graphics2D

abstract class DelayedElementRender(var x: Int, var y: Int) {

    abstract fun renderInto(g: Graphics2D)

}