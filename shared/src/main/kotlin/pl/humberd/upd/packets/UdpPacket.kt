package pl.humberd.upd.packets

abstract class UdpPacket<T : UdpPacketType>(private val type: T) {
    fun pack(buffer: WriteBuffer) {
        buffer.putUShort(type.value.toUShort())
        packData(buffer)
    }

    abstract fun packData(buffer: WriteBuffer);
}
