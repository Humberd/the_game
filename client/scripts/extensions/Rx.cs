using System;
using System.Reactive.Concurrency;
using System.Reactive.Linq;

namespace Client.scripts.extensions
{
    public static class Rx
    {
        public static IObservable<T> ThrottleTime<T>(this IObservable<T> source, TimeSpan ts)
        {
            return ThrottleTime(source, ts, Scheduler.Default);
        }

        public static IObservable<T> ThrottleTime<T>(this IObservable<T> source, TimeSpan ts, IScheduler scheduler)
        {
            return source
                .Timestamp(scheduler)
                .Scan((EmitValue: false, OpenTime: DateTimeOffset.MinValue, Item: default(T)), (state, item) => item.Timestamp > state.OpenTime
                    ? (true, item.Timestamp + ts, item.Value)
                    : (false, state.OpenTime, item.Value)
                )
                .Where(t => t.EmitValue)
                .Select(t => t.Item);
        }
    }
}
