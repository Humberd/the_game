package core.types

inline class ActionResult(val hasError: Boolean) {
    companion object {
        val OK = ActionResult(false)
        val ERROR = ActionResult(true)
    }
}
