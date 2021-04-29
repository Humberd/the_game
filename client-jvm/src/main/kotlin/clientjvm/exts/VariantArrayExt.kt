package clientjvm.exts

import godot.core.VariantArray

fun <T> VariantArray<T>.print(): String {
    val iMax = size - 1
    if (iMax == -1) return "[]"
    val b = StringBuilder()
    b.append('[')
    var i = 0
    while (true) {
        b.append(this[i].toString())
        if (i == iMax) return b.append(']').toString()
        b.append(", ")
        i++
    }
}
