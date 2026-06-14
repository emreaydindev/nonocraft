package com.neilb.nonocraft.data.model

class RequestException(val code: Int, message: String) : Throwable(message)