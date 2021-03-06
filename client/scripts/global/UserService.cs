using System;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.global
{
    public class UserService : Node
    {
        public static UserService Instance;

        private uint _pid;
        private uint _cid;

        public override void _Ready()
        {
            Instance = this;
        }

        public override void _ExitTree()
        {
            Instance = null;
        }

        public void HandleDetails(IngressDataPacket.PlayerDetails action)
        {
            _pid = action.Pid;
            _cid = action.Cid;
        }

        public uint GetCid()
        {
            if (_cid == 0)
            {
                throw new Exception("Not yet logged in");
            }

            return _cid;
        }
    }
}
