package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.svg.loadSVG
import java.io.File

class Contours003 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())


        drawFunc = {
            drawer.background(ColorRGBa.WHITE.shade(0.1))
            drawer.strokeWeight = 5.0

            drawer.translate(-50.0, -50.0)

            for (shape in composition.findShapes()) {
                for (contour in shape.shape.contours) {
                    drawer.pushTransforms()
                    var i = 0
                    drawer.stroke = ColorRGBa.GRAY
                    drawer.contour(contour.sub(Math.cos(seconds+i) * 0.5 + 0.5-0.2, Math.cos(seconds+i) * 0.5 + 0.5))
                    drawer.stroke = ColorRGBa.PINK
                    drawer.contour(contour.sub(Math.cos(seconds+i) * 0.5 + 0.5, Math.cos(seconds+i) * 0.5 + 0.5 + 0.2))
                    drawer.stroke = ColorRGBa.WHITE
                    drawer.contour(contour.sub(Math.cos(seconds+i) * 0.5 + 0.5+0.2, Math.cos(seconds+i) * 0.5 + 0.5 + 0.4))
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
    application(Contours003(), configuration {
        width = 1280
        height = 720
    })
}