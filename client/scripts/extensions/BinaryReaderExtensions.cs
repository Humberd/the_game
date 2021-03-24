using System;
using System.IO;
using Godot;

namespace Client.scripts.extensions
{
    public static class BinaryReaderExtensions
    {
        public static string ReadServerString(this BinaryReader buffer)
        {
            var length = buffer.ReadUInt16();
            if (length == 0)
            {
                return "";
            }

            return System.Text.Encoding.UTF8.GetString(buffer.ReadBytes(length));
        }

        public static T[] ReadServerArray<T>(this BinaryReader buffer, Func<T> callback)
        {
            var length = buffer.ReadUInt16();
            if (length == 0)
            {
                return new T[0];
            }

            var result = new T[length];
            for (int i = 0; i < length; i++)
            {
                result[i] = callback.Invoke();
            }

            return result;
        }

        public static Vector2 ReadVector2(this BinaryReader buffer)
        {
            return new Vector2(buffer.ReadSingle(), buffer.ReadSingle());
        }

        public static void PutVector2(this StreamPeerBuffer buffer, Vector2 value)
        {
            buffer.PutFloat(value.x);
            buffer.PutFloat(value.y);
        }

    }
}
