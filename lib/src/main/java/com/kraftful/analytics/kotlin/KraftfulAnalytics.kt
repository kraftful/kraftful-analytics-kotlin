package com.kraftful.analytics.kotlin

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.segment.analytics.kotlin.android.Analytics
import com.segment.analytics.kotlin.core.Analytics
import com.segment.analytics.kotlin.core.Settings
import com.segment.analytics.kotlin.core.manuallyEnableDestination
import com.segment.analytics.kotlin.core.platform.Plugin
import com.segment.analytics.kotlin.core.platform.plugins.SegmentDestination
import kotlinx.serialization.json.*

/**
 * A Kraftful Analytics client.
 */
class KraftfulAnalytics(private val analytics: Analytics) {

    companion object Singleton {
        lateinit var singleton: KraftfulAnalytics

        /**
         * Instantiates a global instance of Kraftful Analytics client accessible.
         */
        fun initialize(
            writeKey: String,
            applicationContext: Context,
            userIdProvider: UserIdProvider? = null
        ) {
            val analytics = Analytics(writeKey, applicationContext) {
                trackApplicationLifecycleEvents = true
                autoAddSegmentDestination = false
                apiHost = "analytics-ingestion.kraftful.com/"
                cdnHost = "analytics-ingestion.kraftful.com/"
            }
            analytics.add(AndroidRecordScreenPlugin())
            val segmentDestination = SegmentDestination()
            segmentDestination.update(Settings(
                integrations = buildJsonObject {
                    put("Segment.io", true)
                }
            ), Plugin.UpdateType.Initial)
            analytics.add(segmentDestination)
            analytics.manuallyEnableDestination(segmentDestination)

            val kraftfulAnalytics = KraftfulAnalytics(analytics)

            if (userIdProvider != null) {
                ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        kraftfulAnalytics.trackAppReturn(userIdProvider())
                    }
                })
            }

            singleton = kraftfulAnalytics
        }
    }

    /**
     * Tracks a `Sign In Start` event.
     */
    fun trackSignInStart() {
        this.analytics.track("Sign In Start")
    }

    /**
     * Tracks a `Sign In Success` event.
     */
    fun trackSignInSuccess(userId: String) {
        this.analytics.identify(userId)
        this.analytics.track("Sign In Success")
    }

    /**
     * Tracks a `Connection Start` event.
     */
    fun trackConnectionStart() {
        this.analytics.track("Connection Start")
    }

    /**
     * Tracks a `Connection Success` event.
     */
    fun trackConnectionSuccess() {
        this.analytics.track("Connection Success")
    }

    /**
     * Tracks a feature use event.
     */
    fun trackFeatureUse(feature: String) {
        this.analytics.track(feature)
    }

    /**
     * Tracks a `Return` event.
     */
    fun trackAppReturn(userId: String?) {
        if (userId != null) {
            this.analytics.identify(userId)
        }
        this.analytics.track("Return")
    }
}

private typealias UserIdProvider = () -> String?
