package errors

data class UnknownPID(private val requester: Any?) : Error(requester?.toString())
data class UnknownItemId(private val requester: Any?) : Error(requester?.toString())

fun NOT_REACHED(): Nothing {
    throw Error("Not Reached!!!")
}
