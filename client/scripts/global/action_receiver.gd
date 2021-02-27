extends Node2D

var PLAYER_UPDATE = 0x20
var PLAYER_DISCONNECT = 0x21
var PLAYER_POSITION_UPDATE = 0x22

func receive_data(arr: Array):
	var buffer = StreamPeerBuffer.new()
	buffer.data_array = arr;
	buffer.big_endian = true;
	
#	handshake		
	if buffer.get_u32() != 0x42424242:
		print("invalid handshake")
		push_error("invalid handshake")
		
		return
	
	match buffer.get_u16():
		PLAYER_UPDATE:
			handlePlayerUpdate(buffer)
		PLAYER_DISCONNECT:
			handlePlayerDisconnect(buffer)
		PLAYER_POSITION_UPDATE:
			handlePlayerPositionUpdate(buffer)
		_:
			push_error("Packet type not found")
			
			return
	
func handlePlayerUpdate(buffer: StreamPeerBuffer):
	var data = {
		pid = buffer.get_u32(),
		position = Vector2(buffer.get_float(), buffer.get_float())
	}
	
	if data["pid"] == Globals.pid:
		Globals.gpc.loadMainPlayer(data["pid"], data["position"])
	else:
		Globals.gpc.loadOtherPlayer(data["pid"], data["position"])
	
	
func handlePlayerDisconnect(buffer: StreamPeerBuffer):
	var data = {
		pid = buffer.get_u32(),
	}
	
	Globals.gpc.destroyPlayer(data["pid"])

func handlePlayerPositionUpdate(buffer: StreamPeerBuffer):
	var data = {
		pid = buffer.get_u32(),
		position = Vector2(buffer.get_float(), buffer.get_float())
	}
	
	Globals.gpc.updatePosition(data["pid"], data["position"])
	
