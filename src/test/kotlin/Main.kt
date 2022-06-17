import io.github.rosemoe.msgRenderer.DataProvider
import io.github.rosemoe.msgRenderer.MessageInfo
import io.github.rosemoe.msgRenderer.MessageRenderer
import io.github.rosemoe.msgRenderer.UserInfo
import net.mamoe.mirai.message.data.*
import java.awt.Image
import java.awt.image.RenderedImage
import java.io.File
import java.io.FileOutputStream
import java.lang.Math.abs
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.random.nextInt
import net.mamoe.mirai.message.data.Image as MiraiImage

fun main(args: Array<String>) {
    val avatar = ImageIO.read(File("avatar.jpg"))
    val msgs = arrayListOf(
        MessageInfo(
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
                PlainText("ceshi!!!")
            ), UserInfo(avatar, "Rosemoe", "Rose")
        ),
        MessageInfo(messageChainOf(PlainText("Awesome!")), UserInfo(avatar, "RosemoeX", "Rose2"), false)
    )
    val fakeUser = UserInfo(avatar, "Rosemoe?", "Bot")
    for (i in 1..10) {
        // Generate messages
        val builder = MessageChainBuilder()
        for (j in 1..10) {
            // 5 Elements
            val v = Random.nextInt(0..50)
            if (v < 35) {
                // Text
                builder.add(UUID.randomUUID().toString())
            } else if (v < 47) {
                // Image
                builder.add(MiraiImage("{00000000-0000-0000-0000-101F1EEBF5B5}.png"))
            } else {
                // At
                builder.add(At(abs(Random.nextLong(Long.MAX_VALUE))))
            }
        }
        msgs.add(MessageInfo(builder.build(), fakeUser, Random.nextBoolean()))
    }
    val image = MessageRenderer().renderMessages(
        msgs,
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

        }
    )
    ImageIO.write(
        image as RenderedImage,
        "png",
        FileOutputStream(File("out.png").also { if (!it.exists()) it.createNewFile() })
    )
}