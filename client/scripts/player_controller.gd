extends Node2D

class_name PlayerController

func _ready():
	print("Hello from Player Controller");
	position = Vector2(100,100)

func _physics_process(delta):
	
	translate(getInput())
	pass;
	
func getInput() -> Vector2:
	var velocity = Vector2();
	var speed = 2;
	
	if Input.is_action_pressed("up"):
		velocity.y -= 1;
	if Input.is_action_pressed("down"):
		velocity.y += 1;
	if Input.is_action_pressed("right"):
		velocity.x += 1;
	if Input.is_action_pressed("left"):
		velocity.x -= 1;
		
	return velocity * speed;
