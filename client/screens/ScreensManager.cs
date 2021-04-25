using Godot;

namespace Client.screens
{
	public class ScreensManager : Control
	{
		public static ScreensManager Instance;

		[Export] private PackedScene _loginScreen;
		private Node _loginScreenNode;
		[Export] private PackedScene _gameScreen;
		private Node _gameScreenNode;
		[Export] private PackedScene _inventoryScreen;
		private Node _inventoryScreenNode;

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

		public void LoadGame()
		{
			_loginScreenNode.QueueFree();
			_loginScreenNode = null;

			_gameScreenNode = _gameScreen.Instance();
			AddChild(_gameScreenNode);
		}

		public void ToggleInventoryScreen()
		{
			if (_inventoryScreenNode == null)
			{
				_inventoryScreenNode = _inventoryScreen.Instance();
				AddChild(_inventoryScreenNode);
			}
			else
			{
				_inventoryScreenNode.QueueFree();
				_inventoryScreenNode = null;
			}
		}
	}
}
