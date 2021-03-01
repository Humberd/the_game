using Godot;

namespace Client.scripts.global
{
    public class UserService : Node
    {
        public static UserService Instance;

        public uint PlayerId;

        public override void _Ready()
        {
            Instance = this;
        }

        public override void _ExitTree()
        {
            Instance = null;
        }
    }
}
