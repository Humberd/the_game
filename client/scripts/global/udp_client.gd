extends Node2D

var udp: PacketPeerUDP;

func _ready():
	udp = PacketPeerUDP.new();
	udp.set_dest_address("127.0.0.1", 4445);
	start_server();
	
func start_server():
	var rng = RandomNumberGenerator.new()
	rng.randomize()
	var port = rng.randi_range(999, 9999)

	if (udp.listen(port, "*", 256) != OK):
		printt("Error listening on port: " + str(port))
	else:
		printt("Listening on port: " + str(port))

func _process(delta):
	if (udp.get_available_packet_count() > 0):
		ActionReceiver.receive_data(udp.get_packet())
	

func sendString(data: String):
	udp.put_packet(data.to_ascii());
	
func sendVariant(data):
	udp.put_var(data, true)
	
func sendRaw(data: PoolByteArray):
	udp.put_packet(data)
