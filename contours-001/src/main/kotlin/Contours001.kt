package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.svg.loadSVG
import java.io.File

class Contours001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())


        drawFunc = {
            drawer.stroke = ColorRGBa.PINK
            for (shape in composition.findShapes()) {
                for (contour in shape.shape.contours) {
                    drawer.contour(contour.sub(Math.cos(seconds)*0.5+0.5,1.0))

                }
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Contours001(), configuration {
        width = 1280
        height = 720
    })
}