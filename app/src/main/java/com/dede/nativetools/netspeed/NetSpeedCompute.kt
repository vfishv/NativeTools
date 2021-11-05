package com.dede.nativetools.netspeed

import com.dede.nativetools.netspeed.stats.INetStats
import com.dede.nativetools.util.HandlerTicker
import kotlin.math.max
import kotlin.math.roundToLong
import kotlin.properties.Delegates

typealias OnTickNetSpeed = (Long, Long) -> Unit

/**
 * 网速计算
 */
class NetSpeedCompute(private var onTick: OnTickNetSpeed? = null) {

    private var rxBytes: Long = 0L
        set(value) {
            rxSpeed = env(value, field, interval)
            field = value
        }
    private var txBytes: Long = 0L
        set(value) {
            txSpeed = env(value, field, interval)
            field = value
        }
    private val netStats: INetStats = INetStats.getInstance()

    var interval: Int by Delegates.observable(NetSpeedPreferences.DEFAULT_INTERVAL) { _, old, new ->
        if (old != new) {
            reset()
            handlerTicker.interval = new.toLong()
        }
    }

    private fun env(a: Long, b: Long, ms: Int): Long {
        return max((ms / 1000.0 * (a - b)).roundToLong(), 0L)
    }

    private val handlerTicker: HandlerTicker = HandlerTicker(interval.toLong()) {
        synchronized(this) {
            rxBytes = netStats.getRxBytes()
            txBytes = netStats.getTxBytes()
            onTick?.invoke(rxSpeed, txSpeed)
        }
    }

    var rxSpeed: Long = 0L
        private set
    var txSpeed: Long = 0L
        private set

    private fun reset() {
        rxSpeed = 0L
        txSpeed = 0L
        rxBytes = netStats.getRxBytes()
        txBytes = netStats.getTxBytes()
    }

    fun start() {
        reset()
        handlerTicker.start()
    }

    fun stop() {
        handlerTicker.stop()
    }

}