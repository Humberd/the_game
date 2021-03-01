using System;
using System.IO;

namespace Client.scripts.global.ingress
{
    public class IngressPacketReceiver
    {
        private readonly IngressPacketHandler _ingressPacketHandler = new IngressPacketHandler();

        public void Handle(byte[] packet)
        {
            var buffer = new BinaryReader(new MemoryStream(packet));

            if (buffer.ReadUInt32() != 0x42424242)
            {
                throw new Exception("Invalid handshake");
            }

            var packetType = (IngressPacketType) buffer.ReadUInt16();

            switch (packetType)
            {
                case IngressPacketType.PLAYER_UPDATE:
                {
                    _ingressPacketHandler.Handle(IngressDataPacket.PlayerUpdate.From(buffer));
                    break;
                }
                case IngressPacketType.PLAYER_DISCONNECT:
                {
                    _ingressPacketHandler.Handle(IngressDataPacket.PlayerDisconnect.From(buffer));
                    break;
                }
                case IngressPacketType.PLAYER_POSITION_UPDATE:
                {
                    _ingressPacketHandler.Handle(IngressDataPacket.PlayerPositionUpdate.From(buffer));
                    break;
                }
                default:
                {
                    throw new Exception($"Packet type not supported {packetType:X2}");
                }
            }
        }
    }
}
