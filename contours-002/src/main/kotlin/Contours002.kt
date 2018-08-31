package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.svg.loadSVG
import java.io.File

class Contours002 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())
        drawFunc = {

            for (shape in composition.findShapes()) {
                for (contour in shape.shape.contours) {
                    drawer.stroke = ColorRGBa.PINK
                    drawer.contour(contour.sub(Math.cos(seconds)*0.5+0.5,1.0))
                    drawer.pushTransforms()
                    drawer.translate(10.0, 10.0)
                    drawer.contour(contour.sub(Math.sin(seconds)*0.5+0.5,1.0))
                    drawer.popTransforms()

                }
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Contours002(), configuration {
        width = 1280
        height = 720
    })
}