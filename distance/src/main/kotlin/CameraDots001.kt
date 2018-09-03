package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.draw.shadeStyle
import org.openrndr.draw.tint
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import java.io.File

class CameraDots001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())
        val videoPlayer = FFMPEGVideoPlayer.fromDevice()

        val points = composition.findShapes().map { it.shape }.flatMap {
            it.contours.flatMap {
                it.equidistantPositions((it.length/5.0).toInt())
            }
        }
        videoPlayer.start()
        videoPlayer.next()

        val videoTarget = renderTarget(videoPlayer.width, videoPlayer.height) {
            colorBuffer()
        }

        val screen = renderTarget(width, height) {
            colorBuffer()
        }

        drawer.isolatedWithTarget(screen) {
            drawer.background(ColorRGBa.BLACK)
        }

        var pointIndex = 0
        drawFunc = {
            drawer.isolatedWithTarget(videoTarget) {
                ortho(videoTarget)
                videoPlayer.next()
                videoPlayer.draw(drawer)
            }

            videoTarget.colorBuffer(0).generateMipmaps()
            drawer.isolatedWithTarget(screen) {
                drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(0.5))
                drawer.translate(points[pointIndex%points.size])
                drawer.rotate(pointIndex*0.0)

                drawer.shadeStyle = shadeStyle {
                    fragmentTransform = """
                        float l = smoothstep(0.5, 0.4, length(va_texCoord0-vec2(0.5, 0.5)));
                        x_fill.a *= l;
                    """.trimIndent()
                }

                drawer.image(videoTarget.colorBuffer(0), videoTarget.colorBuffer(0).bounds,
                        Rectangle(-32.0, -32.0, 64.0, 64.0))

                pointIndex++
            }
            drawer.image(screen.colorBuffer(0))
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(CameraDots001(), configuration {
        width = 1280
        height = 720
    })
}