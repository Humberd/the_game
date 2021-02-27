extends Node2D

var CONNECTION_HELLO = 0x00
var DISCONNECT = 0x01
var AUTH_LOGIN = 0x05
var POSITION_CHANGE = 0x10

func _ready():
	sendHello()
	
func _exit_tree():
	sendDisconnect()
	
func prepareShortHeader(type: int) -> Array:
	var arr = [0x69, 0x69, 0x69, 0x69]
	putShort(arr, type);
	
	return arr
	
func putByte(arr: Array, value: int):
	arr.append(value & 0xff)
	
func putShort(arr: Array, value: int):
	arr.append(value & 0xff00)
	arr.append(value & 0x00ff)
	
func putInt(arr: Array, value: int):
	arr.append(value & 0xff000000)
	arr.append(value & 0x00ff0000)
	arr.append(value & 0x0000ff00)
	arr.append(value & 0x000000ff)


#--------------------------

func sendHello():
	var data = prepareShortHeader(CONNECTION_HELLO)
	UdpClient.sendRaw(data)
	
func sendDisconnect():
	var data = prepareShortHeader(DISCONNECT)
	UdpClient.sendRaw(data)
	
func sendAuthLogin(id: int):
	var data = prepareShortHeader(AUTH_LOGIN)
	putInt(data, id)
	UdpClient.sendRaw(data)

func sendPositionChange(mask: int):
	var data = prepareShortHeader(POSITION_CHANGE)
	putByte(data, mask)
	UdpClient.sendRaw(data)
