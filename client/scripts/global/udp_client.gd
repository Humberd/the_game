extends Node

# Declare member variables here. Examples:
# var a = 2
# var b = "text"

var udp: PacketPeerUDP;


# Called when the node enters the scene tree for the first time.
func _ready():
	udp = PacketPeerUDP.new();
	udp.set_dest_address("127.0.0.1", 4445)

func sendString(data: String):
	udp.put_packet(data.to_ascii());
	
func sendVariant(data):
	udp.put_var(data, true)
	
func sendRaw(data: PoolByteArray):
	udp.put_packet(data)
