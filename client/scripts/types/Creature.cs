using Godot;
using PID = System.UInt32;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.scripts.types
{
    public class Creature
    {
        public CID Cid;
        public string Name;
        public uint Health;
        public Vector2 Position;
        public SpriteId SpriteId;

        public Creature(uint cid, string name, uint health, Vector2 position, ushort spriteId)
        {
            Cid = cid;
            Name = name;
            Health = health;
            Position = position;
            SpriteId = spriteId;
        }
    }

    public class Player : Creature
    {
        public PID Pid;

        public Player(uint pid, uint cid, string name, uint health, Vector2 position, ushort spriteId)
            : base(cid, name, health, position, spriteId)
        {
            Pid = pid;
        }
    }
}
