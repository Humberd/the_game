using System.IO;
using Client.scripts.extensions;
using Godot;
using PID = System.UInt32;

namespace Client.scripts.global.udp.ingress
{
    public enum IngressPacketType
    {
        PLAYER_UPDATE = 0x20,
        PLAYER_DISCONNECT = 0x21,
        PLAYER_POSITION_UPDATE = 0x22,
        TERRAIN_UPDATE = 0x23,
        TERRAIN_ITEMS_UPDATE = 0x24
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

            private PlayerDisconnect(uint pid)
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

            private PlayerPositionUpdate(uint pid, Vector2 position)
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

        public class TerrainUpdate : IngressDataPacket
        {
            public readonly byte WindowWidth;
            public readonly byte WindowHeight;
            public readonly ushort WindowGridStartPositionX;
            public readonly ushort WindowGridStartPositionY;
            public readonly ushort[] SpriteIds;

            private TerrainUpdate(byte windowWidth, byte windowHeight, ushort windowGridStartPositionX,
                ushort windowGridStartPositionY, ushort[] spriteIds)
            {
                WindowWidth = windowWidth;
                WindowHeight = windowHeight;
                WindowGridStartPositionX = windowGridStartPositionX;
                WindowGridStartPositionY = windowGridStartPositionY;
                SpriteIds = spriteIds;
            }

            public static TerrainUpdate From(BinaryReader buffer)
            {
                return new TerrainUpdate(
                    windowWidth: buffer.ReadByte(),
                    windowHeight: buffer.ReadByte(),
                    windowGridStartPositionX: buffer.ReadUInt16(),
                    windowGridStartPositionY: buffer.ReadUInt16(),
                    spriteIds: buffer.ReadServerArray(() => buffer.ReadUInt16())
                );
            }
        }

        public class TerrainItemsUpdate : IngressDataPacket
        {
            public readonly ItemData[] Items;

            public class ItemData
            {
                public readonly uint InstanceId;
                public readonly ushort ItemId;
                public readonly Vector2 Position;

                public ItemData(uint instanceId, ushort itemId, Vector2 position)
                {
                    InstanceId = instanceId;
                    ItemId = itemId;
                    Position = position;
                }
            }

            private TerrainItemsUpdate(ItemData[] items)
            {
                Items = items;
            }

            public static TerrainItemsUpdate From(BinaryReader buffer)
            {
                return new TerrainItemsUpdate(
                    items: buffer.ReadServerArray(() => new ItemData(
                        instanceId: buffer.ReadUInt32(),
                        itemId: buffer.ReadUInt16(),
                        position: new Vector2(buffer.ReadSingle(), buffer.ReadSingle())
                    ))
                );
            }
        }
    }
}
