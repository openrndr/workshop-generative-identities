package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.ColorBufferShadow
import org.openrndr.draw.colorBuffer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.svg.loadSVG
import studio.rndr.distance.FastestDirectionalFieldBuilder
import java.io.File

class Flow003 : Program() {
    var drawFunc = {}

    override fun setup() {
        val image = ColorBuffer.fromFile("data/logo-01.png")
        var flip = false

        mouse.clicked.listen {
            flip = !flip
        }

        image.shadow.download()
        val field = FastestDirectionalFieldBuilder().build(image.shadow) {
            if ((it.r + it.g + it.b) / 3.0 < 0.5) 0 else 1
        }

        drawFunc = {
            drawer.strokeWeight = 2.0
            val points = mutableListOf<Vector2>()
            val factor = mouse.position.y / height
            val spacing = 16
            for (y in 0 until height step spacing) {
                for (x in 0 until width step spacing) {
                    val xo = (y/spacing)%2 * spacing/2
                    val distance = field.distance(x,y)
                    val direction = if (flip) distance.perpendicular else distance
                    val position = Vector2(xo + x.toDouble(), y.toDouble()) + direction * factor
                    points.add(position)
                }
            }
            drawer.circles(points, 10.0)
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Flow003(), configuration {
        width = 1280
        height = 720
    })
}