package uk.co.deftelf.gorest.data.connectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

expect class NetworkMonitor() {
    val isConnected: Flow<Boolean>
}
