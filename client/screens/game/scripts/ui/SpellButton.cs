using Client.scripts.global;
using Client.scripts.global.udp.egress;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.screens.game.scripts.ui
{
    public class SpellButton : TextureButton
    {
        private string _name;
        private uint _sid;
        private readonly Label _cooldownLabel;

        public SpellButton()
        {
            LoadDefaultSprite();
            RectSize = new Vector2(64, 64);

            _cooldownLabel = new Label
            {
                RectPosition = new Vector2(32, 50)
            };
            AddChild(_cooldownLabel);
        }

        private void LoadDefaultSprite()
        {
            TextureNormal = GameResourceLoader.GetSprite(8);
        }

        public override void _Pressed()
        {
            if (_sid == 0)
            {
                return;
            }
            ActionSenderMono.Instance.Send(new EgressDataPacket.SpellUsage(
                sid: _sid
            ));
        }

        public void UpdateName(string name)
        {
            _name = name;
        }

        public void UpdateSprite(ushort spriteId)
        {
            TextureNormal = GameResourceLoader.GetSprite(spriteId);
        }

        public void UpdateCooldown(uint cooldown)
        {
            float seconds = cooldown / 1000f;
            _cooldownLabel.Text = $"{seconds}s";
        }

        public void UpdateState(IngressDataPacket.EquippedSpellsUpdate.SpellUpdate actionSpell)
        {
            if (actionSpell == null)
            {
                LoadDefaultSprite();
                _cooldownLabel.Text = "";
                _sid = 0;
                return;
            }

            UpdateName(actionSpell.Name);
            UpdateSprite(actionSpell.SpriteId);
            UpdateCooldown(actionSpell.Cooldown);
            _sid = actionSpell.Sid;
        }
    }
}
