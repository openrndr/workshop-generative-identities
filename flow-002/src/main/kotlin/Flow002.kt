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

class Flow002 : Program() {
    var drawFunc = {}

    override fun setup() {
        val image = ColorBuffer.fromFile("data/clouds.png")

        val viz = colorBuffer(image.width, image.height)

        var fill = true

        mouse.clicked.listen {
            fill = !fill
        }

        image.shadow.download()
        val field = FastestDirectionalFieldBuilder().build(image.shadow) {
            if ((it.r + it.g + it.b) / 3.0 < 0.5) 0 else 1
        }




        for (y in 0 until field.height()) {
            for (x in 0 until field.width()) {
                val dist = field.distance(x,y)
                val l = dist.length
                val n = dist.normalized
                viz.shadow.write(x,y, ColorRGBa(n.x*0.5+0.5, n.y*0.5+0.5, 0.0 ))
                //viz.shadow.write(x,y, ColorRGBa(l/320.0, 0.0, 0.0))

            }
        }
        viz.shadow.upload()

        fun pointInside(shadow: ColorBufferShadow): Vector2 {
            while (true) {
                val v = Vector2(Math.random() * width, Math.random() * height)
                if (shadow.read(v.x.toInt(), v.y.toInt()).let {
                            (it.r + it.g + it.b) / 3.0
                        } > 0.5) {
                    return v
                }
            }
        }

        val points = (0..3000).map {
            pointInside(image.shadow)
            //Vector2(Math.random() * width, Math.random() * height)
        }.toMutableList()

        val radii = points.map { 1.0 }.toMutableList()

        drawFunc = {
            drawer.background(ColorRGBa.WHITE)
            //drawer.fill = ColorRGBa.BLACK.opacify(0.5)
            if (fill) {
                drawer.fill = ColorRGBa.BLACK
                drawer.stroke = null

            } else {
                drawer.fill = null
                drawer.stroke = ColorRGBa.BLACK
            }
            points.forEachIndexed { index, it ->
                val distance = field.distance(it.x.toInt(), it.y.toInt())
                points[index] = it + distance.normalized.perpendicular * Math.cos(index*0.1)
                radii[index] = distance.length
            }
            drawer.image(viz)
            //drawer.circles(points, radii)

            drawer.stroke = ColorRGBa.WHITE

            for (y in 0 until height step 16)
                for (x in 0 until width step 16) {
                    val d = field.distance(x,y)
                    if (d.length > 0)
                    drawer.lineSegment(Vector2(x*1.0,y*1.0), Vector2(x*1.0,y*1.0) + d.normalized*8.0)
                }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Flow002(), configuration {
        width = 1280
        height = 720
    })
}