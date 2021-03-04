using System.Collections.Generic;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class ItemsPlaneController : Node2D
    {
        private readonly Dictionary<uint, ItemController> _items = new Dictionary<uint, ItemController>();

        public void DrawItems(IngressDataPacket.TerrainItemsUpdate action)
        {
            foreach (var actionItem in action.Items)
            {
                DrawItem(actionItem);
            }
        }

        private void DrawItem(IngressDataPacket.TerrainItemsUpdate.ItemData item)
        {
            if (_items.ContainsKey(item.InstanceId))
            {
                // fixme: should handle updating of the items
                return;
            }

            var itemController = new ItemController(item);
            _items[item.InstanceId] = itemController;
            AddChild(itemController);
        }
    }
}
