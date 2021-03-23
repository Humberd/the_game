using Client.scripts.extensions;
using Client.scripts.global.udp.egress;
using Godot;

namespace Client.screens.game.scripts.components.creature
{
    public class PlayerController : Spatial
    {
        private bool _isRequestingPositionChange;
        private Camera _camera;

        public override void _Ready()
        {
            GD.Print("Hello from Main Player Controller C#");
            _camera = GetNode<Camera>("Camera");
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
            var rayLength = 1000;
            var mousePos = GetViewport().GetMousePosition();
            var from = _camera.ProjectRayOrigin(mousePos);
            var to = from + _camera.ProjectRayNormal(mousePos) * rayLength;
            var spaceState = GetWorld().DirectSpaceState;
            var result = spaceState.IntersectRay(from, to, collideWithAreas: true, collideWithBodies: false);
            if (result.Contains("position"))
            {
                var position = (Vector3) result["position"];
                ActionSenderMono.Instance.Send(new EgressDataPacket.PositionChange(position.to2D()));
            }
        }
    }
}
