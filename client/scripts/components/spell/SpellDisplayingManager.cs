using System;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.components.spell
{
    public class SpellDisplayingManager : Node2D
    {
        public void DisplaySpell(IngressDataPacket.SpellUse action)
        {
            for (var i = 0; i < action.Effects.Length; i++)
            {
                var effectAction = action.Effects[i];
                var newPosition = action.SourcePosition + new Vector2(64 * i, 0);

                var effectNode = new EffectNode();
                AddChild(effectNode);
                effectNode.Init(newPosition, effectAction.SpriteId, effectAction.Duration);
            }
        }

        public void DisplayDamageTaken(IngressDataPacket.DamageTaken action)
        {
            foreach (var damage in action.Damages)
            {
                var damageNode = new DamageNode();
                AddChild(damageNode);
                damageNode.Init(damage.Position * 64, damage.Amount);
            }
        }

        public void DisplayProjectile(IngressDataPacket.ProjectileSend action)
        {
            var projectileNode = new ProjectileNode();
            AddChild(projectileNode);
            projectileNode.Init(
                spriteId: action.SpriteId,
                position: action.SourcePosition * 64,
                targetPosition: action.TargetPosition * 64,
                duration: action.Duration
            );
        }
    }
}
