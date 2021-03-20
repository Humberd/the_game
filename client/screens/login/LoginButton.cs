using Client.scripts.global.udp.egress;
using Godot;

namespace Client.screens.login
{
    public class LoginButton : Button
    {
        [Export]
        private uint _pid;

        public override void _Pressed()
        {
            ScreensManager.Instance.LoadGame();
            ActionSenderMono.Instance.Send(new EgressDataPacket.AuthLogin(_pid));
        }
    }
}
