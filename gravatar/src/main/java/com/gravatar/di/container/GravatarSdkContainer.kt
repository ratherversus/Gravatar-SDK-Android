package com.gravatar.di.container

import com.google.gson.GsonBuilder
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V1
import com.gravatar.GravatarConstants.GRAVATAR_API_BASE_URL_V3
import com.gravatar.services.AuthenticationInterceptor
import com.gravatar.services.AvatarUploadTimeoutInterceptor
import com.gravatar.services.GravatarApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }
    }

    private fun getRetrofitApiV1Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V1)

    private fun getRetrofitApiV3Builder() = Retrofit.Builder().baseUrl(GRAVATAR_API_BASE_URL_V3)

    private val gson = GsonBuilder().setLenient()
        .create()

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null

    /**
     * Get Gravatar API service
     *
     * @param okHttpClient The OkHttp client to use
     * @return The Gravatar API service
     */
    fun getGravatarV1Service(okHttpClient: OkHttpClient? = null): GravatarApi {
        return getRetrofitApiV1Builder().apply {
            okHttpClient?.let { client(it) }
        }.build().create(GravatarApi::class.java)
    }

    fun getGravatarV3Service(okHttpClient: OkHttpClient? = null, oauthToken: String? = null): GravatarApi {
        return getRetrofitApiV3Builder().apply {
            client(okHttpClient ?: buildOkHttpClient(oauthToken))
        }.addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GravatarApi::class.java)
    }

    private fun buildOkHttpClient(oauthToken: String?) = OkHttpClient()
        .newBuilder()
        .addInterceptor(AuthenticationInterceptor(oauthToken))
        .addInterceptor(AvatarUploadTimeoutInterceptor())
        .build()
}
