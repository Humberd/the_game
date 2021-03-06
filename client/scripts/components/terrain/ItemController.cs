﻿using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class ItemController : KinematicBody2D
    {
        private readonly Sprite _sprite;

        private uint _iid;
        private uint _type;

        private bool _isDragging;

        public ItemController()
        {
            _sprite = new Sprite
            {
                Centered = true
            };
            AddChild(_sprite);

            InputPickable = true;
            CollisionLayer = 1;
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
                            iid: _iid,
                            position: Position + GetLocalMousePosition()
                        ));
                        GetTree().SetInputAsHandled();
                    }
                }
            }
        }

        public void UpdateData(IngressDataPacket.TerrainItemsUpdate.ItemData itemData)
        {
            _iid = itemData.Iid;
            _type = itemData.Type;
            Position = itemData.Position;
            // fixme: Should be retrieved from json store
            _sprite.Texture = (Texture) ResourceLoader.Load($"res://assets/resources/sprites/{_type}.png");
        }
    }
}
