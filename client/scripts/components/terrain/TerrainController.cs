using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.terrain
{
    public class TerrainController : Node2D
    {
        private static int TILE_SIZE = 64;
        public void DrawTerrain(IngressDataPacket.TerrainUpdate action)
        {
            var gridX = 0;
            var gridY = 0;
            for (var i = 0; i < action.SpriteIds.Length; i++)
            {
                var worldPosition = new Vector2(
                    x: gridX * TILE_SIZE + action.WindowGridStartPositionX,
                    y: gridY * TILE_SIZE + action.WindowGridStartPositionY
                );
                var spriteId = action.SpriteIds[i];
                DrawTile(spriteId, worldPosition);

                gridY++;
                if (gridY == action.WindowHeight)
                {
                    gridX++;
                    gridY = 0;
                }
            }
        }

        private void DrawTile(ushort spriteId, Vector2 position)
        {
            var tile = new Sprite
            {
                Texture = (Texture) ResourceLoader.Load($"res://assets/tiles/{spriteId}.png"),
                Name = "Sprite",
                Centered = true,
                Position = position
            };
            AddChild(tile);
        }
    }
}
