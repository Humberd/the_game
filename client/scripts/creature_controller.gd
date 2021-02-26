extends Node2D

class_name CreatureController

export (float) var speed = 2;

func overwritePosition(position: Vector2): 
	translate(position);
