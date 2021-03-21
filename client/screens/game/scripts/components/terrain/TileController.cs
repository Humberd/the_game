using Client.scripts.global;
using Godot;
using Font = Godot.Font;

namespace Client.screens.game.scripts.components.terrain
{
    public class TileController : Sprite3D
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

            Translate(new Vector3(gridCoordinates.x, gridCoordinates.y, 0));
            Scale = new Vector3(100f / _size, 100f / _size, 0f);
            // ZIndex = (int) RenderLayer.BackgroundTerrain;
            _font = new Label().GetFont("");
            Centered = false;
            RegionEnabled = true;
            RegionRect = new Rect2
            {
                Size = _vecSize
            };
            Name = $"Tile({_gridCoordinates.x}, {_gridCoordinates.y})";
        }




        // public override void _Draw()
        // {
        //     DrawRect(new Rect2(Vector2.Zero, _vecSize), Colors.Black, false);
        //     DrawString(_font, new Vector2(0, _size / 2), $"{_gridCoordinates.x}, {_gridCoordinates.y}");
        // }

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
