using Godot;

namespace Client.scripts.global.udp.egress
{
    public enum EgressPacketType
    {
        CONNECTION_HELLO = 0x00,
        DISCONNECT = 0x01,
        AUTH_LOGIN = 0x05,
        POSITION_CHANGE = 0x10
    }

    public abstract class EgressDataPacket
    {
        private readonly EgressPacketType _type;

        public EgressDataPacket(EgressPacketType type)
        {
            _type = type;
        }

        public byte[] Pack()
        {
            var buffer = new StreamPeerBuffer();
            buffer.BigEndian = true;
            buffer.PutU32(0x69696969);
            buffer.PutU16((ushort) _type);

            PackData(buffer);

            return buffer.DataArray;
        }

        protected abstract void PackData(StreamPeerBuffer buffer);

        public class ConnectionHello : EgressDataPacket
        {
            public ConnectionHello() : base(EgressPacketType.CONNECTION_HELLO)
            {
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                // nothing to send
            }
        }

        public class Disconnect : EgressDataPacket
        {
            public Disconnect() : base(EgressPacketType.DISCONNECT)
            {
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                // nothing to send
            }
        }

        public class AuthLogin : EgressDataPacket
        {
            private readonly uint _pid;

            public AuthLogin(uint pid) : base(EgressPacketType.AUTH_LOGIN)
            {
                _pid = pid;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutU32(_pid);
            }
        }

        public class PositionChange: EgressDataPacket
        {
            private readonly byte _mask;

            public PositionChange(byte mask) : base(EgressPacketType.POSITION_CHANGE)
            {
                _mask = mask;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutU8(_mask);
            }
        }
    }
}
