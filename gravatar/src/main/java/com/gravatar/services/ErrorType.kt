package com.gravatar.services

import com.google.gson.Gson
import com.gravatar.HttpResponseCode
import com.gravatar.restapi.models.Error
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Objects

internal fun HttpException.errorTypeFromHttpCode(gson: Gson): ErrorType = when (code) {
    HttpResponseCode.HTTP_CLIENT_TIMEOUT -> ErrorType.Timeout
    HttpResponseCode.HTTP_NOT_FOUND -> ErrorType.NotFound
    HttpResponseCode.HTTP_TOO_MANY_REQUESTS -> ErrorType.RateLimitExceeded
    HttpResponseCode.UNAUTHORIZED -> ErrorType.Unauthorized
    HttpResponseCode.INVALID_REQUEST -> {
        val error: Error? = runCatching {
            gson.fromJson(rawErrorBody, Error::class.java)
        }.getOrNull()
        ErrorType.InvalidRequest(error)
    }

    in HttpResponseCode.SERVER_ERRORS -> ErrorType.Server
    else -> ErrorType.Unknown
}

internal fun Throwable.errorType(gson: Gson): ErrorType {
    return when (this) {
        is SocketTimeoutException -> ErrorType.Timeout
        is UnknownHostException -> ErrorType.Network
        is HttpException -> this.errorTypeFromHttpCode(gson)
        else -> ErrorType.Unknown
    }
}

/**
 * Error types for Gravatar image upload
 */
public sealed class ErrorType {
    /** server returned an error */
    public data object Server : ErrorType()

    /** network request timed out */
    public data object Timeout : ErrorType()

    /** network is not available */
    public data object Network : ErrorType()

    /** User or hash not found */
    public data object NotFound : ErrorType()

    /** User or hash not found */
    public data object RateLimitExceeded : ErrorType()

    /** User not authorized to perform given action **/
    public data object Unauthorized : ErrorType()

    /** An unknown error occurred */
    public data object Unknown : ErrorType()

    /**
     * An error occurred while processing the request.
     *
     * @property error The detailed error that occurred, if returned from the server.
     */
    public class InvalidRequest(public val error: Error?) : ErrorType() {
        override fun toString(): String = "InvalidRequest(error=$error)"

        override fun equals(other: Any?): Boolean = other is InvalidRequest && error == other.error

        override fun hashCode(): Int = Objects.hash(error)
    }
}
