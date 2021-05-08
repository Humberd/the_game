package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info

import clientjvm.exts.getNodeAs
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info.scenes.bar.CreatureInfoBarScene
import godot.Control
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import mu.KLogging
import pl.humberd.udp.packets.serverclient.CreatureUpdate

@RegisterClass
class CreatureInfoScene : Control() {
    companion object : KLogging()

    private lateinit var healthBar: CreatureInfoBarScene
    private lateinit var manaBar: CreatureInfoBarScene
    private lateinit var expBar: CreatureInfoBarScene
    private lateinit var levelLabel: Label
    private lateinit var nameLabel: Label


    @RegisterFunction
    override fun _ready() {
        healthBar = getNodeAs<CreatureInfoBarScene>("Bars/HealthBar").also {
            it.setColor(Color.darkred)
        }
        manaBar = getNodeAs<CreatureInfoBarScene>("Bars/ManaBar").also {
            it.setColor(Color.darkblue)
        }
        expBar = getNodeAs<CreatureInfoBarScene>("Bars/ExpBar").also {
            it.setColor(Color.darkgray)
        }
        levelLabel = getNodeAs("Panel/Level")
        nameLabel = getNodeAs("Name")
    }

    fun update(packet: CreatureUpdate) {
        healthBar.width = packet.currentHealth / packet.baseHealth.toFloat()
        manaBar.width = 0f

        val totalExpToCurrentLevel = packet.experience.toLevel().expRequired()
        val totalExpToNextLevel = packet.experience.toLevel().next().expRequired()
        val expFromCurrentToNextLevel = totalExpToNextLevel - totalExpToCurrentLevel
        val currentExp = packet.experience
        val expGainedForThisLevel = currentExp - totalExpToCurrentLevel

        expBar.width = expGainedForThisLevel.value / expFromCurrentToNextLevel.value.toFloat()
        levelLabel.text = packet.experience.toLevel().value.toString()
        nameLabel.text = packet.name
    }
}
