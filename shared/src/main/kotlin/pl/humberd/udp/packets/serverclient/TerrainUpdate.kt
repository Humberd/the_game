package pl.humberd.udp.packets.serverclient

import pl.humberd.misc.HotPacket
import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type.TERRAIN_UPDATE

@HotPacket
data class TerrainUpdate(
    val windowWidth: UByte,
    val windowHeight: UByte,
    val windowGridStartPositionX: Short,
    val windowGridStartPositionY: Short,
    // fixme spriteId
    val spriteIds: Array<UShort>,
    val obstacles: Array<Array<Array<ApiVector2>>>
) : ServerClientUdpPacket(TERRAIN_UPDATE) {
    constructor(buffer: ReadBuffer) : this(
        windowWidth = buffer.getUByte(),
        windowHeight = buffer.getUByte(),
        windowGridStartPositionX = buffer.getShort(),
        windowGridStartPositionY = buffer.getShort(),
        spriteIds = buffer.getArray { buffer.getUShort() },
        obstacles = buffer.getArray { buffer.getArray { buffer.getArray { buffer.getVector2() } } }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putUByte(windowWidth)
        buffer.putUByte(windowHeight)
        buffer.putShort(windowGridStartPositionX)
        buffer.putShort(windowGridStartPositionY)
        buffer.putArray(spriteIds) { buffer.putUShort(it) }
        buffer.putArray(obstacles) { buffer.putArray(it) { buffer.putArray(it) { buffer.putVector2(it) } } }
    }
}

