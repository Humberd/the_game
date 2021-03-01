namespace Client.scripts.extensions.bigendian
{
    internal static unsafe class Reinterpret
    {
        public static int FloatAsInt32(float f)
            => *(int*) &f;

        public static float Int32AsFloat(int i)
            => *(float*) &i;

        public static long DoubleAsInt64(double d)
            => *(long*) &d;

        public static double Int64AsDouble(long l)
            => *(double*) &l;
    }
}
