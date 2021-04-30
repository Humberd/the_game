package clientjvm.global

import java.net.DatagramSocket
import kotlin.random.Random

val socket = DatagramSocket(Random.nextInt(43211, 45211))
