package com.alec.mad.p6

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.*

object DownloadUtils {
    /**
     * Sets up HttpsURLConnection to accept a 'self-signed certificate'. Note: this code would be
     * *completely unnecessary* (and in fact bad) in a real-world situation. However, we need it
     * when testing and experimenting, because in this situation it's impractical to go and get a
     * proper certificate.
     *
     * @param context The Android context/activity. Needed in order to extract the certificate
     * text itself from the app's resources.
     * @param conn The HTTPS connection that will use the certificate.
     * @throws IOException If we couldn't load the certificate or the KeyStore.
     * @throws GeneralSecurityException If any of the other various setup steps fail. We're not
     * performing the actual verification here though. If the certificate turns out not to match
     * the server's certificate, that will *only later on* trigger an exception.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class, GeneralSecurityException::class)
    fun addCertificate(context: Context, conn: HttpsURLConnection) {
        /**
         * Adapted in part from https://developer.android.com/training/articles/security-ssl#java
         */
        var cert: Certificate?
        context.resources.openRawResource(R.raw.cert).use { `is` ->
            cert = CertificateFactory.getInstance("X.509").generateCertificate(`is`)
        }
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", cert)
        val tmf = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        tmf.init(keyStore)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)
        conn.sslSocketFactory = sslContext.socketFactory
        conn.hostnameVerifier = HostnameVerifier { _: String, _: SSLSession -> true }
    }
}