package pl.humberd.udp.packets.serverclient

import pl.humberd.models.ApiVector2
import pl.humberd.models.Milliseconds
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class ProjectileSend(
    // fixme spritId
    val spriteId: UInt,
    val sourcePosition: ApiVector2,
    val targetPosition: ApiVector2,
    val duration: Milliseconds
) : ServerClientUdpPacket(Type.PROJECTILE_SEND) {
    constructor(buffer: ReadBuffer) : this(
        spriteId = buffer.getUInt(),
        sourcePosition = buffer.getVector2(),
        targetPosition = buffer.getVector2(),
        duration = buffer.getMilliseconds()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putUInt(spriteId)
        buffer.putVector2(sourcePosition)
        buffer.putVector2(targetPosition)
        buffer.putMilliseconds(duration)

    }
}
