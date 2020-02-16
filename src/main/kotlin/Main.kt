import frc.team4069.saturn.units.*

fun main(args: Array<String>) {
    val x = 3.amp
    val y = 2.second
    val z = 6.coulomb

    val z2 = z + x * y
    println(z2.value)
}