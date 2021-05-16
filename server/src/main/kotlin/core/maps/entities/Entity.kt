package core.maps.entities

interface Entity : Lifecycle

interface Lifecycle {
    fun onInit() {}
    fun onUpdate(deltaTime: Float) {}
    fun onDestroy() {}
}
