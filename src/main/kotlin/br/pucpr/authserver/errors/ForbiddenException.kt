package br.pucpr.authserver.errors

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(FORBIDDEN)
class ForbiddenException(
    message: String = FORBIDDEN.reasonPhrase,
    cause: Throwable? = null
) : Exception(message, cause)