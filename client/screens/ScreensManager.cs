using Godot;

namespace Client.screens
{
    public class ScreensManager : Control
    {
        public static ScreensManager Instance;

        [Export] private PackedScene _loginScreen;
        private Node _loginScreenNode;
        [Export] private PackedScene _gameScreen;

        public override void _Ready()
        {
            Instance = this;
            _loginScreenNode = _loginScreen.Instance();
            AddChild(_loginScreenNode);
        }

        public override void _ExitTree()
        {
            Instance = null;
        }
    }
}
