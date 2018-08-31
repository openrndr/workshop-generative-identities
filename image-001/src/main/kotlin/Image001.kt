import org.openrndr.Program
import org.openrndr.application
import org.openrndr.configuration
import org.openrndr.draw.ColorBuffer
import org.openrndr.math.Vector2
import org.openrndr.math.map
import org.openrndr.shape.Rectangle


lateinit var image: ColorBuffer

class Image001 : Program() {

    override fun setup() {
        super.setup()

        image = ColorBuffer.fromFile("data/IMG_4124.JPG")
    }

    override fun draw() {
        super.draw()

        val blockSize = 128.0

        val amount = (width / blockSize).toInt()

        for(x in 0..amount) {
            for(y in 0..amount) {

                    val pos1 = mouse.position
                    val pos2 = Vector2(x * blockSize , y * blockSize)

                    val distance = Math.sqrt((pos1.x-pos2.x)*(pos1.x-pos2.x) + (pos1.y-pos2.y)*(pos1.y-pos2.y))

                    drawer.image(image,
                            Rectangle(Vector2(x * blockSize + (distance), y * blockSize + (distance) ), blockSize, blockSize),
                            Rectangle(Vector2(x * blockSize , y * blockSize ), blockSize, blockSize))
                }
            }
        }

}

fun main(args: Array<String>) {
    application(Image001(), configuration {
        width = 1024
        height = 1024
    })
}

