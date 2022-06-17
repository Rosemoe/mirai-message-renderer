package io.github.rosemoe.msgRenderer

class Row(val top: Int, val textLineHeight: Int) {

    var height = textLineHeight

    val elements = mutableListOf<DelayedElementRender>()

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

    fun addElement(element: DelayedElementRender, elementWidth: Int, elementHeight: Int) {
        elements.add(element)
        width += elementWidth
        if (elementHeight > height) {
            updateHeight(elementHeight)
        }
    }

}