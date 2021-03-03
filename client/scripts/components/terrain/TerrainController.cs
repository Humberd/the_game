using System;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class TerrainController : Node2D
    {
        private static int GRID_SIZE = 20;
        private static int TILE_SIZE = 64;

        private TileController[,] _tiles = new TileController[GRID_SIZE, GRID_SIZE];

        public override void _Ready()
        {
            Name = "TerrainController";

            for (int x = 0; x < GRID_SIZE; x++)
            {
                for (int y = 0; y < GRID_SIZE; y++)
                {
                    var tile = new TileController(TILE_SIZE, new Vector2(x, y));
                    _tiles[x, y] = tile;
                    AddChild(tile);
                }
            }
        }

        public void DrawTerrain(IngressDataPacket.TerrainUpdate action)
        {
            var startX = action.WindowGridStartPositionX;
            var startY = action.WindowGridStartPositionY;

            var endX = startX + action.WindowWidth;
            var endY = startY + action.WindowHeight;

            ClearAllTiles();

            for (int x = startX; x < endX; x++)
            {
                for (int y = startY; y < endY; y++)
                {
                    var offsetX = x - startX;
                    var offsetY = y - startY;
                    var index = offsetX * action.WindowHeight + offsetY;
                    var spriteId = action.SpriteIds[index];
                    _tiles[x, y].SetTile(spriteId);
                }
            }
        }

        private void ClearAllTiles()
        {
            foreach (var tileController in _tiles)
            {
                tileController.UnsetTile();
            }
        }
    }
}
