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
    val bodyRadius: Float,
    val monsterData: MonsterData?
): ServerClientUdpPacket(CREATURE_UPDATE) {
    data class MonsterData(
        val detectionRadius: Float,
        val chaseRadius: Float
    )

    constructor(buffer: ReadBuffer) : this(
        cid = buffer.getCID(),
        name = buffer.getString(),
        baseHealth = buffer.getInt(),
        currentHealth = buffer.getInt(),
        experience = buffer.getExperience(),
        position = buffer.getVector2(),
        bodyRadius = buffer.getFloat(),
        monsterData = buffer.getNullableObject {
            MonsterData(
                detectionRadius = buffer.getFloat(),
                chaseRadius = buffer.getFloat()
            )
        }
    )


    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(cid)
        buffer.putString(name)
        buffer.putInt(baseHealth)
        buffer.putInt(currentHealth)
        buffer.putExperience(experience)
        buffer.putVector2(position)
        buffer.putFloat(bodyRadius)
        buffer.putNullableObject(monsterData) {
            buffer.putFloat(it.detectionRadius)
            buffer.putFloat(it.chaseRadius)
        }
    }
}
