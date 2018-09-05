package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.ColorBuffer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.ShapeContour
import studio.rndr.distance.FastestDirectionalFieldBuilder
import java.io.File

class Map003 : Program() {
    var drawFunc = {}

    override fun setup() {
        val map = OSMap(File("data/corato.osm"))
        val scale = 0.5

        val mask = ColorBuffer.fromFile("data/logo-01.png")
        mask.shadow.download()

        val field = FastestDirectionalFieldBuilder().build(mask.shadow) {
            if ((it.r + it.g + it.b) / 3.0 < 0.5) 0 else 1
        }

        // -- prepare all the roads for drawing. this is somewhat more noisy than needed.
        val roadsDraw = map.waysWithTag("highway").flatMap {
            it.pairs.flatMap {
                it.toList().map {
                    it * scale
                }.asSequence()
            }
        }.toList()

        // -- prepare roads as contours
        val roadContours = map.waysWithTag("highway").map {
            ShapeContour.fromPoints(it.points().map { it * scale }, false)
        }.toList()

        drawFunc = {
            drawer.background(ColorRGBa.PINK)
            drawer.translate(width/2.0, height/2.0)
            drawer.rotate(-90.0)
            drawer.translate(map.bounds.center * -scale)
            drawer.stroke = ColorRGBa.WHITE
            drawer.lineSegments(roadsDraw)

            drawer.stroke = null
            drawer.fill = ColorRGBa.BLACK

            for (i in 0 until 3) {
                val circles = roadContours.mapIndexed { index, it ->
                    val position = it.position(Math.cos(seconds + index + i) * 0.5 + 0.5)
                    val maskPosition = (drawer.view * drawer.model * position.xy01).xyz.xy

                    val intensity = if (maskPosition.x >= 0 && maskPosition.x < mask.width && maskPosition.y >= 0 && maskPosition.y < mask.height) {
                        val c = mask.shadow.read(maskPosition.x.toInt(), maskPosition.y.toInt())
                        (c.r + c.g + c.b) / 3.0
                    } else {
                        1.0
                    }

                    val distance = if (maskPosition.x >= 0 && maskPosition.x < mask.width && maskPosition.y >= 0 && maskPosition.y < mask.height) {
                        field.distance(maskPosition.x.toInt(), maskPosition.y.toInt())
                    } else {
                        Vector2(0.0, 0.0)
                    }
                    val length = distance.length

                    Circle(position, 5.0 + (1.0 - intensity) * length)
                }
                drawer.circles(circles)
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Map003(), configuration {
        width = 1280
        height = 720
    })
}