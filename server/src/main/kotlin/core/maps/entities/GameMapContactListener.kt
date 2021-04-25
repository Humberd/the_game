package core.maps.entities

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import core.maps.entities.creatures.Creature
import core.maps.shapes.Wall

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class GameMapContactListener : ContactListener {
    override fun beginContact(contact: Contact) {
        logger.debug { "Collision ${contact.fixtureA.userData} with ${contact.fixtureB.userData}" }
        handleContacts(contact.fixtureA.userData, contact.fixtureB.userData)
        handleContacts(contact.fixtureB.userData, contact.fixtureA.userData)
    }

    private fun handleContacts(aaa: Any?, bbb: Any?) {
        when (aaa) {
            is Creature -> when (bbb) {
                is Wall -> aaa.hooks.onCollideWith(bbb)
            }
        }
    }

    override fun endContact(contact: Contact) {
//        logger.debug { "END_CONTACT $contact" }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
//        logger.debug { "PRE_SOLVE $contact $oldManifold" }
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
//        logger.debug { "POST_SOLVE $contact $impulse" }
    }
}
