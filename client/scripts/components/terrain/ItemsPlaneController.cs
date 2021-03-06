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
            var idsToRemove = _items.Keys.Except(action.Items.Select(item => item.Iid));
            foreach (var idToRemove in idsToRemove.ToList())
            {
                var itemControllerToRemove = _items[idToRemove];
                itemControllerToRemove.QueueFree();
                _items.Remove(idToRemove);
            }


            foreach (var actionItem in action.Items)
            {
                if (_items.ContainsKey(actionItem.Iid))
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
            var itemController = new ItemController();
            _items[item.Iid] = itemController;
            itemController.UpdateData(item);
            AddChild(itemController);
        }

        private void ModifyItem(IngressDataPacket.TerrainItemsUpdate.ItemData item)
        {
            var itemController = _items[item.Iid];
            itemController.UpdateData(item);
        }
    }
}
