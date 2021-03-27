using Client.scripts.global.udp.ingress;
using Client.scripts.global;
using Godot;

namespace Client.screens.inventory.equipment
{
    public class EquipmentSlotController : Control
    {
        [Export] private NodePath _textureNodePath;
        private TextureRect _textureNode;

        [Export] private NodePath _labelNodePath;
        private Label _labelNode;

        private ItemSchema? _itemSchema;


        public override void _Ready()
        {
            _textureNode = GetNode<TextureRect>(_textureNodePath);
            _labelNode = GetNode<Label>(_labelNodePath);
            Clear();
        }

        public void SetEmpty()
        {
            if (_itemSchema == null)
            {
                return;
            }

            Clear();
        }

        private void Clear()
        {
            _textureNode.Texture = GameResourceLoader.GetSprite(8);
            _labelNode.Text = "";
        }

        public void LoadData(IngressDataPacket.BackpackUpdate.BackpackSlotDto slotDto)
        {
            if (slotDto.ItemSchemaId == _itemSchema?.Id)
            {
                return;
            }

            _itemSchema = ItemSchemaStore.Instance.Get(slotDto.ItemSchemaId);
            _textureNode.Texture = GameResourceLoader.GetSprite(_itemSchema.Value.ResourceId);
            if (slotDto.StackCount > 1 || _itemSchema.Value.Stackable)
            {
                _labelNode.Text = $"{slotDto.StackCount}";
            } else
            {
                _labelNode.Text = "";
            }

        }
    }
}
