using Godot;

namespace Client.scripts.global
{
    public static class GameResourceLoader
    {
        public static Texture GetSprite(uint spriteId)
        {
            return (Texture) ResourceLoader.Load($"res://assets/resources/sprites/{spriteId}.png");
        }

        public static Texture GetTile(uint spriteId)
        {
            return (Texture) ResourceLoader.Load($"res://assets/tiles/{spriteId}.png");
        }
    }
}
