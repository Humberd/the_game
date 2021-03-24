using System;
using Godot;

namespace Client.screens.inventory.stats
{
    public class StatRowController : HBoxContainer
    {
        private Label _statName;
        private Label _baseValue;
        private Label _bonusValue;

        public override void _Ready()
        {
            _statName = GetNode<Label>("StatName");
            _baseValue = GetNode<Label>("BaseValue");
            _bonusValue = GetNode<Label>("BonusValue");
        }

        public void Load(string name, int baseValue, int currentValue)
        {
            _statName.Text = $"{name}: ";
            _baseValue.Text = $"{baseValue}";

            var bonus = currentValue - baseValue;
            if (bonus != 0)
            {
                var prefix = bonus > 0 ? "+" : "-";
                _bonusValue.Text = $"({prefix}{bonus})";
            }
            else
            {
                _bonusValue.Text = "";
            }
        }

        public void Load(string name, float baseValue, float currentValue)
        {
            _statName.Text = $"{name}: ";
            _baseValue.Text = $"{baseValue}";

            var bonus = currentValue - baseValue;
            if (bonus != 0)
            {
                var prefix = bonus > 0 ? "+" : "-";
                _bonusValue.Text = $"({prefix}{bonus})";
            }
            else
            {
                _bonusValue.Text = "";
            }
        }
    }
}
