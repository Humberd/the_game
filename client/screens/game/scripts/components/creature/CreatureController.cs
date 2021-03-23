using Client.scripts.extensions;
using Client.scripts.global;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.screens.game.scripts.components.creature
{
    public class CreatureController : Spatial
    {
        private readonly CreatureInfoController _creatureInfoController;

        private CID _cid;
        private uint _health;
        private string _name;
        private float _bodyRadius;
        private float _attackTriggerRadius;
        private byte _isBeingAttackedByMe;

        private SpriteId _spriteId;

        public CreatureController()
        {
            // ZIndex = (int) RenderLayer.Creatures;

            _creatureInfoController = new CreatureInfoController();
            // AddChild(_creatureInfoController);
        }

        public void SetIsMe(bool isMe)
        {
            if (!isMe)
            {
                GetNode("Player").QueueFree();
            }
        }

        public void UpdateCid(CID cid)
        {
            _cid = cid;
            Name = $"Creature-{cid}";
        }

        public void UpdatePosition(Vector2 position)
        {

            Translate(position.To3D());
        }

        public void UpdateName(string name)
        {
            if (name != _name)
            {
                _name = name;
                _creatureInfoController.UpdateName(name);
            }
        }

        public void UpdateHealth(uint baseHealth, uint currentHealth)
        {
            if (_health != currentHealth)
            {
                _health = currentHealth;
                _creatureInfoController.UpdateHealth(currentHealth, baseHealth);
            }
        }

        public void UpdateOutfit(SpriteId spriteId)
        {
            if (_spriteId != spriteId)
            {
                _spriteId = spriteId;
                // update creature outfit
            }
        }

        public void UpdateData(IngressDataPacket.PlayerUpdate playerUpdate)
        {
            UpdateCid(playerUpdate.Cid);
            UpdatePosition(playerUpdate.Position);
            UpdateName(playerUpdate.Name);
            UpdateHealth(playerUpdate.BaseHealth, playerUpdate.CurrentHealth);
            UpdateOutfit(playerUpdate.SpriteId);
            _bodyRadius = playerUpdate.BodyRadius * 64;
            _attackTriggerRadius = playerUpdate.AttackTriggerRadius;
            _isBeingAttackedByMe = playerUpdate.IsBeingAttackedByMe;
            // Update();
        }

        public override void _Input(InputEvent @event)
        {
            if (@event is InputEventMouseButton mouseEvent)
            {
                if (mouseEvent.ButtonIndex == (int) ButtonList.Left)
                {
                    // if (mouseEvent.Pressed && _sprite.IsPixelOpaque(GetLocalMousePosition()))
                    // {
                    //     if (_isBeingAttackedByMe == 0)
                    //     {
                    //         ActionSenderMono.Instance.Send(new EgressDataPacket.BasicAttackStart(_cid));
                    //     }
                    //     else
                    //     {
                    //         ActionSenderMono.Instance.Send(new EgressDataPacket.BasicAttackStop());
                    //     }
                    //     GetTree().SetInputAsHandled();
                    // }
                }
            }
        }
    }
}
