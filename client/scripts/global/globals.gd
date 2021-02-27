extends Node2D

var gpc: GamePlaneController
var pid: int

func registerGpc(ctrl: GamePlaneController):
	gpc = ctrl
	
func setMainPid(_pid: int):
	pid = _pid

func loginAction(pid: int): 
	ActionSender.sendAuthLogin(pid)
