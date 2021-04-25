using System.Collections.Generic;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.game.scripts.ui
{
    public class GameMapUiController : GridContainer
    {
        private readonly List<SpellButton> _spellButtons;
        public static GameMapUiController Instance;

        public GameMapUiController()
        {
            AddConstantOverride("hseparation", 16);
            _spellButtons = new List<SpellButton>
            {
                new SpellButton(),
                new SpellButton(),
                new SpellButton(),
                new SpellButton()
            };
            foreach (var spellButton in _spellButtons)
            {
                AddChild(spellButton);
            }
        }

        public override void _Ready()
        {
            Instance = this;
        }

        public override void _ExitTree()
        {
            Instance = null;
        }

        public void UpdateSpells(IngressDataPacket.EquippedSpellsUpdate action)
        {
            for (var i = 0; i < action.Spells.Length; i++)
            {
                _spellButtons[i].UpdateState(action.Spells[i]);
            }
        }
    }
}
