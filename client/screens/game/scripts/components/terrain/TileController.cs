using Client.scripts.extensions;
using Client.scripts.global;
using Godot;

namespace Client.screens.game.scripts.components.terrain
{
    public class TileController : Sprite3D
    {
        private int _size;
        private Vector2 _gridCoordinates;
        private Vector2 _vecSize;

        // public override void _Draw()
        // {
        //     DrawRect(new Rect2(Vector2.Zero, _vecSize), Colors.Black, false);
        //     DrawString(_font, new Vector2(0, _size / 2), $"{_gridCoordinates.x}, {_gridCoordinates.y}");
        // }

        public void Load(int size, Vector2 gridCoordinates)
        {
            _size = size;
            _gridCoordinates = gridCoordinates;
            _vecSize = new Vector2(_size, _size);

            Translate(gridCoordinates.To3D());
            Name = $"Tile({_gridCoordinates.x}, {_gridCoordinates.y})";
            UnsetTile();
            // ZIndex = (int) RenderLayer.BackgroundTerrain;
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
