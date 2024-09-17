/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */
package com.gravatar.restapi.models

import com.google.gson.annotations.SerializedName
import java.util.Objects

/**
 *
 *
 * @param emailHash The email SHA256 hash to set the avatar for.
 */

public class SetEmailAvatarRequest private constructor(
    // The email SHA256 hash to set the avatar for.
    @SerializedName("email_hash")
    public val emailHash: kotlin.String,
) {
    override fun toString(): String = "SetEmailAvatarRequest(emailHash=$emailHash)"

    override fun equals(other: Any?): Boolean = other is SetEmailAvatarRequest &&
        emailHash == other.emailHash

    override fun hashCode(): Int = Objects.hash(emailHash)

    public class Builder {
        // The email SHA256 hash to set the avatar for.
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var emailHash: kotlin.String? = null

        public fun setEmailHash(emailHash: kotlin.String?): Builder = apply { this.emailHash = emailHash }

        public fun build(): SetEmailAvatarRequest = SetEmailAvatarRequest(emailHash!!)
    }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
public fun SetEmailAvatarRequest(initializer: SetEmailAvatarRequest.Builder.() -> Unit): SetEmailAvatarRequest {
    return SetEmailAvatarRequest.Builder().apply(initializer).build()
}
