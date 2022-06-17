package io.github.rosemoe.msgRenderer.render

import java.awt.Graphics2D

/**
 * Common interface for computed element rendering action
 */
abstract class ElementRenderNode(var x: Int, var y: Int) {

    abstract fun renderInto(g: Graphics2D)

}