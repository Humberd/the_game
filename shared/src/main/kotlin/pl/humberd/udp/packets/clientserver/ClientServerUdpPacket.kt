package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.UdpPacket

sealed class ClientServerUdpPacket(type: ClientServerUdpPacketType) : UdpPacket<ClientServerUdpPacketType>(type)
