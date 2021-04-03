using System;
using System.Diagnostics;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.game.scripts.ui
{
    public class DebugInfoController : Control
    {
        public static DebugInfoController Instance { get; private set; }

        [Export] private NodePath _fpsLabelPath;
        private Label _fpsLabel;

        [Export] private NodePath _pingLabelPath;
        private Label _pingLabel;

        private float _deltaSum;
        private byte _framesCount;

        private bool _shouldSendPingRequest = true;
        private int _pingRequestTimeMillis;

        public override void _Ready()
        {
            Instance = this;
            _fpsLabel = GetNode<Label>(_fpsLabelPath);
            _pingLabel = GetNode<Label>(_pingLabelPath);
        }

        protected override void Dispose(bool disposing)
        {
            Instance = null;
            base.Dispose(disposing);
        }

        public override void _Process(float delta)
        {
            _deltaSum += delta;
            if (_deltaSum >= 1)
            {
                UpdateFrameCountLabel(_framesCount);
                _deltaSum = 0;
                _framesCount = 0;
            }
            else
            {
                _framesCount++;
            }
        }

        public override void _PhysicsProcess(float delta)
        {
            if (_shouldSendPingRequest)
            {
                _shouldSendPingRequest = false;
                _pingRequestTimeMillis = DateTime.Now.Millisecond;
                ActionSenderMono.Instance.Send(new EgressDataPacket.PingRequest());
                Debug.Print("Send ping Request");
            }
        }

        private void UpdateFrameCountLabel(byte framesCount)
        {
            _fpsLabel.Text = $"{framesCount} fps";
        }

        public void ReceivePingResponse(IngressDataPacket.PingResponse action)
        {
            Debug.Print("Receive ping Request");
            var nowMilliseconds = DateTime.Now.Millisecond;
            var diff = nowMilliseconds - _pingRequestTimeMillis;
            UpdatePingCountLabel(diff);
            SchedulePingRequest();
        }

        private void UpdatePingCountLabel(int pingMs)
        {
            _pingLabel.Text = $"{pingMs} ms";
        }

        private async void SchedulePingRequest()
        {
            int millis = 1000;
            await ToSignal(GetTree().CreateTimer(millis / 1000f), "timeout");
            _shouldSendPingRequest = true;
        }
    }
}
