using Client.screens.inventory.stats;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.inventory
{
    public class InventoryScreenController : PanelContainer
    {
        public static InventoryScreenController Instance;

        private StatsPanelController _statsPanelController;
        public override void _Ready()
        {
            Instance = this;
            ActionSenderMono.Instance.Send(new EgressDataPacket.PlayerStatsUpdateRequest());

            _statsPanelController = GetNode<StatsPanelController>("HSplitContainer/StatsPanel");
        }

        public override void _ExitTree()
        {
            Instance = null;
        }

        public override void _Input(InputEvent @event)
        {
            if (@event.IsActionPressed("inventory"))
            {
                ScreensManager.Instance.ToggleInventoryScreen();
            }
            GetTree().SetInputAsHandled();
        }

        public void DisplayStats(IngressDataPacket.CreatureStatsUpdate action)
        {
            _statsPanelController.LoadStats(action);
        }
    }
}
