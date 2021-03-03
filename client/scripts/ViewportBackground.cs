using Godot;

namespace Client.scripts
{
    public class ViewportBackground : Sprite
    {
        public override void _Ready()
        {
            Texture = (Texture) ResourceLoader.Load("res://assets/background.png");
            Name = "Background";
            Centered = false;
            RegionEnabled = true;
            Offset = new Vector2(-1, -1);
            RegionRect = new Rect2(new Vector2(0, 0), new Vector2(602, 602));
            Modulate = new Color("262C3B");
        }
    }
}
