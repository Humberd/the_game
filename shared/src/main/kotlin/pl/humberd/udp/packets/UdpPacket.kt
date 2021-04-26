package pl.humberd.udp.packets

abstract class UdpPacket<T : UdpPacketType>(private val type: T) {
    fun pack(buffer: WriteBuffer) {
        buffer.putUShort(type.value.toUShort())
        packData(buffer)
    }

    protected abstract fun packData(buffer: WriteBuffer);
}
