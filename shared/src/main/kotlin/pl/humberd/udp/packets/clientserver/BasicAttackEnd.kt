package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

class BasicAttackEnd(): ClientServerUdpPacket(Type.BASIC_ATTACK_END) {
    constructor(buffer: ReadBuffer): this()

    override fun packData(buffer: WriteBuffer) {
        // nothing
    }

    override fun toString() = "BasicAttackEnd()"
}
