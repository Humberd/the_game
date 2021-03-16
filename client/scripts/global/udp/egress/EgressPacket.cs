using Client.scripts.extensions;
using Godot;

namespace Client.scripts.global.udp.egress
{
    public enum EgressPacketType
    {
        CONNECTION_HELLO = 0x00,
        DISCONNECT = 0x01,
        AUTH_LOGIN = 0x05,
        POSITION_CHANGE = 0x10,
        TERRAIN_ITEM_DRAG = 0x11,
        SPELL_USAGE = 0x12,
        BASIC_ATTACK_START = 0x13,
        BASIC_ATTACK_END = 0x14
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

        public class PositionChange : EgressDataPacket
        {
            private readonly Vector2 _targetPosition;

            public PositionChange(Vector2 targetPosition) : base(EgressPacketType.POSITION_CHANGE)
            {
                _targetPosition = targetPosition;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutVector2(_targetPosition);
            }
        }

        public class TerrainItemDrag : EgressDataPacket
        {
            private readonly uint _iid;
            private readonly Vector2 _position;

            public TerrainItemDrag(uint iid, Vector2 position) : base(EgressPacketType.TERRAIN_ITEM_DRAG)
            {
                _iid = iid;
                _position = position;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutU32(_iid);
                buffer.PutVector2(_position);
            }
        }

        public class SpellUsage : EgressDataPacket
        {
            private readonly uint _sid;

            public SpellUsage(uint sid) : base(EgressPacketType.SPELL_USAGE)
            {
                _sid = sid;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutU32(_sid);
            }
        }

        public class BasicAttackStart : EgressDataPacket
        {
            private readonly uint _targetCid;

            public BasicAttackStart(uint targetCid) : base(EgressPacketType.BASIC_ATTACK_START)
            {
                _targetCid = targetCid;
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                buffer.PutU32(_targetCid);
            }
        }

        public class BasicAttackStop : EgressDataPacket
        {
            public BasicAttackStop() : base(EgressPacketType.BASIC_ATTACK_END)
            {
            }

            protected override void PackData(StreamPeerBuffer buffer)
            {
                // not sending any data
            }
        }
    }
}
