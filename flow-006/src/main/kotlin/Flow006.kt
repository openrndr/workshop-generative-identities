package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import studio.rndr.distance.FastestDirectionalFieldBuilder
import java.io.File

class Flow006 : Program() {
    var drawFunc = {}

    override fun setup() {
        val image = ColorBuffer.fromFile("data/logo-01.png")
        val tiles = ColorBuffer.fromFile("data/mosaic-single.png")
        var flip = false

        val tileCount = 1.0
        val tileSize = 64.0
        mouse.clicked.listen {
            flip = !flip
        }

        image.shadow.download()
        val field = FastestDirectionalFieldBuilder().build(image.shadow) {
            if ((it.r + it.g + it.b) / 3.0 < 0.5) 0 else 1
        }

        val foreground = (0 until image.height).map { y ->
            (0 until image.width).map { x ->
                image.shadow.read(x, y).let { (it.r + it.g + it.b) / 3.0 < 0.5 }
            }
        }

        val randoms = (0..10000).map {
            (Math.random() * tileCount).toInt()
        }

        drawFunc = {
            drawer.background(ColorRGBa.PINK)
            drawer.strokeWeight = 2.0
            val points = mutableListOf<Pair<Rectangle, Rectangle>>()
            val factor = 10.0 * mouse.position.y / height
            val spacing = 5
            var index = 0
            val scale = 4.0
            for (y in 0 until height step spacing) {
                for (x in 0 until width step spacing) {

                    if (foreground[y][x]) {
                        val distanceToMouse = Vector2(x.toDouble(), y.toDouble()) - mouse.position

                        val rfactor = 10.0 / (1.0+ distanceToMouse.length * 0.1)

                        val xo = (y / spacing) % 2 * spacing / 2
                        val distance = field.distance(x, y)
                        val direction = if (flip) distance.perpendicular else distance
                        val position = Vector2(xo + x.toDouble(), y.toDouble()) + direction * rfactor

                        val tile = randoms[index]
                        points.add(Rectangle(tile * tileSize, 0.0, tileSize, tileSize) to Rectangle(position.x, position.y, spacing * scale, spacing * scale))
                        index++
                    }
                }
            }
            tiles.filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
            drawer.image(tiles, points)
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Flow006(), configuration {
        width = 1280
        height = 720
    })
}