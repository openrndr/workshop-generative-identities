package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.svg.loadSVG
import java.io.File



class Dots002 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())

        drawFunc = {
            for (shape in composition.findShapes()) {
                for (contour in shape.shape.contours) {

                    drawer.circles(
                    contour.equidistantPositions((contour.length / 5.0).toInt()).mapIndexed { index, it ->
                        Circle(
                        it + Vector2(Math.cos(it.y+seconds*10.0), 0.0),
                                Math.cos(index*0.1+seconds)*2.5+10.0
                        )

                    })
                }
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Dots002(), configuration {
        width = 1280
        height = 720
    })
}