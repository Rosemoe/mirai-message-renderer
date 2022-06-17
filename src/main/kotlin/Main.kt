import io.github.rosemoe.msgRenderer.DataProvider
import io.github.rosemoe.msgRenderer.MessageRenderer
import io.github.rosemoe.msgRenderer.RenderParams
import net.mamoe.mirai.message.data.*
import java.awt.Image
import java.awt.image.RenderedImage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO
import net.mamoe.mirai.message.data.Image as MiraiImage

fun main(args: Array<String>) {
    val image = MessageRenderer().renderMessage(
        messageChainOf(
            PlainText("Test"),
            PlainText("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试"),
            At(123456),
            AtAll,
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B6}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"),
            PlainText("ceshi!!!")
        ),
        object : DataProvider {
            val members = mapOf(
                123456L to "Deleted Account"
            )
            val images = mutableMapOf<String, Image?>()

            override fun getImageForMessage(image: net.mamoe.mirai.message.data.Image): Image? {
                return images.computeIfAbsent(image.imageId) { id ->
                    ImageIO.read(File(id))
                }
            }

            override fun getImageForFace(face: Face): Image? {
                return null
            }

            override fun getTextForAt(at: At): String {
                return "@${members[at.target] ?: at.target} "
            }

        }, ImageIO.read(File("avatar.jpg")), "RosemoeΔ"
    )
    ImageIO.write(
        image as RenderedImage,
        "png",
        FileOutputStream(File("out.png").also { if (!it.exists()) it.createNewFile() })
    )
}