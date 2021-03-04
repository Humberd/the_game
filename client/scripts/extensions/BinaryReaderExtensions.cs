using System;
using System.Collections.Generic;
using System.IO;

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

    }
}
