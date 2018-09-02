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
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import java.io.File

/**
 * OPENRNDR Basics 001
 * -------------------
 * Draws a line and circles, shows the basic principles of stroke weight, stroke and fill and background.
 */
class Basic001 : Program() {
    override fun draw() {
        drawer.background(ColorRGBa.PINK)
        drawer.stroke = ColorRGBa.BLACK
        drawer.strokeWeight = 2.0

        drawer.lineSegment(40.0, 40.0, width - 40.0, height - 40.0)

        drawer.fill = null
        drawer.circle(Vector2(200.0, 200.0), 50.0)

        drawer.fill = ColorRGBa.BLACK
        drawer.circle(Vector2(300.0, 200.0), 50.0)
    }
}

fun main(args: Array<String>) {
    application(Basic001(), configuration {
        width = 1280
        height = 720
    })
}