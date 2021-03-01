using System.IO;
using Client.scripts.extensions;
using Godot;
using PID = System.UInt32;

namespace Client.scripts.global.ingress
{
    public enum IngressPacketType
    {
        PLAYER_UPDATE = 0x20,
        PLAYER_DISCONNECT = 0x21,
        PLAYER_POSITION_UPDATE = 0x22
    }

    public class IngressDataPacket
    {
        public class PlayerUpdate : IngressDataPacket
        {
            public readonly PID Pid;
            public readonly Vector2 Position;
            public readonly uint Health;
            public readonly string Name;

            private PlayerUpdate(uint pid, Vector2 position, uint health, string name)
            {
                Pid = pid;
                Position = position;
                Health = health;
                Name = name;
            }

            public static PlayerUpdate From(BinaryReader buffer)
            {
                return new PlayerUpdate(
                    pid: buffer.ReadUInt32(),
                    position: new Vector2(buffer.ReadSingle(), buffer.ReadSingle()),
                    health: buffer.ReadUInt32(),
                    name: buffer.ReadServerString()
                );
            }
        }

        public class PlayerDisconnect : IngressDataPacket
        {
            public readonly PID Pid;

            public PlayerDisconnect(uint pid)
            {
                Pid = pid;
            }

            public static PlayerDisconnect From(BinaryReader buffer)
            {
                return new PlayerDisconnect(
                    pid: buffer.ReadUInt32()
                );
            }
        }

        public class PlayerPositionUpdate : IngressDataPacket
        {
            public readonly PID Pid;
            public readonly Vector2 Position;

            public PlayerPositionUpdate(uint pid, Vector2 position)
            {
                Pid = pid;
                Position = position;
            }

            public static PlayerPositionUpdate From(BinaryReader buffer)
            {
                return new PlayerPositionUpdate(
                    pid: buffer.ReadUInt32(),
                    position: new Vector2(buffer.ReadSingle(), buffer.ReadSingle())
                );
            }
        }
    }
}
