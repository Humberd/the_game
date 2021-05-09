package pl.humberd.models

object GameMaths {
    fun generateCircle(radius: Float): Array<ApiVector2> {
        require(radius > 0)
        val points = 50
        val step = 1f / points * Math.PI * 2
        return Array(points) {
            val angle = Math.PI * 2 - step * it
            ApiVector2(
                x = (Math.cos(angle) * radius).toFloat(),
                y = (Math.sin(angle) * radius).toFloat()
            )
        }
    }
}
