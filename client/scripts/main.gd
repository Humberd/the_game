extends Node2D


# Declare member variables here. Examples:
# var a = 2
# var b = "text"
var player_node: Node2D;


# Called when the node enters the scene tree for the first time.
func _ready():
	print("hello world from a node");
	
	setupCanvas();
	
	player_node = loadPlayerNode();
	$Canvas.add_child(player_node);
	
	update();
	
	
func setupCanvas():
	var canvas = $Canvas;
	var foo = Node.new()
#	canvas.get_viewport().size.x = 500;
#	canvas.get_viewport().size.y = 500;
	
	var viewport = $".";
	print(viewport.get_viewport_rect())

	var backgroundNode = Node2D.new();
	backgroundNode.draw_rect(Rect2(Vector2(0,0), Vector2(500,500)), Color.brown, true)
	canvas.add_child(backgroundNode)

	

func loadPlayerNode() -> Node2D:
	var sprite = Sprite.new();
	sprite.texture = load("res://assets/player_avatar.png")
	sprite.name = "Sprite"
	
	var node = Node2D.new();
	node.add_child(sprite);
	node.name = "Player"
	return node;

# Called every frame. 'delta' is the elapsed time since the previous frame.
#func _process(delta):
#	pass

func _draw():
#	draw_rect(Rect2(Vector2(0,0), Vector2(300,300)), Color.brown, true)
	pass
