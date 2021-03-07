using System;
using global::Client.scripts.global;
using Godot;

namespace Client.scripts.components.creature
{
    public class CreatureInfoController : Node2D
    {
        private readonly uint _healthBarWidth = 64;
        private readonly uint _healthBarHeight = 5;
        private readonly Label _nameLabel;
        private readonly Sprite _healthSprite;

        public CreatureInfoController()
        {
            ZIndex = (int) RenderLayer.Stats;
            Name = "CreatureInfoController";
            _nameLabel = new Label
            {
                Name = "Name",
                Align = Label.AlignEnum.Center,
                GrowHorizontal = Control.GrowDirection.Both,
                RectPosition = new Vector2(0, -55f)
            };
            _nameLabel.AddColorOverride("font_color_shadow", Colors.Black);
            _nameLabel.AddColorOverride("font_color", Colors.White);
            AddChild(_nameLabel);

            var healthBackgroundSprite = new Sprite
            {
                Texture = (Texture) ResourceLoader.Load("res://assets/background.png"),
                Centered = false,
                RegionEnabled = true,
                RegionRect = new Rect2(Vector2.Zero, new Vector2(_healthBarWidth, _healthBarHeight)),
                Position = new Vector2(-32, -40),
                SelfModulate = Colors.Black,
                Name = "Health Bar Background",
            };
            AddChild(healthBackgroundSprite);

            _healthSprite = new Sprite
            {
                Texture = (Texture) ResourceLoader.Load("res://assets/background.png"),
                Centered = false,
                RegionEnabled = true,
                SelfModulate = Colors.DarkRed,
                Name = "Health Bar Value"
            };
            healthBackgroundSprite.AddChild(_healthSprite);
        }

        public void UpdateName(string name)
        {
            _nameLabel.Text = name;
        }

        public void UpdateHealth(uint current, uint max)
        {
            var newHealthBarWidth = current / (float) max * _healthBarWidth;
            _healthSprite.RegionRect = new Rect2(Vector2.Zero, new Vector2(newHealthBarWidth, _healthBarHeight));
        }
    }
}
