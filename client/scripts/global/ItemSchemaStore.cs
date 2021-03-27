using System;
using System.Collections.Generic;
using Godot;
using Newtonsoft.Json;

namespace Client.scripts.global
{
    public class ItemSchemaStore : Node
    {
        public static ItemSchemaStore Instance { get; private set; }
        private Dictionary<ushort, ItemSchema> _dict = new();

        public override void _Ready()
        {
            Instance = this;
            ReadSchema();
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            Instance = null;
        }

        private void ReadSchema()
        {
            var file = new File();
            file.Open("res://assets/resources/item_schemas.json", File.ModeFlags.Read);
            var content = file.GetAsText();
            file.Close();

            var parsedSchema = JsonConvert.DeserializeObject<ItemSchema[]>(content);
            if (parsedSchema == null)
            {
                throw new Exception("Failed to parse items schema");
            }

            foreach (var itemSchema in parsedSchema)
            {
                _dict[itemSchema.Id] = itemSchema;
            }
        }

        public ItemSchema Get(ushort itemSchemaId)
        {
            return _dict[itemSchemaId];
        }
    }

    public readonly struct ItemSchema
    {
        public readonly ushort Id;
        public readonly string Name;
        public readonly string Description;
        public readonly ushort ResourceId;
        public readonly string[] Equippable;
        public readonly bool Stackable;

        public ItemSchema(ushort id, string name, string description, ushort resourceId, string[] equippable,
            bool stackable)
        {
            Id = id;
            Name = name;
            Description = description;
            ResourceId = resourceId;
            Equippable = equippable;
            Stackable = stackable;
        }
    }
}
