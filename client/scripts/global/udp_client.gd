extends Node2D

var udp: PacketPeerUDP;

func _ready():
	udp = PacketPeerUDP.new();
	udp.set_dest_address("127.0.0.1", 4445)

func sendString(data: String):
	udp.put_packet(data.to_ascii());
	
func sendVariant(data):
	udp.put_var(data, true)
	
func sendRaw(data: PoolByteArray):
	udp.put_packet(data)
