package com.astery.thisapp.remoteStorage

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.astery.thisapp.model.UserAccess
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import com.android.volley.toolbox.HttpHeaderParser

import com.android.volley.NetworkResponse

import com.android.volley.VolleyLog

import com.android.volley.AuthFailureError

import com.android.volley.VolleyError

import com.android.volley.RequestQueue
import com.astery.thisapp.states.JSuccess
import com.astery.thisapp.states.JobState
import java.io.UnsupportedEncodingException


class RemoteDataStorage @Inject constructor(private val retrofit: RetrofitInstance, @ApplicationContext private val context: Context) {
    suspend fun auth(user:UserAccess):JobState{
        return JSuccess()
        Timber.d("auth $user")
        val token = retrofit.api.login(user)
        Timber.d("auth? $token")
        return TODO("return JobState")
    }



    // I hope I can handle it with retrofit, but I need volley to check for details
    private suspend fun login(user:UserAccess){
        val url = "http://172.16.1.5:8000/api/v2/auth/login"


        val requestQueue = Volley.newRequestQueue(context)
        val jsonBody = JSONObject()
        jsonBody.put("username", user.username)
        jsonBody.put("password", user.password)
        val requestBody = jsonBody.toString()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response -> Timber.i(response) },
            Response.ErrorListener { error -> Timber.e(error.toString()); Timber.e(requestBody)}) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {return requestBody.toByteArray(charset("utf-8")) }

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                var responseString = ""
                responseString = response.statusCode.toString()
                return Response.success(
                    responseString,
                    HttpHeaderParser.parseCacheHeaders(response)
                )
            }
        }

        //stringRequest.headers["Content-Type"] = "application/json"
        //stringRequest.headers["Media-Type"] = "application/json"


        requestQueue.add(stringRequest)
    }
}