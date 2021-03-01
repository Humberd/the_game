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
    }
}
