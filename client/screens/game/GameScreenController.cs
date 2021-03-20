using Godot;

namespace Client.screens.game
{
    public class GameScreenController : PanelContainer
    {
        public override void _Input(InputEvent @event)
        {
            if (@event.IsActionPressed("inventory"))
            {
                ScreensManager.Instance.ToggleInventoryScreen();
            }
        }
    }
}
