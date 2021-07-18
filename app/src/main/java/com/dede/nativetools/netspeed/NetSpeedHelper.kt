package com.dede.nativetools.netspeed

import android.net.TrafficStats
import com.dede.nativetools.util.IntervalHelper
import java.lang.reflect.Method
import kotlin.properties.Delegates

typealias NetSpeedChanged = (Long, Long) -> Unit

/**
 * 网速计算
 */
class NetSpeedHelper(private var netSpeedChanged: NetSpeedChanged? = null) {

    private var rxBytes: Long = 0L
    private var txBytes: Long = 0L

    var interval: Int by Delegates.observable(NetSpeedConfiguration.DEFAULT_INTERVAL) { _, old, new ->
        if (old != new) {
            reset()
            intervalHelper.setInterval(new.toLong())
        }
    }

    private val intervalHelper: IntervalHelper = IntervalHelper(interval.toLong()) {
        synchronized(this) {
            val rxBytes = getRxBytes()
            val txBytes = getTxBytes()
            val rxSpeed = (rxBytes - this.rxBytes).toDouble() / interval * 1000 + .5f
            val txSpeed = (txBytes - this.txBytes).toDouble() / interval * 1000 + .5f
            this.rxSpeed = rxSpeed.toLong()
            this.txSpeed = txSpeed.toLong()
            this.rxBytes = rxBytes
            this.txBytes = txBytes

            netSpeedChanged?.invoke(this.rxSpeed, this.txSpeed)
        }
    }

    var rxSpeed: Long = 0L
        private set
    var txSpeed: Long = 0L
        private set

    private fun reset() {
        rxBytes = getRxBytes()
        txBytes = getTxBytes()
    }

    fun resume() {
        reset()
        intervalHelper.start()
    }

    fun pause() {
        intervalHelper.stop()
    }

    private var methodGetLoopbackRxBytes: Method? = null
    private var methodGetLoopbackTxBytes: Method? = null

    init {
        try {
            methodGetLoopbackRxBytes = TrafficStats::class.java.getMethod("getLoopbackRxBytes")
            methodGetLoopbackTxBytes = TrafficStats::class.java.getMethod("getLoopbackTxBytes")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTxBytes(): Long {
        return getTotalTxBytes() - getLoopbackTxBytes()
    }

    private fun getRxBytes(): Long {
        return getTotalRxBytes() - getLoopbackRxBytes()
    }

    private fun getLoopbackRxBytes(): Long {
        if (methodGetLoopbackRxBytes == null) return 0L
        val result = try {
            methodGetLoopbackRxBytes!!.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return (result as? Long) ?: 0L
    }

    private fun getLoopbackTxBytes(): Long {
        if (methodGetLoopbackTxBytes == null) return 0L
        val result = try {
            methodGetLoopbackTxBytes!!.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return (result as? Long) ?: 0L
    }

    private fun getTotalRxBytes(): Long {
        return TrafficStats.getTotalRxBytes()
    }

    private fun getTotalTxBytes(): Long {
        return TrafficStats.getTotalTxBytes()
    }
}