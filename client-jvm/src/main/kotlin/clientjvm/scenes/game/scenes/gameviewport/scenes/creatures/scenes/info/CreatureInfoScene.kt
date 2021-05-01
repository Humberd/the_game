package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info

import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info.scenes.bar.CreatureInfoBarScene
import godot.Control
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.getNode
import pl.humberd.udp.packets.serverclient.CreatureUpdate

@RegisterClass
class CreatureInfoScene : Control() {
    private lateinit var healthBar: CreatureInfoBarScene
    private lateinit var manaBar: CreatureInfoBarScene
    private lateinit var expBar: CreatureInfoBarScene
    private lateinit var levelLabel: Label
    private lateinit var nameLabel: Label

    @RegisterFunction
    override fun _ready() {
        healthBar = getNode<CreatureInfoBarScene>("Bars/HealthBar").also {
            it.setColor(Color.darkred)
        }
        manaBar = getNode<CreatureInfoBarScene>("Bars/ManaBar").also {
            it.setColor(Color.darkblue)
        }
        expBar = getNode<CreatureInfoBarScene>("Bars/ExpBar").also {
            it.setColor(Color.darkgray)
        }
        levelLabel = getNode("Panel/Level")
        nameLabel = getNode("Name")
    }

    fun update(packet: CreatureUpdate) {
        healthBar.width = packet.currentHealth / packet.baseHealth.toFloat()
        manaBar.width = 0f
        expBar.width = 0f
        nameLabel.text = packet.name
    }
}
