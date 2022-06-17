package io.github.rosemoe.msgRenderer

import io.github.rosemoe.msgRenderer.render.ElementRenderNode

/**
 * Row object for managing render nodes on a single line
 */
internal class Row(val top: Int, textLineHeight: Int) {

    var height = textLineHeight

    val elements = mutableListOf<ElementRenderNode>()

    var width = 0

    fun bottom() : Int {
        return top + height
    }

    fun updateHeight(newHeight: Int) {
        val delta = newHeight - height
        elements.forEach {
            it.y += delta
        }
        height = newHeight
    }

    fun addElement(element: ElementRenderNode, elementWidth: Int, elementHeight: Int) {
        elements.add(element)
        width += elementWidth
        if (elementHeight > height) {
            updateHeight(elementHeight)
        }
    }

}