using Godot;

namespace Client.scripts.components.creature
{
    public class CreatureController : Node2D
    {
        public void Load(string name, Vector2 position)
        {
            var sprite = new Sprite
            {
                Texture = (Texture) ResourceLoader.Load("res://assets/player_avatar.png"),
                Name = "Sprite",
                Centered = true,
                Scale = new Vector2(0.35f, 0.35f)
            };

            AddChild(sprite);

            Name = name;
            Position = position;
        }

        public void UpdatePosition(Vector2 position)
        {
            Position = position;
        }
    }
}
