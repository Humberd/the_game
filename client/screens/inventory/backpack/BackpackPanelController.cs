using System.Collections.Generic;
using Client.screens.inventory.equipment;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.inventory.backpack
{
    public class BackpackPanelController : Control
    {
        [Export] private PackedScene _equipmentSlotTemplate;
        [Export] private NodePath _gridPath;
        private GridContainer _grid;

        private readonly List<EquipmentSlotController> _slots = new();


        public override void _Ready()
        {
            _grid = GetNode<GridContainer>(_gridPath);
        }

        public void LoadBackpack(IngressDataPacket.BackpackUpdate action)
        {
            for (var index = 0; index < action.Items.Length; index++)
            {
                var slotData = action.Items[index];
                EquipmentSlotController slotController;
                // Controller doesn't exist
                if (index >= _slots.Count)
                {
                    slotController = (EquipmentSlotController) _equipmentSlotTemplate.Instance();
                    _grid.AddChild(slotController);
                    _slots.Add(slotController);
                }
                else
                {
                    slotController = _slots[index];
                }

                if (slotData == null)
                {
                    slotController.SetEmpty();
                }
                else
                {
                    slotController.LoadData(slotData);
                }
            }
        }
    }
}
