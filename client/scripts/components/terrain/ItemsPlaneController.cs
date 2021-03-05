using System.Collections.Generic;
using System.Linq;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class ItemsPlaneController : Node2D
    {
        private readonly Dictionary<uint, ItemController> _items = new Dictionary<uint, ItemController>();

        public void DrawItems(IngressDataPacket.TerrainItemsUpdate action)
        {
            var idsToRemove = _items.Keys.Except(action.Items.Select(item => item.InstanceId));
            foreach (var idToRemove in idsToRemove.ToList())
            {
                var itemControllerToRemove = _items[idToRemove];
                RemoveChild(itemControllerToRemove);
                _items.Remove(idToRemove);
            }


            foreach (var actionItem in action.Items)
            {
                if (_items.ContainsKey(actionItem.InstanceId))
                {
                    ModifyItem(actionItem);
                }
                else
                {
                    DrawItem(actionItem);
                }
            }
        }

        private void DrawItem(IngressDataPacket.TerrainItemsUpdate.ItemData item)
        {
            var itemController = new ItemController(item);
            _items[item.InstanceId] = itemController;
            AddChild(itemController);
        }

        private void ModifyItem(IngressDataPacket.TerrainItemsUpdate.ItemData item)
        {
            var itemController = _items[item.InstanceId];
            itemController.Update(item);
        }
    }
}
