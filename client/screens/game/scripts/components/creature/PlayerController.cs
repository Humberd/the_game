using System;
using Godot;

namespace Client.screens.game.scripts.components.creature
{
    public class PlayerController : Spatial
    {
        private bool _isRequestingPositionChange;

        public override void _Ready()
        {
            Console.WriteLine("Hello from Main Player Controller C#");
        }

        public override void _Input(InputEvent @event)
        {
            if (@event is InputEventMouseButton eventMouseButton)
            {
                if (eventMouseButton.ButtonIndex == (int) ButtonList.Right)
                {
                    if (eventMouseButton.Pressed)
                    {
                        _isRequestingPositionChange = true;
                        SendPositionChange();
                    }
                    else
                    {
                        _isRequestingPositionChange = false;
                    }
                }
            }
        }

        public override void _Process(float delta)
        {
            if (_isRequestingPositionChange)
            {
                SendPositionChange();
            }
        }

        private void SendPositionChange()
        {
            // ActionSenderMono.Instance.Send(new EgressDataPacket.PositionChange(GetGlobalMousePosition() / 64f));
        }
    }
}
