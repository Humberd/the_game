using System;

namespace Client.scripts.extensions
{
    public class RxObserverAdapter<T> : IObserver<T>
    {
        private readonly Action<T> _action;

        private RxObserverAdapter(Action<T> action)
        {
            _action = action;
        }

        public static IObserver<T> Get(Action<T> action)
        {
            return new RxObserverAdapter<T>(action);
        }

        public void OnNext(T value)
        {
            _action.Invoke(value);
        }

        public void OnError(Exception error)
        {
        }

        public void OnCompleted()
        {
        }


    }
}
