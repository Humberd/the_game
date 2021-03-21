using Client.screens.game.scripts.components.creature;
using Godot;

namespace Client.screens.game.scripts
{
    public class CameraController : Camera
    {
        public static CameraController Instance;

        public override void _Ready()
        {
            Instance = this;
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            Instance = null;
        }

        public void Track(PlayerController player)
        {
            // LookAt(new Vector3(1, 1, 1), new Vector3(1, 1, 1));
        }

        public void Untrack()
        {
        }
    }
}
