package org.openrndr.workshop

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.math.mod
import org.openrndr.shape.rectangleBounds
import org.openrndr.shape.vector2Bounds

import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.experimental.buildSequence

fun measure(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double{  // generally used geo measurement function
    var R = 6378.137; // Radius of earth in KM
    var dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
    var dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                    Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    return d * 1000.0; // meters
}

fun fixWinding(points: List<Vector2>): List<Vector2> {

    var sum = 0.0
    points.forEachIndexed { i, v ->
        val after = points[mod(i + 1, points.size)]
        sum += (after.x - v.x) * (after.y + v.y)
    }

    var sign = 1.0
    if (sum < 0.0) {
        sign = -1.0
    }

    if (sum < 0.0) {
        return points.reversed()
    } else {
        return points
    }
}


class OSMap {
    lateinit var bounds: Rectangle

    class Node(val id:String) {
        var lat:Double = 0.0
        var lon:Double = 0.0
        var x: Double = 0.0
        var y: Double = 0.0
        var tags = mutableMapOf<String, String>()
    }

    val nodes: MutableMap<String, Node>  = mutableMapOf()

    class Way {
        var height = -1.0
        val nodes = mutableListOf<Node>()

        fun points(): List<Vector2> {
            return nodes.map { Vector2(it.x, it.y) }
        }

        val pairs = buildSequence {
            val points = points()
            for (i in 0 until points.size-1) {
                yield(Pair(points[i], points[i+1]))
            }
        }

        val ringPairs = buildSequence {
            val points = points()
            for (i in 0 until points.size) {
                yield(Pair(points[i], points[(i+1)%points.size]))
            }
        }
        var tags = mutableMapOf<String, String>()
    }
    val ways: MutableMap<String, Way> = mutableMapOf()

    val buildings = waysWithTag("building")

    fun waysWithTag(tag:String):Sequence<Way> =
         buildSequence {
            for (v in ways.values) {
                if (v.tags.containsKey(tag)) {
                    yield(v)
                }
            }
        }

    fun waysWithTagValues(tag:String, vararg values:String):Sequence<Way> {

        val set = values.toSet()

        return buildSequence {
            for (v in ways.values) {
                if (v.tags.containsKey(tag)) {
                    if (v.tags[tag] in set) {
                        yield(v)
                    }
                }
            }
        }
    }

    fun boundsLatLon() : Rectangle {

        val b = vector2Bounds(nodes.values.map { Vector2(it.lat, it.lon) })
        return b
    }

    fun boundsPlanar() : Rectangle {
        val seq = buildings.flatMap { it.nodes.map { Vector2(it.x, it.y) }.asSequence() }.toList()
        val b = vector2Bounds(seq)
        return b
    }


    constructor(file : File) {
        val stream = FileInputStream(file)
        val doc = Jsoup.parse(stream, "UTF-8", "", Parser.xmlParser())
        val nodes= doc.getElementsByTag("node")
        nodes.forEach {
            val node = Node(it.attr("id")).apply {
                lat = it.attr("lat").toDouble()
                lon = it.attr("lon").toDouble()

            }
            it.getElementsByTag("tag").forEach { tag ->
                val key = tag.attr("k")
                val value = tag.attr("v")?:""
                node.tags[key] = value
            }

            val id = it.attr("id")
            this.nodes.put(id, node)
        }

        doc.getElementsByTag("way").forEach {
            val way = Way().apply {
                it.getElementsByTag("nd").forEach {
                    val r = this@OSMap.nodes.get(it.attr("ref"))
                    if (r!=null) {
                        this.nodes.add(r)
                    } else {
                        println("node not found!")
                    }
                }
            }

            it.getElementsByTag("tag").forEach {
                val key = it.attr("k")
                val value = it.attr("v") ?: ""
                way.tags[key] = value
            }
            ways.put(it.attr("id"), way)
        }
        println("loaded ${nodes.size} nodes")
        println("loaded ${ways.size} ways")

        val b = boundsLatLon()
        this.nodes.values.forEach {
            val d = measure(b.x, b.y, it.lat, it.lon)// Mapping.map(b.x, b.x+b.width, 0.0, 640.0*200, it.lat)
            val v = Vector2(it.lat-b.x, it.lon-b.y)*100000.0
            it.x = v.x
            it.y = v.y
        }
        bounds = boundsPlanar()
    }
}