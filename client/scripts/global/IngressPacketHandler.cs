using Client.scripts.global.udp.ingress;

namespace Client.scripts.global
{
    public class IngressPacketHandler
    {
        public void Handle(IngressDataPacket.PlayerUpdate action)
        {
            GamePlaneController.Instance.PlayerUpdate(action);
        }

        public void Handle(IngressDataPacket.CreatureDisappear action)
        {
            GamePlaneController.Instance.CreatureDisappear(action.Pid);
        }

        public void Handle(IngressDataPacket.CreaturePositionUpdate action)
        {
            GamePlaneController.Instance.UpdateCreaturePosition(action);
        }

        public void Handle(IngressDataPacket.TerrainUpdate action)
        {
            GamePlaneController.Instance.DrawTerrain(action);
        }

        public void Handle(IngressDataPacket.TerrainItemsUpdate action)
        {
            GamePlaneController.Instance.DrawItems(action);
        }

        public void Handle(IngressDataPacket.PlayerDetails action)
        {
            UserService.Instance.HandleDetails(action);
        }

        public void Handle(IngressDataPacket.EquippedSpellsUpdate action)
        {
            GameMapUiController.Instance.UpdateSpells(action);
        }

        public void Handle(IngressDataPacket.SpellUse action)
        {
            GamePlaneController.Instance.DisplaySpell(action);
        }

        public void Handle(IngressDataPacket.DamageTaken action)
        {
            GamePlaneController.Instance.DisplayDamageTaken(action);
        }

        public void Handle(IngressDataPacket.ProjectileSend action)
        {
            GamePlaneController.Instance.DisplayProjectile(action);
        }
    }
}
