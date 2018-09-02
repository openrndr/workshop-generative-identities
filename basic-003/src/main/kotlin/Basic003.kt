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
 * OPENRNDR Basics 003
 * -------------------
 * Constructs a contour
 */
class Basic003 : Program() {
    override fun draw() {
        drawer.background(ColorRGBa.PINK)

    }
}

fun main(args: Array<String>) {
    application(Basic003(), configuration {
        width = 1280
        height = 720
    })
}