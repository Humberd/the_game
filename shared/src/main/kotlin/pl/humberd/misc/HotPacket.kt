package pl.humberd.misc

import pl.humberd.udp.packets.UdpPacket
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HotPacket

fun UdpPacket<*>.isHot() = this::class.hasAnnotation<HotPacket>()
