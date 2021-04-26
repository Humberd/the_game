package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class BackpackUpdate(
    val items: Array<BackpackSlot?>
) : ServerClientUdpPacket(Type.BACKPACK_UPDATE) {
    data class BackpackSlot(
        // fixme: itemSchemaID
        val itemSchemaId: UShort,
        val stackCount: UShort
    )

    constructor(buffer: ReadBuffer) : this(
        items = buffer.getNullableArray {
            BackpackSlot(
                itemSchemaId = buffer.getUShort(),
                stackCount = buffer.getUShort()
            )
        }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putNullableArray(items) {
            buffer.putUShort(it.itemSchemaId)
            buffer.putUShort(it.stackCount)
        }
    }
}
