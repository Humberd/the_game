using System;
using System.Collections.Generic;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.inventory.stats
{
    public class StatsPanelController : VBoxContainer
    {
        [Export] private PackedScene _statRowTemplate;

        private List<StatRowController> _statRows = new List<StatRowController>();

        public override void _Ready()
        {
        }

        public void LoadStats(IngressDataPacket.CreatureStatsUpdate action)
        {
            ClearRows();
            InstantiateRow().Load("Defense", action.Defense.BaseValue, action.Defense.CurrentValue);
            InstantiateRow().Load("Attack", action.Attack.BaseValue, action.Attack.CurrentValue);
            InstantiateRow().Load("Attack Speed", action.AttackSpeed.BaseValue, action.AttackSpeed.CurrentValue);
            InstantiateRow().Load("Movement Speed", action.MovementSpeed.BaseValue, action.MovementSpeed.CurrentValue);
            InstantiateRow().Load("Max Health", action.HealthPool.BaseValue, action.HealthPool.CurrentValue);
        }

        private void ClearRows()
        {
            foreach (var row in _statRows)
            {
                row.QueueFree();
            }
            _statRows.Clear();
        }

        private StatRowController InstantiateRow()
        {
            var row = (StatRowController) _statRowTemplate.Instance();
            AddChild(row);
            _statRows.Add(row);
            return row;
        }
    }
}
