using Client.scripts.global.udp.ingress;
using Godot;

using PID = System.UInt32;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.scripts.components.creature
{
    public class CreatureController : Node2D
    {
        private readonly Sprite _sprite;

        private CID _cid;
        private uint _health;
        private SpriteId _spriteId;

        public CreatureController()
        {
            _sprite = new Sprite
            {
                Name = "Sprite",
                Centered = true,
            };
            AddChild(_sprite);
        }

        public void UpdatePosition(Vector2 position)
        {
            Position = position;
        }

        public void UpdateName(string name)
        {
            Name = name;
        }

        public void UpdateHealth(uint health)
        {
            _health = health;
        }

        public void UpdateOutfit(SpriteId spriteId)
        {
            if (_spriteId != spriteId)
            {
                _spriteId = spriteId;
                _sprite.Texture = (Texture) ResourceLoader.Load($"res://assets/resources/sprites/{spriteId}.png");
            }
        }

        public void UpdateData(IngressDataPacket.PlayerUpdate playerUpdate)
        {
            _cid = playerUpdate.Cid;
            UpdatePosition(playerUpdate.Position);
            UpdateName(playerUpdate.Name);
            UpdateHealth(playerUpdate.Health);
            UpdateOutfit(playerUpdate.SpriteId);
        }
    }
}
