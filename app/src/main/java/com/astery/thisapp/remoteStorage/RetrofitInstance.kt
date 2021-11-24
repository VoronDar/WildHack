package com.astery.thisapp.remoteStorage

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


@Singleton
class  RetrofitInstance @Inject constructor(){

    private val retrofit: Retrofit by lazy{

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        //val logging = HttpLoggingInterceptor()
        //logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
            //.addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followSslRedirects(true)
            .followRedirects(true)
            .build()


        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    val api:ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }

    private val BASE_URL = "http://172.16.1.5:8000/api/v2/"


    private val interceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            var response = chain.proceed(chain.request())
            if (response.code == 307) {
                request = request.newBuilder()
                    .url(response.header("Location")!!)
                    .build()
                response = chain.proceed(request)
            }
            return response
        }
    }
}