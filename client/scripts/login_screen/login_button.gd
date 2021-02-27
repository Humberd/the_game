extends BaseButton

class_name LoginButton

export (int) var playerId;

func _pressed():
	Globals.setMainPid(playerId)
	ActionSender.sendAuthLogin(playerId)
	get_parent().get_parent().remove_child(get_parent())
