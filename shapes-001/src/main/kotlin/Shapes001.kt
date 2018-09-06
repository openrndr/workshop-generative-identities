package org.openrndr.workshop

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.math.Vector2
import org.openrndr.svg.loadSVG
import java.io.File

class Shapes001 : Program() {
    var drawFunc = {}

    override fun setup() {
        val letters = loadSVG(File("data/logo.svg").readText()).findShapes()
        val shapes = loadSVG(File("data/shapes.svg").readText()).findShapes()

        drawFunc = {
            drawer.background(ColorRGBa.PINK)

            for ((letterIndex, node) in letters.withIndex()) {

                drawer.pushStyle()
                drawer.pushTransforms()
                drawer.fill = ColorRGBa.BLACK
                drawer.shape(node.shape)
                //drawer.drawStyle.clip = node.bounds

                drawer.stroke = ColorRGBa.PINK
                drawer.fill = ColorRGBa.PINK
                drawer.strokeWeight = 2.0


                val shapeNode = shapes[letterIndex%shapes.size]

                drawer.translate(node.bounds.center)
                drawer.rotate(seconds*45.0)

                drawer.translate(mouse.position*0.1)
                drawer.translate(-shapeNode.bounds.center)

                drawer.shape(shapeNode.shape)
                drawer.popStyle()
                drawer.popTransforms()
            }
        }
    }

    override fun draw() {
        drawFunc()
    }
}

fun main(args: Array<String>) {
    application(Shapes001(), configuration {
        width = 1280
        height = 720
    })
}