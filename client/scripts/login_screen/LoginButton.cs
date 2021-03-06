using Client.scripts.global.udp.egress;
using global::Client.scripts.global;
using Godot;

namespace Client.scripts.login_screen
{
    public class LoginButton : Button
    {
        [Export]
        private uint _pid;

        public override void _Pressed()
        {
            ActionSenderMono.Instance.Send(new EgressDataPacket.AuthLogin(_pid));
            GetParent().QueueFree();
        }
    }
}
