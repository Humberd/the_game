using global::Client.scripts.global;
using Godot;

namespace Client.scripts.components.spell
{
    public class DamageNode : Node2D
    {
        private Font _font;
        private uint _damageAmount;

        public void Init(Vector2 damagePosition, uint damageAmount)
        {
            Position = damagePosition;
            _damageAmount = damageAmount;
            ZIndex = (int) RenderLayer.Damage;
            SetTime(1000);
            _font = new Label().GetFont("");
        }

        private async void SetTime(uint duration)
        {
            await ToSignal(GetTree().CreateTimer(duration / 1000f), "timeout");
            QueueFree();
        }

        public override void _Draw()
        {
            DrawString(_font, new Vector2(0,  0), $"{_damageAmount}", Colors.Black);

        }
    }
}
