package clientjvm.exts

fun lerpAngle(start: Float, end: Float, amount: Float): Float {
    val shortest_angle = ((((end - start) % 360) + 540) % 360) - 180
    return shortest_angle * amount;
}
