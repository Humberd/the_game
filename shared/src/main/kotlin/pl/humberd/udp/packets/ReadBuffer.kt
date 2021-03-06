package pl.humberd.udp.packets

import pl.humberd.models.*
import java.nio.ByteBuffer

class ReadBuffer(private val buffer: ByteBuffer) {
    // signed
    fun getByte() = buffer.get()
    fun getShort() = buffer.short
    fun getInt() = buffer.int
    fun getLong() = buffer.long
    fun getFloat() = buffer.float
    fun getDouble() = buffer.double

    // unsigned
    fun getUByte() = getByte().toUByte()
    fun getUShort() = getShort().toUShort()
    fun getUInt() = getInt().toUInt()
    fun getULong() = getLong().toULong()

    // complex
    fun getBoolean() = getUByte() > UByte.MIN_VALUE
    fun getVector2() = ApiVector2(getFloat(), getFloat())
    inline fun <reified T> getArray(noinline mapper: (index: Int) -> T) =
        Array(getUShort().toInt(), mapper)

    inline fun <reified T> getNullableArray(mapper: (index: Int) -> T): Array<T?> =
        Array(getUShort().toInt()) {
            val hasContent = getBoolean()
            if (!hasContent) {
                return@Array null
            }
            mapper.invoke(it)
        }

    inline fun <reified T> getNullableObject(mapper: () -> T): T? {
        val hasContent = getBoolean()
        if (!hasContent) {
            return null
        }

        return mapper.invoke()
    }

    fun getString(): String {
        val length = getUShort().toInt()
        if (length == 0) {
            return ""
        }
        val bytes = ByteArray(length)
        buffer.get(bytes, 0, length)
        return String(bytes, Charsets.UTF_8)
    }

    // ids
    fun getPID() = PID(getUInt())
    fun getCID() = CID(getUInt())
    fun getSID() = SID(getUInt())

    fun getMilliseconds() = Milliseconds(getUInt())
    fun getExperience() = Experience(getLong())
}


