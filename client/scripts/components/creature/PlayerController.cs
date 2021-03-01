using System;
using Client.scripts.global.udp.egress;
using Godot;

namespace Client.scripts.components.creature
{
    public class PlayerController : CreatureController
    {
        public override void _Ready()
        {
            Console.WriteLine("Hello from Main Player Controller C#");
        }

        public override void _PhysicsProcess(float delta)
        {
            var mask = GetInput();
            if (mask != 0)
            {
                ActionSenderMono.Instance.Send(new EgressDataPacket.PositionChange(mask));
            }
        }

        private byte GetInput()
        {
            byte mask = 0;

            if (Input.IsActionPressed("up")) mask |= 0x1;
            if (Input.IsActionPressed("down")) mask |= 0x4;
            if (Input.IsActionPressed("right")) mask |= 0x2;
            if (Input.IsActionPressed("left")) mask |= 0x8;

            return mask;
        }
    }
}
