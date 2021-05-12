package com.ewellshop.helpers

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ewellshop.helpers.Constants.TAG
import com.ewellshop.popups.ProgressPopup
import com.google.android.gms.security.ProviderInstaller
import org.json.JSONException
import org.json.JSONObject
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Internet(
    private var context: Context,
    private val url: String?,
    private val params: HashMap<String, Any>?,
    private val showPopup: Boolean,
    private val error: ((Int) -> Unit)? = null,
    private val noInternet: (() -> Unit)? = null,
    private val retry: Boolean = false,
    private val method: Int = Request.Method.POST,
    private val callback: (JSONObject) -> Unit
) {
    init {
        val popup = ProgressPopup()
        if (showPopup) (context as? AppCompatActivity)?.run { popup.show(
            supportFragmentManager,
            "progressBar"
        ) }

        updateAndroidSecurityProvider()
        HttpsTrustManager.allowAllSSL()
        val requestQueue = Volley.newRequestQueue(context)
        val request: StringRequest = object : StringRequest(method, url,
            com.android.volley.Response.Listener { response: String? ->
                if (showPopup) popup.dismiss()
                try {
                    val response = JSONObject(response)
                    if (response.has("_errorCode")) {
                        if (response.has("_errorMsg")) alert(response.getString("_errorMsg"), retry)
                        else error?.invoke(response.getInt("_errorCode"))
                    } else
                        callback.invoke(response)
                } catch (e: JSONException) {
                    if (response?.isNotEmpty() == true) {
                        callback.invoke(JSONObject().apply {
                            put("String", response)
                        })
                    } else {
                        Log.d(TAG, e.message.toString())
                        alert("Couldn't read data. Report this to us")
                    }
                }
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                if (showPopup) {
                    popup.dismiss()
                    alert("Please connect to the internet", true)
                }
                noInternet?.invoke()
//                alert(error.toString())
                Log.d(TAG, error.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val parameters = hashMapOf<String, String>()
                this@Internet.params?.forEach { (key, value) -> parameters[key] = value.toString() }
                return parameters
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }

    private fun updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(context)
        } catch (e: Exception) {
            e.message
        }
    }

    fun alert(err: String?, retry: Boolean = false){
        Actions.showAlert(context, "Error", err, if (retry) "Try again" else "Okay",
            { _, _ ->
                if (retry) Internet(context, url, params, showPopup, error, noInternet,
                        retry, method, callback)
            })
    }

    class HttpsTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            x509Certificates: Array<X509Certificate>, s: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            x509Certificates: Array<X509Certificate>, s: String
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return _AcceptedIssuers
        }

        companion object {
            private var trustManagers: Array<TrustManager>? = null
            private val _AcceptedIssuers = arrayOf<X509Certificate>()
            fun allowAllSSL() {
                HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
                var context: SSLContext? = null
                if (trustManagers == null) {
                    trustManagers = arrayOf(HttpsTrustManager())
                }
                try {
                    context = SSLContext.getInstance("TLS")
                    context.init(null, trustManagers, SecureRandom())
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: KeyManagementException) {
                    e.printStackTrace()
                }
                HttpsURLConnection.setDefaultSSLSocketFactory(context?.getSocketFactory())
            }
        }
    }
}