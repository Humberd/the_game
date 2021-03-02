using System;
using System.Text;

namespace Client.scripts.extensions
{
    public class Utils
    {
        public static string ByteArrayToString(byte[] ba)
        {
            var value = BitConverter.ToString(ba).Replace("-", ", 0x");
            return $"[0x{value}]";
        }

        public static string ArrayToString(ushort[] arr)
        {
            var stringBuilder = new StringBuilder(arr.Length * 8);
            stringBuilder.Append("[");
            foreach (var @ushort in arr)
            {
                stringBuilder.Append("0x");
                stringBuilder.Append(@ushort.ToString("x4"));
                stringBuilder.Append(", ");
            }
            stringBuilder.Append("]");
            return stringBuilder.ToString();
        }
    }
}
