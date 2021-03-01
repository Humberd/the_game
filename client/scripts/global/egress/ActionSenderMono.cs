using Godot;

namespace Client.scripts.global.egress
{
    public class ActionSenderMono : Node
    {

        public static ActionSenderMono Instance;

        public override void _Ready()
        {
            Instance = this;
            Send(new EgressDataPacket.ConnectionHello());
        }

        public override void _ExitTree()
        {
            Send(new EgressDataPacket.Disconnect());
            Instance = null;
        }

        public void Send(EgressDataPacket data)
        {
            UdpClientMono.Instance.Send(data.Pack());
        }
    }
}
