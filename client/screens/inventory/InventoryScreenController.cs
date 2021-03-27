using Client.screens.inventory.backpack;
using Client.screens.inventory.equipment;
using Client.screens.inventory.stats;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.inventory
{
    public class InventoryScreenController : PanelContainer
    {
        public static InventoryScreenController Instance;

        [Export] private NodePath _statsPanelPath;
        private StatsPanelController _statsPanelController;

        [Export] private NodePath _equipmentPanelPath;
        private EquipmentPanelController _equipmentPanelController;

        [Export] private NodePath _backpackPanelPath;
        private BackpackPanelController _backpackPanelController;
        public override void _Ready()
        {
            Instance = this;
            ActionSenderMono.Instance.Send(new EgressDataPacket.PlayerStatsUpdateRequest());

            _statsPanelController = GetNode<StatsPanelController>(_statsPanelPath);
            _equipmentPanelController = GetNode<EquipmentPanelController>(_equipmentPanelPath);
            _backpackPanelController = GetNode<BackpackPanelController>(_backpackPanelPath);
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            Instance = null;

        }

        public override void _Input(InputEvent @event)
        {
            if (@event.IsActionPressed("inventory"))
            {
                ScreensManager.Instance.ToggleInventoryScreen();
            }
            // GetTree().SetInputAsHandled();
        }

        public void DisplayStats(IngressDataPacket.CreatureStatsUpdate action)
        {
            _statsPanelController.LoadStats(action);
        }

        public void DisplayBackpack(IngressDataPacket.BackpackUpdate action)
        {
            _backpackPanelController.LoadBackpack(action);
        }
    }
}
