package clientjvm.scenes.game.scenes.gameviewport.scenes.ui_spells

import clientjvm.global.ClientDataSender
import godot.Control
import godot.InputEvent
import godot.InputEventKey
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import mu.KLogging
import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.clientserver.SpellCastEnd
import pl.humberd.udp.packets.clientserver.SpellCastStart

@RegisterClass
class GameUiSpellsScene : Control() {
    companion object : KLogging()

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (event !is InputEventKey) {
            return
        }
        if (event.isActionPressed("spell_slot_0")) {
            ClientDataSender.send(SpellCastStart(0u, ApiVector2(0f, 0f)))
            logger.info { "Pressed" }
        }
        if (event.isActionReleased("spell_slot_0")) {
            ClientDataSender.send(SpellCastEnd(0u, ApiVector2(0f, 0f)))
            logger.info { "Released" }
        }
    }
}
