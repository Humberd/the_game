package utils

import com.badlogic.gdx.math.Vector2
import java.nio.ByteBuffer

fun ByteBuffer.uByte(): UByte {
    return this.get().toUByte()
}

fun ByteBuffer.uShort(): UShort {
    return this.short.toUShort()
}

fun ByteBuffer.uInt(): UInt {
    return this.int.toUInt()
}

fun ByteBuffer.uLong(): ULong {
    return this.long.toULong()
}

fun ByteBuffer.vector(): Vector2 {
    return Vector2(float, float)
}

fun ByteBuffer.putUByte(value: UByte) {
    this.put(value.toByte())
}

fun ByteBuffer.putUShort(value: UShort) {
    this.putShort(value.toShort())
}

fun ByteBuffer.putUInt(value: UInt) {
    this.putInt(value.toInt())
}

fun ByteBuffer.putString(value: String) {
    if (value.length > 500) {
        throw Error("String too long ${value.slice(0..50)}")
    }

    val utf8Bytes = value.encodeToByteArray()
    putUShort(utf8Bytes.size.toUShort())
    put(utf8Bytes)
}

// FIXME: 26.03.2021 There must be a better way to pack multiple booleans
fun ByteBuffer.putBoolean(value: Boolean) {
    put(if (value) 1 else 0)
}

fun <T> ByteBuffer.putArray(value: Array<T>, itemPut: (T) -> Unit) {
    putUShort(value.size.toUShort())
    value.forEach { itemPut.invoke(it) }
}

// FIXME: 26.03.2021 There must be a better way to pack a nullable array
fun <T> ByteBuffer.putNullableArray(value: Array<T?>, itemPut: (T) -> Unit) {
    putUShort(value.size.toUShort())
    value.forEach {
        if (it == null){
            put(0)
        } else {
            put(1)
            itemPut.invoke(it)
        }
    }
}

fun <T> ByteBuffer.putList(value: Collection<T>, itemPut: (T) -> Unit) {
    putUShort(value.size.toUShort())
    value.forEach { itemPut.invoke(it) }
}

fun ByteBuffer.putVector(value: Vector2) {
    putFloat(value.x)
    putFloat(value.y)
}
