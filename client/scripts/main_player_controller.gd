extends Node2D

class_name MainPlayerController

func _ready():
	print("Hello from Main Player Controller");
	position = Vector2(100,100)

func _physics_process(delta):
	
	var mask = getInput()
	if mask != 0:
		ActionSender.sendPositionChange(mask)
	
func getInput() -> int:
	var mask = 0;
	
	if Input.is_action_pressed("up"):
		mask = mask | 0x01
	if Input.is_action_pressed("down"):
		mask = mask | 0x04
	if Input.is_action_pressed("right"):
		mask = mask | 0x02
	if Input.is_action_pressed("left"):
		mask = mask | 0x08
		
	return mask;
