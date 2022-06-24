package com.kraftful.analytics.kotlin

import com.segment.analytics.kotlin.core.*
import com.segment.analytics.kotlin.core.platform.Plugin
import com.segment.analytics.kotlin.core.platform.VersionedPlugin
import com.segment.analytics.kotlin.core.platform.plugins.logger.LogFilterKind
import com.segment.analytics.kotlin.core.platform.plugins.logger.log
import com.segment.analytics.kotlin.core.utilities.putIntegrations
import java.util.*
import kotlin.concurrent.schedule

class KraftfulSessionPlugin : Plugin, VersionedPlugin {
    override val type: Plugin.Type = Plugin.Type.Enrichment
    override lateinit var analytics: Analytics
    private var sessionID: Long = -1
    private val key = "Kraftful"

    private var timer: TimerTask? = null
    private val fireTime: Long = 300000

    // Add the session_id to the Amplitude payload for cloud mode to handle.
    private inline fun <reified T : BaseEvent?> insertSession(payload: T?): BaseEvent? {
        var returnPayload = payload
        payload?.let {
            analytics.log(
                message = "Running ${payload.type} payload through AmplitudeSession",
                kind = LogFilterKind.DEBUG
            )
            returnPayload =
                payload.putIntegrations(key, mapOf("session_id" to sessionID)) as T?
        }
        return returnPayload
    }

    override fun execute(event: BaseEvent): BaseEvent? {
        var result: BaseEvent? = event
        when (result) {
            is IdentifyEvent -> {
                result = identify(result)
            }
            is TrackEvent -> {
                result = track(result)
            }
            is GroupEvent -> {
                result = group(result)
            }
            is ScreenEvent -> {
                result = screen(result)
            }
            is AliasEvent -> {
                result = alias(result)
            }
            else -> {
                // do nothing
            }
        }
        return result
    }

    private fun track(payload: TrackEvent): BaseEvent? {
        if (payload.event == "Application Backgrounded") {
            onBackground()
        } else if (payload.event == "Application Opened") {
            onForeground()
        }
        insertSession(payload)
        return payload
    }

    private fun identify(payload: IdentifyEvent): BaseEvent? {
        insertSession(payload)
        return payload
    }

    private fun screen(payload: ScreenEvent): BaseEvent? {
        insertSession(payload)
        return payload
    }

    private fun group(payload: GroupEvent): BaseEvent? {
        insertSession(payload)
        return payload
    }

    private fun alias(payload: AliasEvent): BaseEvent? {
        insertSession(payload)
        return payload
    }

    private fun onBackground() {
        stopTimer()
    }

    private fun onForeground() {
        startTimer()
    }

    private fun startTimer() {

        // Set the session id
        sessionID = Calendar.getInstance().timeInMillis

        timer = Timer().schedule(fireTime) {
            stopTimer()
            startTimer()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        sessionID = -1
    }

    override fun version(): String {
        return "1.0.0"
    }
}
