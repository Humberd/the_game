using System;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using Client.scripts.extensions;
using Client.scripts.global.udp.ingress;
using Godot;
using CID = System.UInt32;
using SpriteId = System.UInt16;

namespace Client.screens.game.scripts.components.creature
{
    public class CreatureController : Spatial
    {
        [Export] private NodePath _bodyPath;
        private BodyController _body;

        [Export] private NodePath _bodyAreaPath;
        private Spatial _bodyArea;

        private CreatureInfoController _creatureInfoController = new();

        private CID _cid;
        private uint _health;
        private string _name;
        private float _attackTriggerRadius;
        private byte _isBeingAttackedByMe;

        private SpriteId _spriteId;

        private Subject<bool> _isMoving = new();
        private readonly Subject<bool> _unsubscribe = new();

        public override void _Ready()
        {
            _body = GetNode<BodyController>(_bodyPath);
            _bodyArea = GetNode<Spatial>(_bodyAreaPath);
            _isMoving
                // This is really a debounce
                .Throttle(TimeSpan.FromMilliseconds(100))
                .TakeUntil(_unsubscribe)
                .Subscribe(RxObserverAdapter<bool>.Get(_ => _body.StopWalking()));
        }

        protected override void Dispose(bool disposing)
        {
            _unsubscribe.OnNext(true);
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
            var radsAngle = Transform.origin.to2D().AngleToPoint(position);
            Translation = position.To3D();
            if (radsAngle != 0)
            {
                _body.Rotation = new Vector3(0, -radsAngle - Mathf.Deg2Rad(90), 0);
            }

            _body.StartWalking();
            _isMoving.OnNext(true);
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

        public void UpdateBodyRadius(float bodyRadius)
        {
            _bodyArea.Scale = new Vector3(bodyRadius, 0.01f, bodyRadius);
        }

        public void UpdateData(IngressDataPacket.PlayerUpdate playerUpdate)
        {
            UpdateCid(playerUpdate.Cid);
            UpdatePosition(playerUpdate.Position);
            UpdateName(playerUpdate.Name);
            UpdateHealth(playerUpdate.BaseHealth, playerUpdate.CurrentHealth);
            UpdateOutfit(playerUpdate.SpriteId);
            UpdateBodyRadius(playerUpdate.BodyRadius);
            _attackTriggerRadius = playerUpdate.AttackTriggerRadius;
            _isBeingAttackedByMe = playerUpdate.IsBeingAttackedByMe;
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
