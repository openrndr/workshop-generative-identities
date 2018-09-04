package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import org.openrndr.svg.loadSVG
import java.io.File

class Letters001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())


        val shapes = mutableListOf<Shape>()
        for (shape in composition.findShapes()) {
            shapes.add(shape.shape.map {
                it.sampleEquidistant((it.length/5.0).toInt())
            })
        }

        drawFunc = {
            drawer.stroke = ColorRGBa.WHITE

            for(shape in shapes) {
                val reshaped = shape.map { contour ->
                    val points = (0 until contour.segments.size).map {
                        val position = contour.position((1.0 * it) / contour.segments.size)
                        val normal = contour.normal((1.0 * it) / contour.segments.size)
                        position + normal * Math.cos(seconds * 10.0 + position.y) * 5.0
                    }
                    ShapeContour.fromPoints(points, contour.closed)
                }
                drawer.shape(reshaped)
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Letters001(), configuration {
        width = 1280
        height = 720
    })
}