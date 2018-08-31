import org.openrndr.Program
import org.openrndr.application
import org.openrndr.configuration

class Dots : Program() {

}

fun main(args: Array<String>) {
    application(Dots(), configuration {
        width = 1280
        height = 720
    })
}