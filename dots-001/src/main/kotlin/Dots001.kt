package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.configuration
import org.openrndr.svg.loadSVG
import java.io.File

class Dots001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())

        drawFunc = {
            for (shape in composition.findShapes()) {
                for (contour in shape.shape.contours) {
                    drawer.circles(contour.equidistantPositions((contour.length / 5.0).toInt()), 5.0)
                }
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Dots001(), configuration {
        width = 1280
        height = 720
    })
}