package utils

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex(): String {
    val result = StringBuffer()

    result.append("[")
    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append("0x")
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
        result.append(", ")
    }
    result.delete(result.length - 2, result.length)
    result.append("]")

    return result.toString()
}
