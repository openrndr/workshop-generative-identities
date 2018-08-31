package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.ColorBuffer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.svg.loadSVG
import java.io.File

class Mosaic001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val image = ColorBuffer.fromFile("data/logo.png")
        val tiles = ColorBuffer.fromFile("data/mosaic-16.png")


        val blockSize = 16

        image.shadow.download()
        drawFunc = {
            drawer.background(ColorRGBa.WHITE)

            val pairs = mutableListOf<Pair<Rectangle, Rectangle>>()


            for (v in 0 until image.height step blockSize) {
                for (u in 0 until image.width step blockSize) {

                    var average = 0.0
                    var weight = 0
                    for (j in 0 until blockSize) {
                        for (i in 0 until blockSize) {

                            if (u+i < image.width && v+j < image.height) {
                                val c = image.shadow.read(u + i, v + j)
                                val i = 1.0 - ((c.r + c.g + c.b) / 3.0)


                                average += i
                                weight++
                            }

                        }
                    }
                    average /= weight
                    val tile = Math.min((Math.random()-0.5) * 0.2 + average*8.0, 7.0).toInt()

                    if (average > 0) {
                        val o = (Math.random() * 4).toInt()

                        when (o) {
                            0 -> pairs.add(
                                    Rectangle(tile * blockSize * 1.0, 0.0, blockSize * 1.0, blockSize * 1.0) to Rectangle(u * 1.0, v * 1.0, blockSize * 1.0, blockSize * 1.0))
                            1 -> pairs.add(
                                    Rectangle(tile * blockSize * 1.0, blockSize * 1.0, blockSize * 1.0, blockSize * -1.0) to Rectangle(u * 1.0, v * 1.0, blockSize * 1.0, blockSize * 1.0))
                            2 -> pairs.add(
                                    Rectangle((1.0 + tile) * blockSize * 1.0, 0.0, blockSize * -1.0, blockSize * 1.0) to Rectangle(u * 1.0, v * 1.0, blockSize * 1.0, blockSize * 1.0))
                            3 -> pairs.add(
                                    Rectangle((1.0 + tile) * blockSize * 1.0, blockSize * 1.0, blockSize * -1.0, blockSize * -1.0) to Rectangle(u * 1.0, v * 1.0, blockSize * 1.0, blockSize * 1.0))

                        }
                    }

                }
            }
            drawer.image(tiles, pairs)

        }

    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Mosaic001(), configuration {
        width = 1280
        height = 720
    })
}