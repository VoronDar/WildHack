package com.astery.thisapp.states


/** feetback from repository to viewModel (error, failure and success) or from viewModel to ui (idle, running and others) */
sealed class JobState()

/** The action didn't started  */
class JIdle:JobState()
/** the action started, but the result didn't come yet */
class JRunning:JobState()
/** internet connection error, timeout, 404, etc */
data class JError(val error: JobErrorType):JobState()
data class JFailure(val reason: JobFailureType):JobState()
class JSuccess():JobState()