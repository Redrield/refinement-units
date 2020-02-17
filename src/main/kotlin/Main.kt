import frc.team4069.saturn.units.*

fun main(args: Array<String>) {
    val x = 6.meter
    val y = 2.second
    val z = 3.meter.velocity

    val z2 = z + x / y
//    val z2 = z + x
    println(z2.value)
}