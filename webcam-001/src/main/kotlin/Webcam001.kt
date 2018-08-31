import org.openrndr.Program
import org.openrndr.application
import org.openrndr.configuration
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.RenderTarget
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.math.Vector2
import org.openrndr.math.map
import org.openrndr.shape.Rectangle


class Webcam001 : Program() {

    lateinit var videoPlayer: FFMPEGVideoPlayer
    var frame: RenderTarget? = null

    override fun setup() {
        super.setup()

        videoPlayer = FFMPEGVideoPlayer.fromDevice()
        videoPlayer.start()
    }

    override fun draw() {
        super.draw()

        val blockSize = 64.0

        videoPlayer.next()

        if (videoPlayer.width != 0 && videoPlayer.height != 0) {
            frame = renderTarget(width, height) {
                colorBuffer()
            }
        }

        drawer.isolatedWithTarget(frame!!) {
            ortho(frame!!)
            videoPlayer.draw(drawer)
        }

        val amount = (width / blockSize).toInt()

        val pos1 = mouse.position
        val pos2 = Vector2(width/2.0, height/2.0)

        val distance = Math.sqrt((pos1.x-pos2.x)*(pos1.x-pos2.x) + (pos1.y-pos2.y)*(pos1.y-pos2.y))

        for(x in 0..amount) {
            for(y in 0..amount) {

                    var mapping = map(0.0, 10.0, 0.0, -1.0, distance)

                    drawer.image(frame!!.colorBuffer(0),
                            Rectangle(Vector2(x * blockSize + (mapping), y * blockSize + (mapping) ), blockSize - (mapping * 2.0) , blockSize - (mapping * 2.0) ),
                            Rectangle(Vector2(x * blockSize , y * blockSize ), blockSize, blockSize))
                }
            }
        }

}

fun main(args: Array<String>) {
    application(Webcam001(), configuration {
        width = 1280
        height = 720
    })
}
