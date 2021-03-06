package pl.humberd.udp.packets

import pl.humberd.models.*
import java.nio.ByteBuffer

class WriteBuffer(private val buffer: ByteBuffer) {
    // signed
    fun putByte(v: Byte) = buffer.put(v)
    fun putShort(v: Short) = buffer.putShort(v)
    fun putInt(v: Int) = buffer.putInt(v)
    fun putLong(v: Long) = buffer.putLong(v)
    fun putFloat(v: Float) = buffer.putFloat(v)
    fun putDouble(v: Double) = buffer.putDouble(v)

    // unsigned
    fun putUByte(v: UByte) = putByte(v.toByte())
    fun putUShort(v: UShort) = putShort(v.toShort())
    fun putUInt(v: UInt) = putInt(v.toInt())
    fun putULong(v: ULong) =  putLong(v.toLong())

    // complex
    fun putBoolean(v: Boolean) = putByte(if (v) 1 else 0)
    fun putVector2(v: ApiVector2) {
        putFloat(v.x)
        putFloat(v.y)
    }
    fun <T> putArray(v: Array<T>, mapper: (T) -> Unit) {
        putUShort(v.size.toUShort())
        v.forEach { mapper.invoke(it) }
    }
    fun <T> putNullableArray(v: Array<T?>, mapper: (T) -> Unit) {
        putUShort(v.size.toUShort())
        v.forEach {
            if (it == null) {
                putBoolean(false)
            } else {
                putBoolean(true)
                mapper.invoke(it)
            }
        }
    }

    fun <T> putNullableObject(v: T?, mapper: (T) -> Unit) {
        if (v == null) {
            putBoolean(false)
        } else {
            putBoolean(true)
            mapper.invoke(v)
        }
    }

    fun putString(v: String) {
        require(v.length < 500)
        putUShort(v.length.toUShort())
        buffer.put(v.encodeToByteArray())
    }

    // ids
    fun putPID(v: PID) = putUInt(v.value)
    fun putCID(v: CID) = putUInt(v.value)
    fun putSID(v: SID) = putUInt(v.value)

    fun putMilliseconds(v: Milliseconds) = putUInt(v.value)
    fun putExperience(v: Experience) = putLong(v.value)
}
