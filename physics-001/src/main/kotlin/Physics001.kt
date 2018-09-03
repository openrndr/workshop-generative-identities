import com.badlogic.gdx.physics.box2d.*
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.svg.loadSVG
import java.io.File
import ktx.box2d.*

class Physics001 : Program() {

    private var accumulator = 0f
    private lateinit var world : World
    private lateinit var bodyDef : BodyDef
    lateinit var fixture : Fixture
    private lateinit var body : Body
    var joints = arrayListOf<Joint>()

    class Constants {
        companion object {
            var TIME_STEP = 1/60f
            var VELOCITY_ITERATIONS = 1
            var POSITION_ITERATIONS = 1
        }
    }

    private var bodyList = arrayListOf<BodyShape>()
    private var floorBody = arrayListOf<BodyShape>()

    val composition = loadSVG(File("data/logo.svg").readText())

    class BodyShape(var body: Body, var width: Double, var height: Double, var origin: com.badlogic.gdx.math.Vector2)

    override fun setup() {
        super.setup()

        world = World(com.badlogic.gdx.math.Vector2(0.0f, 0.5f), false)

        val fixtureWidth = width
        val fixtureHeight = 15.0


        bodyDef = BodyDef()
        val origin = com.badlogic.gdx.math.Vector2(width/2.0f, height.toFloat())

        bodyDef.position.set(origin.x, origin.y)
        bodyDef.type = BodyDef.BodyType.KinematicBody

        body = world.createBody(bodyDef)
        val shape = PolygonShape()
        shape.setAsBox(
                (fixtureWidth / 2.0).toFloat(),
                (fixtureHeight / 2.0).toFloat()
        )
        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape

        body.createFixture(fixtureDef)
        val bodyShape = BodyShape(body, fixtureWidth.toDouble(), fixtureHeight.toDouble(), origin)
        floorBody.add(bodyShape)


        // add dots
        for (shape in composition.findShapes()) {
            for (contour in shape.shape.contours) {
                contour.equidistantPositions((contour.length / 10.0).toInt()).mapIndexed { index, it ->
                    newBall(Vector2(it.x, it.y))
                }
            }
        }

        // make forces
        bodyList.forEachIndexed { index, bodyShape ->
            bodyList.forEachIndexed { index2, bodyShape2 ->
                if(index != index2) {

                    val pos1 = bodyShape.body.position
                    val pos2 = bodyShape2.body.position
                    val distance = Math.sqrt(((pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y)).toDouble())

                    if (distance < 21) {
                        var joint = bodyShape.body.distanceJointWith(bodyShape2.body) {
                            this.length = (distance) .toFloat()
                        }
                        joints.add(joint)
                    }

                }
            }
        }

    }

    private fun newBall(position: Vector2) {

        val fixtureWidth = 10.0
        val fixtureHeight = 10.0

        val origin = com.badlogic.gdx.math.Vector2(
                position.x.toFloat(),
                position.y.toFloat()
        )

        bodyDef.position.set(origin.x, origin.y)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val shape = CircleShape()

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.shape.radius = 5.0f
        fixtureDef.density = 0.5f
        fixtureDef.friction = 0.2f
        fixtureDef.restitution = .9f // Make it bounce a little bit
        fixtureDef.shape = shape

        body.createFixture(fixtureDef)
        val bodyShape = BodyShape(body, fixtureWidth, fixtureHeight, origin)
        bodyList.add(bodyShape)

        val cl = bodyList.size

    }

    override fun draw() {
        super.draw()

        drawer.stroke = null
        drawer.fill = ColorRGBa.PINK



        drawer.circles(
                bodyList.mapIndexed { index, f ->
                    Circle(Vector2(f.body.position.x*1.0, f.body.position.y*1.0), 5.0)
                }
        )

        floorBody.forEachIndexed { index, f ->
            drawer.isolated {
                drawer.translate(f.body.position.x.toDouble() - f.width/2,
                        f.body.position.y.toDouble() - f.height/2)
                drawer.rectangle(
                        1.0,
                        5.0,
                        f.width-1,
                        f.height-10.0
                )
            }
        }

        var lineList = mutableListOf<org.openrndr.math.Vector2>()
        for (j in joints) {

            drawer.stroke = ColorRGBa.PINK
            drawer.isolated {
                lineList.add(org.openrndr.math.Vector2(j.anchorA.x.toDouble(), j.anchorA.y.toDouble()))
                lineList.add(org.openrndr.math.Vector2(j.anchorB.x.toDouble(), j.anchorB.y.toDouble()))
            }

        }

        if (lineList.size > 0) {
            drawer.lineSegments(lineList)
        }

        doPhysicsStep(seconds.toFloat())
    }

    private fun doPhysicsStep(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS)
            accumulator -= Constants.TIME_STEP
        }
    }

}

fun main(args: Array<String>) {
    application(Physics001(), configuration {
        width = 1280
        height = 720
    })
    Box2D.init()
}
