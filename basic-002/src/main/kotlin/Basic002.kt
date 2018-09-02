package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.*
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import org.openrndr.text.Writer
import java.io.File

/**
 * OPENRNDR Basics 002
 * -------------------
 * Loads a font and draws a text
 */
class Basic002 : Program() {
    override fun draw() {
        drawer.background(ColorRGBa.PINK)

        drawer.fill = ColorRGBa.BLACK
        drawer.fontMap = FontImageMap.fromUrl("file:data/IBMPlexMono-Bold.ttf", 32.0)

        val w = Writer(drawer)
        w.box = Rectangle(100.0, 100.0, width-200.0, height-200.0)
        for (i in 0 until 10) {
            w.newLine()
            w.text("OPENRNDR")
        }
    }
}

fun main(args: Array<String>) {
    application(Basic002(), configuration {
        width = 1280
        height = 720
    })
}