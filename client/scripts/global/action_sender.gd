extends Node2D

var POSITION_CHANGE = 0x10

func sendPositionChange(mask: int):
	
	UdpClient.sendRaw([0x69, 0x69, 0x69, 0x00, POSITION_CHANGE, mask])
