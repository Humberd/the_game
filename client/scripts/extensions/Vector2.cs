using Godot;

namespace Client.scripts.extensions
{
    public static class Vector2Extensions
    {
        public static Vector3 To3D(this Vector2 vector)
        {
            return new(vector.x, 0, vector.y);
        }
    }
}
