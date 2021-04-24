package pl.humberd.upd.packets

import pl.humberd.upd.models.putUShort
import java.nio.ByteBuffer

abstract class UdpPacket<T : UdpPacketType>(private val type: T) {
    fun pack(buffer: ByteBuffer) {
        buffer.putUShort(type.value.toUShort())
        packData(buffer)
    }

    abstract fun packData(buffer: ByteBuffer);
}
