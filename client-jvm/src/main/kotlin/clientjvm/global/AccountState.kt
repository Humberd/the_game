package clientjvm.global

import clientjvm.exts.emitter
import pl.humberd.udp.packets.serverclient.PlayerDetails

object AccountState {
    var playerDetails: PlayerDetails? = null
        private set

    private val unsub by emitter()

    fun init() {
        ClientDataReceiver.watchFor<PlayerDetails>()
            .takeUntil(unsub)
            .subscribe {
                println(it)
                playerDetails = it
            }
    }

    fun kill() {
        unsub.onNext(true)
    }
}
