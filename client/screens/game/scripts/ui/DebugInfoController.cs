using Godot;

namespace Client.screens.game.scripts.ui
{
    public class DebugInfoController : Control
    {
        [Export] private NodePath _fpsLabelPath;
        private Label _fpsLabel;

        private float _deltaSum;
        private byte _framesCount;

        public override void _Ready()
        {
            _fpsLabel = GetNode<Label>(_fpsLabelPath);
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

        private void UpdateFrameCountLabel(byte framesCount)
        {
            _fpsLabel.Text = $"{framesCount} fps";
        }
    }
}
