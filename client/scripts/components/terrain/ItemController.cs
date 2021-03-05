﻿using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class ItemController : Sprite
    {
        private uint _instanceId;
        private ushort _itemId;

        public ItemController(IngressDataPacket.TerrainItemsUpdate.ItemData itemData)
        {
            Update(itemData);
            Centered = true;
        }

        public void Update(IngressDataPacket.TerrainItemsUpdate.ItemData itemData)
        {
            _instanceId = itemData.InstanceId;
            _itemId = itemData.ItemId;
            Position = itemData.Position;
            // fixme: itemId is not a spriteId.
            Texture = (Texture) ResourceLoader.Load($"res://assets/resources/items/sprites/{_itemId}.png");
        }
    }
}
