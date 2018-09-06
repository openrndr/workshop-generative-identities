package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.panel.controlManager
import org.openrndr.panel.elements.Range
import org.openrndr.panel.elements.slider
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import org.openrndr.svg.loadSVG
import java.io.File

class Tool001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val composition = loadSVG(File("data/logo.svg").readText())
        val shapes = mutableListOf<Shape>()
        for (shape in composition.findShapes()) {
            shapes.add(shape.shape.map {
                it.sampleEquidistant((it.length/5.0).toInt())
            })
        }


        var amplitude = 0.0
        var freq = 0.0
        var vfreq = 0.0
        var hfreq = 0.0
        val cm = controlManager {
            layout {
                slider {
                    label = "crisi"
                    range = Range(0.0, 1.0)
                    events.valueChanged.subscribe {
                        amplitude = it.newValue
                    }
                }
                slider {
                    label = "crisi freq"
                    range = Range(0.0, 1.0)
                    events.valueChanged.subscribe {
                        freq = it.newValue
                    }
                }
                slider {
                    label = "crisi vertical freq"
                    range = Range(0.0, 1.0)
                    events.valueChanged.subscribe {
                        vfreq = it.newValue
                    }
                }
                slider {
                    label = "crisi horizontal freq"
                    range = Range(0.0, 1.0)
                    events.valueChanged.subscribe {
                        hfreq = it.newValue
                    }
                }

            }
        }
        extend(cm) // <- this registers the control manager as a Program extension


        drawFunc = {
            drawer.stroke = ColorRGBa.WHITE

            for(shape in shapes) {
                val reshaped = shape.map { contour ->
                    val points = (0 until contour.segments.size).map {
                        val position = contour.position((1.0 * it) / contour.segments.size)
                        val normal = contour.normal((1.0 * it) / contour.segments.size)
                        position     +
                                normal * Math.cos(seconds * 1.0 + (position.x + position.y) * freq) * amplitude * 20.0 +
                                Vector2(1.0, 0.0) * Math.cos(seconds * 1.0 + position.y * vfreq) * amplitude * 80.0 +
                                Vector2(0.0, 1.0) * Math.cos(seconds * 1.0 + position.x * hfreq) * amplitude * 80.0
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
    application(Tool001(), configuration {
        width = 1280
        height = 720
    })
}