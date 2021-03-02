using System;
using Client.scripts.extensions;
using Client.scripts.global.udp.ingress;

namespace Client.scripts.global
{
    public class IngressPacketHandler
    {
        public void Handle(IngressDataPacket.PlayerUpdate action)
        {
            GamePlaneController.Instance.SpawnPlayer(action);
        }

        public void Handle(IngressDataPacket.PlayerDisconnect action)
        {
            GamePlaneController.Instance.DestroyPlayer(action.Pid);
        }

        public void Handle(IngressDataPacket.PlayerPositionUpdate action)
        {
            GamePlaneController.Instance.UpdatePlayerPosition(action.Pid, action.Position);
        }

        public void Handle(IngressDataPacket.TerrainUpdate action)
        {
            GamePlaneController.Instance.DrawTerrain(action);
        }
    }
}
