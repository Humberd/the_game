package clientjvm

import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class RootScene : Spatial() {

	// Declare member variables here. Examples:
	// val a = 2;
	// val b = "text";

	// Called when the node enters the scene tree for the first time.
	@RegisterFunction
	override fun _ready() {
		println("heeheh")
	}

	// Called every frame. 'delta' is the elapsed time since the previous frame.
	@RegisterFunction
	override fun _process(delta: Double) {

	}
}
