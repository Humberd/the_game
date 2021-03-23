using Godot;

namespace Client.screens.game.scripts.components.creature
{
    public class BodyController : Spatial
    {
        [Export] private NodePath _animationPath;
        private AnimationPlayer _animationPlayer;
        public override void _Ready()
        {
            _animationPlayer = GetNode<AnimationPlayer>(_animationPath);
            _animationPlayer.GetAnimation("default").Loop = true;
            _animationPlayer.CurrentAnimation = "default";
        }

        public void StartWalking()
        {
            if (_animationPlayer.IsPlaying())
            {
                return;
            }

            _animationPlayer.Play();
        }

        public void StopWalking()
        {
            _animationPlayer.Stop();
            _animationPlayer.Seek(0, true);
        }
    }
}
