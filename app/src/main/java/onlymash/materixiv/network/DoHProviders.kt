package onlymash.materixiv.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException

object DoHProviders {

    val cleanBrowsing: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("185.228.168.9", "185.228.168.9")
            val bootsrapHostsV6 = getByIPv6("2a0d:2a00:1::2", "2a0d:2a00:2::2")
            return DnsOverHttps.Builder()
                .url("https://doh.cleanbrowsing.org/doh/security-filter".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    val dnsSb: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("185.222.222.222", "45.11.45.11")
            val bootsrapHostsV6 = getByIPv6("2a09::", "2a11::")
            return DnsOverHttps.Builder()
                .url("https://doh.sb/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    val openDns: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("208.67.222.222", "208.67.220.220")
            val bootsrapHostsV6 = getByIPv6("2620:119:35::35", "2620:119:53::53")
            return DnsOverHttps.Builder()
                .url("https://doh.opendns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    val quad9: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("9.9.9.11", "149.112.112.11")
            val bootsrapHostsV6 = getByIPv6("2620:fe::11", "2620:fe::fe:11")
            return DnsOverHttps.Builder()
                .url("https://dns11.quad9.net/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    val cloudflareDns: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("104.16.248.249", "104.16.249.249")
            val bootsrapHostsV6 = getByIPv6("2606:4700::6810:f9f9", "2606:4700::6810:f8f9")
            return DnsOverHttps.Builder()
                .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    val googleDns: DnsOverHttps
        get() {
            val bootsrapHostsV4 = getByIPv4("8.8.4.4", "8.8.8.8")
            val bootsrapHostsV6 = getByIPv6("2001:4860:4860::8844", "2001:4860:4860::8888")
            return DnsOverHttps.Builder()
                .url("https://dns.google/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootsrapHostsV4 + bootsrapHostsV6)
                .client(OkHttpClient())
                .build()
        }

    private fun getByIPv4(vararg ips: String): List<InetAddress> {
        return try {
            ips.map { ip ->
                Inet4Address.getByName(ip)
            }
        } catch (e: UnknownHostException) {
            throw RuntimeException(e)
        }
    }

    private fun getByIPv6(vararg ips: String): List<InetAddress> {
        return try {
            ips.map { ip ->
                Inet6Address.getByName(ip)
            }
        } catch (e: UnknownHostException) {
            throw RuntimeException(e)
        }
    }
}