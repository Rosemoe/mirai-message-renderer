package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

interface DataProvider {

    fun getImageForMessage(image: Image) : java.awt.Image?

    fun getImageForFace(face: Face) : java.awt.Image?

    fun getTextForAt(at: At) : String

}