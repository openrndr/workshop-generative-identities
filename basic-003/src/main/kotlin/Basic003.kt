package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.shape.contour

/**
 * OPENRNDR Basics 003
 * -------------------
 * Constructs a contour, also uses sub() to get a sub contour
 */
class Basic003 : Program() {
    override fun draw() {
        drawer.background(ColorRGBa.PINK)

        val contour = contour {
            moveTo(Vector2(100.0, 100.0))
            lineTo(Vector2(200.0, 100.0))
            lineTo(Vector2(200.0,200.0))
            lineTo(Vector2(100.0, 200.0))
            lineTo(Vector2(100.0, 100.0))
            close()
        }

        drawer.stroke = ColorRGBa.BLACK
        drawer.fill = null
        drawer.contour(contour)

        drawer.translate(200.0, 0.0)
        drawer.contour(contour.sub(seconds, seconds + 0.1))

    }
}

fun main(args: Array<String>) {
    application(Basic003(), configuration {
        width = 1280
        height = 720
    })
}