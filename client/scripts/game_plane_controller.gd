extends ViewportContainer


# Declare member variables here. Examples:
# var a = 2
# var b = "text"
var player_node: Node2D;


# Called when the node enters the scene tree for the first time.
func _ready():
	print("hello world from a node");

	var plane = $Viewport/Plane;
	var viewport = $Viewport;
	
	createBackground(plane, viewport);
	loadPlayerNode(plane);

	
func createBackground(plane: Node2D, viewport: Viewport):
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/background.png");
	sprite.name = "Background"
	sprite.centered = false;
	sprite.region_enabled = true;
	sprite.region_rect = Rect2(Vector2(0, 0), Vector2(viewport.size.x, viewport.size.y));
	sprite.modulate = Color("515948")
	
	plane.add_child(sprite);

func loadPlayerNode(plane: Node2D):
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/player_avatar.png");
	sprite.name = "Sprite";
	sprite.centered = true;
	sprite.scale = Vector2(0.35, 0.35);

	var node = Node2D.new();
	node.add_child(sprite);
	node.name = "Player";
	node.set_script(load("res://scripts/player_controller.gd"));
	
	plane.add_child(node);

# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass

func _draw():
#	draw_rect(Rect2(Vector2(0,0), Vector2(300,300)), Color.brown, true)
	pass
