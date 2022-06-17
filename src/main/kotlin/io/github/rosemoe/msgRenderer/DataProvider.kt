package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

/**
 * Provide data for rendering.
 * The object is expected to serve for only one render action
 */
interface DataProvider {

    /**
     * Get [java.awt.Image] object for the given [Image] in message.
     * The returned value maybe null and the image will be displayed as text.
     *
     * In order to optimize performance, you are expected to cache the returned value. Because
     * the same image may be requested several times in one rendering action.
     */
    fun getImageForMessage(image: Image) : java.awt.Image?

    /**
     * Get [java.awt.Image] object for the given [Face] in message.
     * The returned value maybe null and the face will be displayed as text.
     *
     * In order to optimize performance, you are expected to cache the returned value. Because
     * the same image may be requested several times in one rendering action. Moreover, you may
     * always cache the images of face in memory for all rendering actions.
     */
    fun getImageForFace(face: Face) : java.awt.Image?

    /**
     * Get the text to display for the given [At] object
     */
    fun getTextForAt(at: At) : String

}