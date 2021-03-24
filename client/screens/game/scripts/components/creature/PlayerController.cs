using System;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using Client.scripts.extensions;
using Client.scripts.global.udp.egress;
using Godot;

namespace Client.screens.game.scripts.components.creature
{
    public class PlayerController : Spatial
    {
        private bool _isRequestingPositionChange;
        private Camera _camera;
        private Subject<bool> _requestPositionChange = new();
        private Subject<bool> _unsubscribe = new();

        public override void _Ready()
        {
            GD.Print("Hello from Main Player Controller C#");
            _camera = GetNode<Camera>("Camera");
            _requestPositionChange
                .ThrottleTime(TimeSpan.FromMilliseconds(200))
                .TakeUntil(_unsubscribe)
                .Subscribe(RxObserverAdapter<bool>.Get(_ => SendPositionChange()));
        }
        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            _unsubscribe.OnNext(true);
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
                        RequestPositionUpdate();
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
                RequestPositionUpdate();
            }
        }

        private void RequestPositionUpdate()
        {
            _requestPositionChange.OnNext(true);
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
