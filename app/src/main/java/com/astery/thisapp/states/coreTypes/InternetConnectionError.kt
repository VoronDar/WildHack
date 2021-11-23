package com.astery.thisapp.states.coreTypes

import com.astery.thisapp.R
import com.astery.thisapp.states.JobErrorType

class InternetConnectionError():JobErrorType{
    override fun stringId(): Int {
        return R.string.error_internet_connection
    }
}
class SomethingWentWrongError():JobErrorType{
    override fun stringId(): Int {
        return R.string.error_something_went_wrong
    }
}