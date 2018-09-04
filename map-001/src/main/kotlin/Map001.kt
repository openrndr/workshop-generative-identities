package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.shape.ShapeContour
import java.io.File

class Map001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val map = OSMap(File("data/corato.osm"))
        val scale = 0.3

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
            drawer.circles(
                    roadContours.mapIndexed { index, it ->
                        it.position(Math.cos(seconds+index)*0.5+0.5)
                    }, 10.0)
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Map001(), configuration {
        width = 1280
        height = 720
    })
}