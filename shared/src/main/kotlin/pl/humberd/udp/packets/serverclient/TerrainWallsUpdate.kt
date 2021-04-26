package pl.humberd.udp.packets.serverclient

import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class TerrainWallsUpdate(
    val chains: Array<Array<ApiVector2>>
) : ServerClientUdpPacket(Type.TERRAIN_WALLS_UPDATE) {
    constructor(buffer: ReadBuffer) : this(
        chains = buffer.getArray {
            buffer.getArray {
                buffer.getVector2()
            }
        }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putArray(chains) {
            buffer.putArray(it) {
                buffer.putVector2(it)
            }
        }
    }
}
