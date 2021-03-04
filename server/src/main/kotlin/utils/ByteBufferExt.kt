package utils

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

fun <T> ByteBuffer.putArray(value: Array<T>, itemPut: (T) -> Unit) {
    putUShort(value.size.toUShort())
    value.forEach { itemPut.invoke(it) }
}

fun <T> ByteBuffer.putList(value: Collection<T>, itemPut: (T) -> Unit) {
    putUShort(value.size.toUShort())
    value.forEach { itemPut.invoke(it) }
}

