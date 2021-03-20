using Client.scripts.global;
using Godot;

namespace Client.screens.game.scripts.components.spell
{
    public class EffectNode : Sprite
    {
        public void Init(Vector2 position, ushort spriteId, uint duration)
        {
            Position = position;
            Texture = GameResourceLoader.GetSprite(spriteId);
            SetTime(duration);
            ZIndex = (int) RenderLayer.Effects;
        }

        private async void SetTime(uint duration)
        {
            await ToSignal(GetTree().CreateTimer(duration / 1000f), "timeout");
            QueueFree();
        }
    }
}
