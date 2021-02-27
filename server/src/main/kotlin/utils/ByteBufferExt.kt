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
