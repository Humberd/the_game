using System;
using System.Collections.Generic;
using Client.screens.game.scripts.components.creature;
using Client.screens.game.scripts.components.spell;
using Client.screens.game.scripts.components.terrain;
using Client.scripts.global;
using Client.scripts.global.udp.ingress;
using Godot;
using CID = System.UInt32;

namespace Client.screens.game.scripts
{
    public class GamePlaneController : Spatial
    {
        private TerrainController _terrainController;
        private ItemsPlaneController _itemsPlaneController;
        private SpellDisplayingManager _spellDisplayingManager;
        private readonly Dictionary<CID, CreatureController> _allCreatures = new();

        public static GamePlaneController Instance;

        public override void _Ready()
        {
            Instance = this;
            Console.WriteLine("Hello from GamePlaneController C#");
            RotateX(Mathf.Deg2Rad(90));

            CreateTerrain();
        }

        public override void _ExitTree()
        {
            Instance = null;
        }

        private void CreateTerrain()
        {
            _terrainController = new TerrainController();
            AddChild(_terrainController);
            _itemsPlaneController = new ItemsPlaneController();
            AddChild(_itemsPlaneController);
            _spellDisplayingManager = new SpellDisplayingManager();
            AddChild(_spellDisplayingManager);
        }

        public void PlayerUpdate(IngressDataPacket.PlayerUpdate action)
        {
            if (_allCreatures.ContainsKey(action.Cid))
            {
                var creature = _allCreatures[action.Cid];
                creature.UpdateData(action);
                return;
            }

            var isMe = UserService.Instance.GetCid() == action.Cid;
            CreatureController newCreature;
            if (isMe)
            {
                newCreature = new PlayerController();
            }
            else
            {
                newCreature = new CreatureController();
            }

            newCreature.UpdateData(action);
            _allCreatures[action.Cid] = newCreature;
            AddChild(newCreature);
        }

        public void CreatureDisappear(CID cid)
        {
            if (!_allCreatures.ContainsKey(cid))
            {
                Console.WriteLine($"Creature ${cid} not found");
                return;
            }

            var creature = _allCreatures[cid];
            _allCreatures.Remove(cid);
            creature.QueueFree();
        }

        public void UpdateCreaturePosition(IngressDataPacket.CreaturePositionUpdate action)
        {
            if (!_allCreatures.ContainsKey(action.Cid))
            {
                Console.WriteLine($"Creature ${action.Cid} not found");
                return;
            }

            var creature = _allCreatures[action.Cid];
            creature.UpdatePosition(action.Position);
        }

        public void DrawTerrain(IngressDataPacket.TerrainUpdate action)
        {
            _terrainController.DrawTerrain(action);
        }

        public void DrawItems(IngressDataPacket.TerrainItemsUpdate action)
        {
            _itemsPlaneController.DrawItems(action);
        }

        public void DisplaySpell(IngressDataPacket.SpellUse action)
        {
            _spellDisplayingManager.DisplaySpell(action);
        }

        public void DisplayDamageTaken(IngressDataPacket.DamageTaken action)
        {
            _spellDisplayingManager.DisplayDamageTaken(action);
        }

        public void DisplayProjectile(IngressDataPacket.ProjectileSend action)
        {
            _spellDisplayingManager.DisplayProjectile(action);
        }
    }
}
