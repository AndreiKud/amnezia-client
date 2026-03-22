package org.amnezia.vpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class VpnControllerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            val proto = VpnStateStore.getVpnState().vpnProto ?: return@launch
            Intent(context, proto.serviceClass).also {
                val localBroadcastIntent = when (intent.action) {
                    "org.amnezia.vpn.action.START_VPN" -> {
                        try {
                            context.startService(it)
                        } catch (_: Throwable) {
                            return@launch
                        }
                        Intent(ACTION_CONNECT).apply {
                            setPackage(context.packageName)
                        }
                    }
                    "org.amnezia.vpn.action.STOP_VPN" -> {
                        Intent(ACTION_DISCONNECT).apply {
                            setPackage(context.packageName)
                        }
                    }
                    else -> null
                }
                localBroadcastIntent?.let(context::sendBroadcast)
            }
        }
    }
}
