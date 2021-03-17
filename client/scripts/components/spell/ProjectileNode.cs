using System;
using global::Client.scripts.global;
using Godot;

namespace Client.scripts.components.spell
{
    public class ProjectileNode : Sprite
    {
        private float _unitsPerSecond;
        private Vector2 _direction;

        public void Init(ushort spriteId, Vector2 position, Vector2 targetPosition, uint duration)
        {
            Position = position;
            Texture = GameResourceLoader.GetSprite(spriteId);
            Rotation = position.AngleToPoint(targetPosition) - Mathf.Deg2Rad(90);
            _direction = position.DirectionTo(targetPosition);
            _unitsPerSecond = position.DistanceTo(targetPosition) / (duration / 1000f);
            SetTime(duration);
            ZIndex = (int) RenderLayer.Effects;
        }

        public override void _Process(float delta)
        {
            Position += _direction * delta * _unitsPerSecond;
        }

        private async void SetTime(uint duration)
        {
            await ToSignal(GetTree().CreateTimer(duration / 1000f), "timeout");
            QueueFree();
        }
    }
}
