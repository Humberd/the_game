package core.maps.entities

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.monster.Monster
import core.maps.obstacles.Obstacle
import core.maps.shapes.Wall
import mu.KLogging


class GameMapContactListener : ContactListener {
    companion object : KLogging()

    override fun beginContact(contact: Contact) {
        logger.info { "Collision ${contact.fixtureA.userData} with ${contact.fixtureB.userData}" }
        handleBeginContacts(contact.fixtureA.userData, contact.fixtureB.userData)
        handleBeginContacts(contact.fixtureB.userData, contact.fixtureA.userData)
    }

    private fun handleBeginContacts(aaa: Any?, bbb: Any?) {
        when (aaa) {
            is Creature -> when (bbb) {
                is Wall -> aaa.hooks.onCollideWith(bbb)
                is Obstacle -> aaa.hooks.onCollideWith(bbb)
            }
            is Monster.DetectionArea -> when (bbb) {
                is Creature -> aaa.onCollisionStartWith(bbb)
            }
        }
    }

    override fun endContact(contact: Contact) {
        logger.info { "END_CONTACT ${contact.fixtureA.userData} with ${contact.fixtureB.userData}\"" }
        handleEndContacts(contact.fixtureA.userData, contact.fixtureB.userData)
        handleEndContacts(contact.fixtureB.userData, contact.fixtureA.userData)
    }

    private fun handleEndContacts(aaa: Any?, bbb: Any?) {
        when (aaa) {
            is Monster.DetectionArea -> when (bbb) {
                is Creature -> aaa.onCollisionEndWith(bbb)
            }
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
//        logger.debug { "PRE_SOLVE $contact $oldManifold" }
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
//        logger.debug { "POST_SOLVE $contact $impulse" }
    }
}
