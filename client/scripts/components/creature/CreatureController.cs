using System;
using Client.scripts.global.udp.ingress;
using global::Client.scripts.global;
using Godot;
using PID = System.UInt32;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.scripts.components.creature
{
    public class CreatureController : KinematicBody2D
    {
        private readonly Sprite _sprite;
        private readonly CreatureInfoController _creatureInfoController;

        private CID _cid;
        private uint _health;
        private string _name;
        private ushort _bodyRadius;
        private ushort _attackTriggerRadius;

        private SpriteId _spriteId;

        public CreatureController()
        {
            ZIndex = (int) RenderLayer.Creatures;
            _sprite = new Sprite
            {
                Name = "Sprite",
                Centered = true
            };
            AddChild(_sprite);

            _creatureInfoController = new CreatureInfoController();
            AddChild(_creatureInfoController);
        }

        public override void _Draw()
        {
            // DrawRect(new Rect2(new Vector2(-32, -32), new Vector2(64, 64)), Colors.Chartreuse, false);
            if (_bodyRadius > 0)
            {
                DrawArc(Vector2.Zero, _bodyRadius, 0, 360, 60, new Color("#272822"));
            }

            if (_attackTriggerRadius > 0)
            {
                DrawArc(Vector2.Zero, _attackTriggerRadius, 0, 360, 60, new Color("#A7271F"));
            }
        }

        public void UpdateCid(CID cid)
        {
            _cid = cid;
            Name = $"Creature-{cid}";
        }

        public void UpdatePosition(Vector2 position)
        {
            Position = position * 64;
        }

        public void UpdateName(string name)
        {
            if (name != _name)
            {
                _name = name;
                _creatureInfoController.UpdateName(name);
            }
        }

        public void UpdateHealth(uint health)
        {
            if (_health != health)
            {
                _health = health;
                _creatureInfoController.UpdateHealth(health, 130);
            }
        }

        public void UpdateOutfit(SpriteId spriteId)
        {
            if (_spriteId != spriteId)
            {
                _spriteId = spriteId;
                _sprite.Texture = GameResourceLoader.GetSprite(spriteId);
            }
        }

        public void UpdateData(IngressDataPacket.PlayerUpdate playerUpdate)
        {
            UpdateCid(playerUpdate.Cid);
            UpdatePosition(playerUpdate.Position);
            UpdateName(playerUpdate.Name);
            UpdateHealth(playerUpdate.Health);
            UpdateOutfit(playerUpdate.SpriteId);
            _bodyRadius = playerUpdate.BodyRadius;
            _attackTriggerRadius = playerUpdate.AttackTriggerRadius;
        }
    }
}
