package uk.co.deftelf.gorest.data.connectivity

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.net.NetworkInterface

actual class NetworkMonitor actual constructor() {
    actual val isConnected: Flow<Boolean> = flow {
        while (true) {
            val connected = try {
                NetworkInterface.getNetworkInterfaces()
                    ?.toList()
                    ?.any { !it.isLoopback && it.isUp && it.inetAddresses.hasMoreElements() }
                    ?: false
            } catch (_: Exception) {
                false
            }
            emit(connected)
            delay(5_000)
        }
    }.distinctUntilChanged()
}
