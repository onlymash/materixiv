package onlymash.materixiv.network.pixiv

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class PixivSSLSocketFactory : SSLSocketFactory() {

    override fun getDefaultCipherSuites(): Array<String> {
        return arrayOf()
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return arrayOf()
    }

    override fun createSocket(s: Socket, host: String?, port: Int, autoClose: Boolean): Socket {
        return createSocket(s.inetAddress, port)
    }

    override fun createSocket(host: String?, port: Int): Socket {
        val sslSocket = getDefault().createSocket(host, port) as SSLSocket
        sslSocket.enabledProtocols = sslSocket.supportedProtocols
        return sslSocket
    }

    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket {
        return createSocket(host, port)
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        val sslSocket = getDefault().createSocket(host, port) as SSLSocket
        sslSocket.enabledProtocols = sslSocket.supportedProtocols
        return sslSocket
    }

    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        return createSocket(address, port)
    }

}