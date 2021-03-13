using System;
using Client.scripts.global.udp.egress;
using Godot;

namespace Client.scripts.components.creature
{
    public class PlayerController : CreatureController
    {
        private Camera2D _camera2D;

        public override void _Ready()
        {
            Console.WriteLine("Hello from Main Player Controller C#");

            _camera2D = new Camera2D
            {
                Current = true,
                SmoothingEnabled = true,
            };

            AddChild(_camera2D);
        }


        public override void _Input(InputEvent @event)
        {
            if (@event is InputEventMouseButton eventMouseButton)
            {
                if (eventMouseButton.Pressed && eventMouseButton.ButtonIndex == (int) ButtonList.Right)
                {
                    ActionSenderMono.Instance.Send(new EgressDataPacket.PositionChange(GetGlobalMousePosition() / 64f));
                }
            }
        }
    }
}
