package io.github.rosemoe.msgRenderer

import net.mamoe.mirai.message.data.Face
import java.awt.Image
import java.io.InputStreamReader
import javax.imageio.ImageIO

/**
 * Class for managing QQ faces
 */
object FaceStore {

    private var size = 40

    private val faces by lazy {
        val map = HashMap<Int, Image?>()
        val cl = FaceStore::class.java.classLoader
        InputStreamReader(cl.getResourceAsStream("FaceStore/index.data")!!).readText().split(",").forEach {
            val fileName = "FaceStore/$it.png"
            val image = ImageIO.read(cl.getResourceAsStream(fileName))
            if (image.getWidth(null) != size) {
                map[it.toInt()] = image.getScaledInstance(size, size, Image.SCALE_SMOOTH)
            } else {
                map[it.toInt()] = image
            }
        }
        map
    }

    fun loadFace(face: Face) = loadFace(face.id)

    @Synchronized
    fun loadFace(faceId: Int): Image? {
        return faces[faceId]
    }

    @Synchronized
    fun setFaceImage(faceId: Int, image: Image?) {
        faces[faceId] = image
        if (image != null && image.getWidth(null) != size) {
            faces[faceId] = image.getScaledInstance(size, size, Image.SCALE_SMOOTH)
        }
    }

    @Synchronized
    fun setImageSize(size: Int) {
        this.size = size
        faces.entries.parallelStream().forEach {
            val image = it.value
            if (image != null && image.getWidth(null) != size) {
                it.setValue(image.getScaledInstance(size, size, Image.SCALE_SMOOTH))
            }
        }
    }


}