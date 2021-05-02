package pl.humberd.udp.packets.serverclient

import pl.humberd.models.ApiVector2
import pl.humberd.models.CID
import pl.humberd.models.Experience
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type.CREATURE_UPDATE

data class CreatureUpdate(
    val cid: CID,
    val name: String,
    val baseHealth: Int,
    val currentHealth: Int,
    val experience: Experience,
    val position: ApiVector2,
    // fixme: spriteId
    val spriteId: UShort,
    val bodyRadius: Float,
    val attackTriggerRadius: Float,
    val isBeingAttackedByMe: Boolean
): ServerClientUdpPacket(CREATURE_UPDATE) {
    constructor(buffer: ReadBuffer): this(
        cid = buffer.getCID(),
        name = buffer.getString(),
        baseHealth = buffer.getInt(),
        currentHealth = buffer.getInt(),
        experience = buffer.getExperience(),
        position = buffer.getVector2(),
        spriteId = buffer.getUShort(),
        bodyRadius = buffer.getFloat(),
        attackTriggerRadius = buffer.getFloat(),
        isBeingAttackedByMe = buffer.getBoolean()
    )


    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(cid)
        buffer.putString(name)
        buffer.putInt(baseHealth)
        buffer.putInt(currentHealth)
        buffer.putExperience(experience)
        buffer.putVector2(position)
        buffer.putUShort(spriteId)
        buffer.putFloat(bodyRadius)
        buffer.putFloat(attackTriggerRadius)
        buffer.putBoolean(isBeingAttackedByMe)
    }
}
