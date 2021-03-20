using Godot;

namespace Client.screens
{
    public class ScreensManager : Control
    {
        public static ScreensManager Instance;

        public override void _Ready()
        {
            Instance = this;
        }

        public override void _ExitTree()
        {
            Instance = null;
        }
    }
}
