package io.github.rosemoe.msgRenderer

import io.github.rosemoe.msgRenderer.render.BackgroundRenderNode
import io.github.rosemoe.msgRenderer.render.ElementRenderNode
import java.awt.Graphics2D

/**
 * Generated layout for a single message piece
 */
internal class GeneratedLayout(val width: Int, val height: Int, var x: Int = 0, var y: Int = 0) {

    private val elements = mutableListOf<ElementRenderNode>()

    fun addElement(element: ElementRenderNode) {
        elements.add(element)
    }

    fun translate(dx: Int, dy: Int) {
        x += dx
        y += dy
        elements.forEach {
            it.y += dy
            // Background should not be translated
            if (it !is BackgroundRenderNode) {
                it.x += dx
            }
        }
    }

    fun renderInto(g: Graphics2D) {
        elements.forEach {
            it.renderInto(g)
        }
    }

}