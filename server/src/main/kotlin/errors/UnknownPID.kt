package errors

data class UnknownPID(private val requester: Any?) : Error(requester?.toString())
