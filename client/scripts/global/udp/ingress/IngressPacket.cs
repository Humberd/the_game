using System.IO;
using Client.scripts.extensions;
using Godot;
using PID = System.UInt32;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.scripts.global.udp.ingress
{
    public enum IngressPacketType
    {
        CREATURE_UPDATE = 0x20,
        CREATURE_DISAPPEAR = 0x21,
        CREATURE_POSITION_UPDATE = 0x22,
        TERRAIN_UPDATE = 0x23,
        TERRAIN_ITEMS_UPDATE = 0x24,
        PLAYER_DETAILS = 0x25
    }

    public class IngressDataPacket
    {
        public class PlayerUpdate : IngressDataPacket
        {
            public readonly CID Cid;
            public readonly string Name;
            public readonly uint Health;
            public readonly Vector2 Position;
            public readonly SpriteId SpriteId;

            public PlayerUpdate(uint cid, string name, uint health, Vector2 position, ushort spriteId)
            {
                Cid = cid;
                Name = name;
                Health = health;
                Position = position;
                SpriteId = spriteId;
            }

            public static PlayerUpdate From(BinaryReader buffer)
            {
                return new PlayerUpdate(
                    cid: buffer.ReadUInt32(),
                    name: buffer.ReadServerString(),
                    health: buffer.ReadUInt32(),
                    position: buffer.ReadVector2(),
                    spriteId: buffer.ReadUInt16()
                );
            }
        }

        public class CreatureDisappear : IngressDataPacket
        {
            public readonly PID Pid;

            private CreatureDisappear(uint pid)
            {
                Pid = pid;
            }

            public static CreatureDisappear From(BinaryReader buffer)
            {
                return new CreatureDisappear(
                    pid: buffer.ReadUInt32()
                );
            }
        }

        public class CreaturePositionUpdate : IngressDataPacket
        {
            public readonly CID Cid;
            public readonly Vector2 Position;

            private CreaturePositionUpdate(uint cid, Vector2 position)
            {
                Cid = cid;
                Position = position;
            }

            public static CreaturePositionUpdate From(BinaryReader buffer)
            {
                return new CreaturePositionUpdate(
                    cid: buffer.ReadUInt32(),
                    position: buffer.ReadVector2()
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
                    spriteIds: buffer.ReadServerArray(buffer.ReadUInt16)
                );
            }
        }

        public class TerrainItemsUpdate : IngressDataPacket
        {
            public readonly ItemData[] Items;

            public class ItemData
            {
                public readonly uint Iid;
                public readonly uint Type;
                public readonly Vector2 Position;

                public ItemData(uint iid, uint type, Vector2 position)
                {
                    Iid = iid;
                    Type = type;
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
                        iid: buffer.ReadUInt32(),
                        type: buffer.ReadUInt32(),
                        position: buffer.ReadVector2()
                    ))
                );
            }
        }

        public class PlayerDetails : IngressDataPacket
        {
            public readonly PID Pid;
            public readonly CID Cid;

            private PlayerDetails(uint pid, uint cid)
            {
                Pid = pid;
                Cid = cid;
            }

            public static PlayerDetails From(BinaryReader buffer)
            {
                return new PlayerDetails(
                    pid: buffer.ReadUInt32(),
                    cid: buffer.ReadUInt32()
                );
            }
        }
    }
}
