package br.pucpr.authserver.errors

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(NOT_FOUND)
class NotFoundException(
    message: String = NOT_FOUND.reasonPhrase,
    cause: Throwable? = null
) : Exception(message, cause)
