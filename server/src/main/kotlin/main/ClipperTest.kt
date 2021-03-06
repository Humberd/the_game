package main

import de.lighti.clipper.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
fun main() {
    val gridCell = Path().also {
        it.addAll(
            arrayListOf(
                Point.LongPoint(0, 0),
                Point.LongPoint(10, 0),
                Point.LongPoint(10, 10),
                Point.LongPoint(0, 10)
            )
        )
    }
    val obstacle = Path().also {
        it.addAll(
            arrayListOf(
                Point.LongPoint(6, 6),
                Point.LongPoint(16, 6),
                Point.LongPoint(16, 16),
                Point.LongPoint(6, 16)
            )
        )
    }

    val solution = Paths()

    val clp = DefaultClipper(Clipper.STRICTLY_SIMPLE)
    clp.addPath(gridCell, Clipper.PolyType.CLIP, true)
    clp.addPath(obstacle, Clipper.PolyType.SUBJECT, true)

    val mark = TimeSource.Monotonic.markNow();
    repeat(100) {
        clp.execute(Clipper.ClipType.INTERSECTION, solution)
        println(mark.elapsedNow())
    }
    println(solution)
}
