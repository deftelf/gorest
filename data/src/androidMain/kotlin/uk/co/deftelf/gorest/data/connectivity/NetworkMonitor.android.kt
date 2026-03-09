package uk.co.deftelf.gorest.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import uk.co.deftelf.gorest.data.local.DatabaseDriverFactory

actual class NetworkMonitor actual constructor() {
    actual val isConnected: Flow<Boolean> = callbackFlow {
        val cm = DatabaseDriverFactory.appContext
            .getSystemService(ConnectivityManager::class.java)

        fun hasInternet(): Boolean {
            val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(hasInternet()) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
            }
            override fun onLost(network: Network) { trySend(hasInternet()) }
        }

        trySend(hasInternet())
        cm.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
