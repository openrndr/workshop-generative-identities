package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import org.w3c.dom.css.Rect
import java.io.File

class MovieDots001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())
        val videoPlayer = FFMPEGVideoPlayer.fromFile("data/pp-01.mp4")

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
                drawer.translate(points[pointIndex%points.size])
                drawer.image(videoTarget.colorBuffer(0), videoTarget.colorBuffer(0).bounds,
                        Rectangle(-16.0, -16.0, 32.0, 32.0))
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
    application(MovieDots001(), configuration {
        width = 1280
        height = 720
    })
}