using Client.scripts.global.egress;
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
            GetParent().GetParent().RemoveChild(GetParent());
        }
    }
}
