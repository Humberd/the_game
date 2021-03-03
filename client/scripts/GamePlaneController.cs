using System;
using System.Collections.Generic;
using Client.scripts.components.creature;
using Client.scripts.components.terrain;
using Client.scripts.global.udp.ingress;
using Client.scripts.global;
using Godot;

namespace Client.scripts
{
    public class GamePlaneController : Node
    {
        public TerrainController TerrainController;
        public PlayerController MainPlayer;
        public Dictionary<uint, CreatureController> OtherPlayers = new Dictionary<uint, CreatureController>();

        public static GamePlaneController Instance;

        public override void _Ready()
        {
            Instance = this;
            Console.WriteLine("Hello from GamePlaneController C#");

            CreateTerrain();
        }

        public override void _ExitTree()
        {
            Instance = null;
        }
        private void CreateTerrain()
        {
            TerrainController = new TerrainController();
            AddChild(TerrainController);
        }

        public void SpawnPlayer(IngressDataPacket.PlayerUpdate action)
        {
            if (action.Pid == UserService.Instance.PlayerId)
            {
                MainPlayer = new PlayerController();
                OtherPlayers[action.Pid] = MainPlayer;

                MainPlayer.Load(action.Name, action.Position);

                AddChild(MainPlayer);
                return;
            }

            var player = new CreatureController();
            OtherPlayers[action.Pid] = player;

            player.Load(action.Name, action.Position);

            AddChild(player);
        }

        public void DestroyPlayer(uint pid)
        {
            if (OtherPlayers.ContainsKey(pid))
            {
                var player = OtherPlayers[pid];
                if (MainPlayer == player)
                {
                    MainPlayer = null;
                }

                OtherPlayers.Remove(pid);
                RemoveChild(player);
            }
            else
            {
                Console.WriteLine($"Player ${pid} not found");
            }
        }

        public void UpdatePlayerPosition(uint pid, Vector2 position)
        {
            if (OtherPlayers.ContainsKey(pid))
            {
                var player = OtherPlayers[pid];
                player.UpdatePosition(position);
            }
            else
            {
                Console.WriteLine($"Player ${pid} not found");
            }
        }

        public void DrawTerrain(IngressDataPacket.TerrainUpdate action)
        {
            TerrainController.DrawTerrain(action);
        }
    }
}
