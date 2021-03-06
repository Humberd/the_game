using System;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;
using Object = Godot.Object;

namespace Client.scripts.components.terrain
{
    public class ItemController : KinematicBody2D
    {
        private uint _instanceId;
        private ushort _itemId;

        private readonly Sprite _sprite;

        private bool _isDragging;

        public ItemController(IngressDataPacket.TerrainItemsUpdate.ItemData itemData)
        {
            _sprite = new Sprite
            {
                Centered = true
            };
            AddChild(_sprite);

            InputPickable = true;
            CollisionLayer = 1;
            Update(itemData);
        }

        public override void _Input(InputEvent @event)
        {
            if (@event is InputEventMouseButton mouseEvent)
            {
                if (mouseEvent.ButtonIndex == (int) ButtonList.Left)
                {
                    if (mouseEvent.Pressed && _sprite.IsPixelOpaque(GetLocalMousePosition()))
                    {
                        _isDragging = true;
                        GetTree().SetInputAsHandled();
                    }

                    if (!mouseEvent.Pressed && _isDragging)
                    {
                        _isDragging = false;
                        ActionSenderMono.Instance.Send(new EgressDataPacket.TerrainItemDrag(
                            itemInstanceId: _instanceId,
                            targetPosition: Position + GetLocalMousePosition()
                        ));
                        GetTree().SetInputAsHandled();
                    }
                }
            }
        }

        public void Update(IngressDataPacket.TerrainItemsUpdate.ItemData itemData)
        {
            _instanceId = itemData.InstanceId;
            _itemId = itemData.ItemId;
            Position = itemData.Position;
            // fixme: itemId is not a spriteId.
            _sprite.Texture = (Texture) ResourceLoader.Load($"res://assets/resources/items/sprites/{_itemId}.png");
        }
    }
}
