package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class EquipmentUpdate(
    val headSlot: EquipmentSlotDTO,
    val bodySlot: EquipmentSlotDTO,
    val legsSlot: EquipmentSlotDTO,
    val feetSlot: EquipmentSlotDTO,
    val leftHandSlot: EquipmentSlotDTO,
    val rightHandSlot: EquipmentSlotDTO,
) : ServerClientUdpPacket(Type.EQUIPMENT_UPDATE) {
    data class EquipmentSlotDTO(
        // fixme: item instance id
        val itemInstanceId: UInt
    )

    companion object {
        fun getSlot(buffer: ReadBuffer) =
            EquipmentSlotDTO(buffer.getUInt())

        fun putSlot(buffer: WriteBuffer, slot: EquipmentSlotDTO) =
            buffer.putUInt(slot.itemInstanceId)
    }

    constructor(buffer: ReadBuffer) : this(
        headSlot = getSlot(buffer),
        bodySlot = getSlot(buffer),
        legsSlot = getSlot(buffer),
        feetSlot = getSlot(buffer),
        leftHandSlot = getSlot(buffer),
        rightHandSlot = getSlot(buffer),
    )


    override fun packData(buffer: WriteBuffer) {
        putSlot(buffer, headSlot)
        putSlot(buffer, bodySlot)
        putSlot(buffer, legsSlot)
        putSlot(buffer, feetSlot)
        putSlot(buffer, leftHandSlot)
        putSlot(buffer, rightHandSlot)
    }
}
