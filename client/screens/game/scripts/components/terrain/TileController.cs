using Client.scripts.global;
using Godot;
using Font = Godot.Font;

namespace Client.screens.game.scripts.components.terrain
{
    public class TileController : Sprite
    {
        private readonly int _size;
        private readonly Vector2 _gridCoordinates;
        private readonly Vector2 _vecSize;
        private Font _font;

        public TileController(int size, Vector2 gridCoordinates)
        {
            _size = size;
            _gridCoordinates = gridCoordinates;
            _vecSize = new Vector2(_size, _size);

            ZIndex = (int) RenderLayer.BackgroundTerrain;
            Position = gridCoordinates * size;
            _font = new Label().GetFont("");
            Centered = false;
            RegionRect = new Rect2
            {
                Size = _vecSize
            };
            Name = $"Tile({_gridCoordinates.x}, {_gridCoordinates.y})";
        }

        public override void _Draw()
        {
            DrawRect(new Rect2(Vector2.Zero, _vecSize), Colors.Black, false);
            DrawString(_font, new Vector2(0, _size / 2), $"{_gridCoordinates.x}, {_gridCoordinates.y}");
        }

        public void SetTile(ushort spriteId)
        {
            Texture = GameResourceLoader.GetTile(spriteId);
        }

        public void UnsetTile()
        {
            Texture = null;
        }
    }
}
