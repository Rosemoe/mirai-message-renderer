import io.github.rosemoe.msgRenderer.*
import net.mamoe.mirai.message.data.*
import java.awt.Image
import java.awt.image.RenderedImage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.random.nextInt
import net.mamoe.mirai.message.data.Image as MiraiImage

fun main(args: Array<String>) {
    val avatar = ImageIO.read(File("avatar.jpg"))
    val localImageId1 = "{00000000-0000-0000-0000-101F1EEBF5B5}.png"
    val localImageId2 = "{00000000-0000-0000-0000-101F1EEBF5B6}.png"

    // Build message info list by DSL
    val messageInfos =
        buildMessageInfo {
            "Rosemoe" titled "Rose" avatar avatar send messageChainOf(
                PlainText("Test"),
                PlainText("测试测试测试测试测试测试测试测试测试测试测试\n测试测试测试测试测试测试测试测试测试测试测试测试测试"),
                At(123456),
                AtAll,
                MiraiImage(localImageId1),
                MiraiImage(localImageId1),
                MiraiImage(localImageId1),
                MiraiImage(localImageId1),
                MiraiImage(localImageId2),
                PlainText("ceshi!!!")
            )
            "RosemoeX" titled "RoseX" avatar avatar send "Awesome!\nWhere did you get the image?" at right
        }


    // Build message info normally
    val botInfo = UserInfo(avatar, "Rosemoe?", "Bot")
    for (i in 1..5) {
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
                builder.add(At(Random.nextLong(10000L, 40000000000L)))
            }
        }
        messageInfos.add(MessageInfo(builder.build(), botInfo, Random.nextBoolean()))
    }

    // Render image
    val image = MessageRenderer().renderMessages(
        messageInfos,
        object : DataProvider {
            val members = mapOf(
                123456L to "Deleted Account"
            )
            val images = mutableMapOf<String, Image?>()

            override fun getImageForMessage(image: MiraiImage): Image? {
                return images.computeIfAbsent(image.imageId) { id ->
                    val file = File(id)
                    if (file.exists() && file.isFile) {
                        ImageIO.read(file)
                    } else {
                        null
                    }
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

    // Save to file
    ImageIO.write(
        image as RenderedImage,
        "png",
        FileOutputStream(File("out.png").also { if (!it.exists()) it.createNewFile() })
    )
}