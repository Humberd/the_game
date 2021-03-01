using System;

namespace Client.scripts.extensions
{
    public class Utils
    {
        public static string ByteArrayToString(byte[] ba)
        {
            var value = BitConverter.ToString(ba).Replace("-", ", 0x");
            return $"[0x{value}]";
        }
    }
}
