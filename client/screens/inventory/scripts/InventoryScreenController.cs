using Godot;

namespace Client.screens.inventory.scripts
{
    public class InventoryScreenController : PanelContainer
    {
        public override void _Input(InputEvent @event)
        {
            if (@event.IsActionPressed("inventory"))
            {
                ScreensManager.Instance.ToggleInventoryScreen();
            }
            GetTree().SetInputAsHandled();
        }
    }
}
