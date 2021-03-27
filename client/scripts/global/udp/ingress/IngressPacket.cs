#nullable enable
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
        PLAYER_DETAILS = 0x25,
        EQUIPPED_SPELLS_UPDATE = 0x26,
        SPELL_USE = 0x27,
        DAMAGE_TAKEN = 0x28,
        PROJECTILE_SEND = 0x29,
        EQUIPMENT_UPDATE = 0x2A,
        CREATURE_STATS_UPDATE = 0x2B,
        BACKPACK_UPDATE = 0x2C
    }

    public class IngressDataPacket
    {
        public class PlayerUpdate : IngressDataPacket
        {
            public readonly CID Cid;
            public readonly string Name;
            public readonly uint BaseHealth;
            public readonly uint CurrentHealth;
            public readonly Vector2 Position;
            public readonly SpriteId SpriteId;
            public readonly float BodyRadius;
            public readonly float AttackTriggerRadius;
            public readonly byte IsBeingAttackedByMe;

            public PlayerUpdate(uint cid, string name, uint baseHealth, uint currentHealth, Vector2 position,
                ushort spriteId,
                float bodyRadius, float attackTriggerRadius, byte isBeingAttackedByMe)
            {
                Cid = cid;
                Name = name;
                BaseHealth = baseHealth;
                CurrentHealth = currentHealth;
                Position = position;
                SpriteId = spriteId;
                BodyRadius = bodyRadius;
                AttackTriggerRadius = attackTriggerRadius;
                IsBeingAttackedByMe = isBeingAttackedByMe;
            }

            public static PlayerUpdate From(BinaryReader buffer)
            {
                return new PlayerUpdate(
                    cid: buffer.ReadUInt32(),
                    name: buffer.ReadServerString(),
                    baseHealth: buffer.ReadUInt32(),
                    currentHealth: buffer.ReadUInt32(),
                    position: buffer.ReadVector2(),
                    spriteId: buffer.ReadUInt16(),
                    bodyRadius: buffer.ReadSingle(),
                    attackTriggerRadius: buffer.ReadSingle(),
                    isBeingAttackedByMe: buffer.ReadByte()
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

        public class EquippedSpellsUpdate : IngressDataPacket
        {
            public readonly SpellUpdate[] Spells;

            public class SpellUpdate
            {
                public readonly uint Sid;
                public readonly string Name;
                public readonly ushort SpriteId;
                public readonly uint Cooldown;

                public SpellUpdate(uint sid, string name, ushort spriteId, uint cooldown)
                {
                    Sid = sid;
                    Name = name;
                    SpriteId = spriteId;
                    Cooldown = cooldown;
                }
            }

            private EquippedSpellsUpdate(SpellUpdate[] spells)
            {
                Spells = spells;
            }

            public static EquippedSpellsUpdate From(BinaryReader buffer)
            {
                return new EquippedSpellsUpdate(
                    spells: buffer.ReadServerArray(() =>
                    {
                        var sid = buffer.ReadUInt32();
                        if (sid == 0)
                        {
                            return null;
                        }

                        return new SpellUpdate(
                            sid: sid,
                            name: buffer.ReadServerString(),
                            spriteId: buffer.ReadUInt16(),
                            cooldown: buffer.ReadUInt32()
                        );
                    })
                );
            }
        }

        public class SpellUse : IngressDataPacket
        {
            public readonly Vector2 SourcePosition;
            public readonly SpellEffect[] Effects;

            public class SpellEffect
            {
                public readonly SpriteId SpriteId;
                public readonly uint Duration;

                public SpellEffect(ushort spriteId, uint duration)
                {
                    SpriteId = spriteId;
                    Duration = duration;
                }
            }

            private SpellUse(Vector2 sourcePosition, SpellEffect[] effects)
            {
                SourcePosition = sourcePosition;
                Effects = effects;
            }

            public static SpellUse From(BinaryReader buffer)
            {
                return new SpellUse(
                    sourcePosition: buffer.ReadVector2(),
                    effects: buffer.ReadServerArray(() => new SpellEffect(
                        spriteId: buffer.ReadUInt16(),
                        duration: buffer.ReadUInt32()
                    ))
                );
            }
        }

        public class DamageTaken
        {
            public readonly Damage[] Damages;

            public class Damage
            {
                public readonly Vector2 Position;
                public readonly uint Amount;

                public Damage(Vector2 position, uint amount)
                {
                    Position = position;
                    Amount = amount;
                }
            }

            private DamageTaken(Damage[] damages)
            {
                Damages = damages;
            }

            public static DamageTaken From(BinaryReader buffer)
            {
                return new DamageTaken(
                    damages: buffer.ReadServerArray(() => new Damage(
                        position: buffer.ReadVector2(),
                        amount: buffer.ReadUInt32()
                    ))
                );
            }
        }

        public class ProjectileSend
        {
            public readonly SpriteId SpriteId;
            public readonly Vector2 SourcePosition;
            public readonly Vector2 TargetPosition;
            public readonly uint Duration;

            public ProjectileSend(ushort spriteId, Vector2 sourcePosition, Vector2 targetPosition, uint duration)
            {
                SpriteId = spriteId;
                SourcePosition = sourcePosition;
                TargetPosition = targetPosition;
                Duration = duration;
            }

            public static ProjectileSend From(BinaryReader buffer)
            {
                return new ProjectileSend(
                    spriteId: buffer.ReadUInt16(),
                    sourcePosition: buffer.ReadVector2(),
                    targetPosition: buffer.ReadVector2(),
                    duration: buffer.ReadUInt32()
                );
            }
        }

        public class CreatureStatsUpdate
        {
            public readonly IntCreatureStatPacket Defense;
            public readonly IntCreatureStatPacket Attack;
            public readonly FloatCreatureStatPacket AttackSpeed;
            public readonly FloatCreatureStatPacket MovementSpeed;
            public readonly IntCreatureStatPacket HealthPool;

            public CreatureStatsUpdate(IntCreatureStatPacket defense, IntCreatureStatPacket attack,
                FloatCreatureStatPacket attackSpeed, FloatCreatureStatPacket movementSpeed,
                IntCreatureStatPacket healthPool)
            {
                Defense = defense;
                Attack = attack;
                AttackSpeed = attackSpeed;
                MovementSpeed = movementSpeed;
                HealthPool = healthPool;
            }

            public readonly struct IntCreatureStatPacket
            {
                public readonly int BaseValue;
                public readonly int CurrentValue;

                public IntCreatureStatPacket(int baseValue, int currentValue)
                {
                    BaseValue = baseValue;
                    CurrentValue = currentValue;
                }
            }

            public readonly struct FloatCreatureStatPacket
            {
                public readonly float BaseValue;
                public readonly float CurrentValue;

                public FloatCreatureStatPacket(float baseValue, float currentValue)
                {
                    BaseValue = baseValue;
                    CurrentValue = currentValue;
                }
            }

            public static CreatureStatsUpdate From(BinaryReader buffer)
            {
                return new CreatureStatsUpdate(
                    defense: ReadIntStat(buffer),
                    attack: ReadIntStat(buffer),
                    attackSpeed: ReadFloatStat(buffer),
                    movementSpeed: ReadFloatStat(buffer),
                    healthPool: ReadIntStat(buffer)
                );
            }

            private static IntCreatureStatPacket ReadIntStat(BinaryReader buffer)
            {
                return new IntCreatureStatPacket(
                    baseValue: buffer.ReadInt32(),
                    currentValue: buffer.ReadInt32()
                );
            }

            private static FloatCreatureStatPacket ReadFloatStat(BinaryReader buffer)
            {
                return new FloatCreatureStatPacket(
                    baseValue: buffer.ReadSingle(),
                    currentValue: buffer.ReadSingle()
                );
            }
        }

        public class BackpackUpdate
        {
            public readonly BackpackSlotDto?[] Items;

            public BackpackUpdate(BackpackSlotDto?[] items)
            {
                Items = items;
            }

            public class BackpackSlotDto
            {
                public readonly ushort ItemSchemaId;
                public readonly ushort StackCount;

                public BackpackSlotDto(ushort itemSchemaId, ushort stackCount)
                {
                    ItemSchemaId = itemSchemaId;
                    StackCount = stackCount;
                }
            }

            public static BackpackUpdate From(BinaryReader buffer)
            {
                return new BackpackUpdate(
                    items: buffer.ReadServerNullableArray(() => new BackpackSlotDto(
                        itemSchemaId: buffer.ReadUInt16(),
                        stackCount: buffer.ReadUInt16()
                    ))
                );
            }
        }
    }
}
