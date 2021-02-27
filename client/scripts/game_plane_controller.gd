extends ViewportContainer

class_name GamePlaneController

var main_player: MainPlayerController;
var all_players: Dictionary = {};

var plane: Node2D;

	
func _init():
	Globals.registerGpc($".")

func _ready():
	print("hello world from a node");

	plane = $Viewport/Plane;
	var viewport = $Viewport;

	createBackground(plane, viewport);

func createBackground(plane: Node2D, viewport: Viewport):
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/background.png");
	sprite.name = "Background"
	sprite.centered = false;
	sprite.region_enabled = true;
	sprite.region_rect = Rect2(Vector2(0, 0), Vector2(viewport.size.x, viewport.size.y));
	sprite.modulate = Color("ababab")

	plane.add_child(sprite);
	
func loadMainPlayer(pid: int, position: Vector2):
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/player_avatar.png");
	sprite.name = "Sprite";
	sprite.centered = true;
	sprite.scale = Vector2(0.35, 0.35);

	var script = load("res://scripts/main_player_controller.gd");

	var node = Node2D.new();
	node.add_child(sprite);
	node.name = "Player";
	node.set_script(script);
	node.position = position

	plane.add_child(node)
	main_player = node;
	all_players[pid] = node

func loadOtherPlayer(pid: int, position: Vector2):
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/player_avatar.png");
	sprite.name = "Sprite";
	sprite.centered = true;
	sprite.scale = Vector2(0.35, 0.35);
	sprite.modulate = Color("ababab")

	var node = Node2D.new();
	node.add_child(sprite);
	node.name = "Other Player";
	node.position = position

	plane.add_child(node)
	all_players[pid] = node
	
func updatePosition(pid: int, position: Vector2):
	var player = all_players[pid]
	if !player:
		print("player doesn't exist")
		return
	
	player.position = position

func destroyPlayer(pid: int):
	var player = all_players[pid]
	if !player:
		print("player doesn't exist")
		return
		
	plane.remove_child(player)
	all_players[pid] = null
