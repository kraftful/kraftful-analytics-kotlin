package com.kraftful.analytics.kotlin

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.kraftful.analytics.AndroidRecordScreenPlugin
import com.segment.analytics.kotlin.android.Analytics
import com.segment.analytics.kotlin.core.Analytics


class KraftfulAnalytics(private val analytics: Analytics) {

    companion object Singleton {
        lateinit var singleton: KraftfulAnalytics

        fun initialize(
            writeKey: String,
            applicationContext: Context,
            userIdProvider: UserIdProvider? = null
        ) {
            val analytics = Analytics(writeKey, applicationContext) {
                trackApplicationLifecycleEvents = true
            }
            analytics.add(AndroidRecordScreenPlugin())
            analytics.add(KraftfulSessionPlugin())

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

    fun trackSignInStart() {
        this.analytics.track("Sign In Start")
    }

    fun trackSignInSuccess(userId: String) {
        this.analytics.identify(userId)
        this.analytics.track("Sign In Success")
    }

    fun trackConnectionStart() {
        this.analytics.track("Connection Start")
    }

    fun trackConnectionSuccess() {
        this.analytics.track("Connection Success")
    }

    fun trackFeatureUse(feature: String) {
        this.analytics.track(feature)
    }

    fun trackAppReturn(userId: String?) {
        if (userId != null) {
            this.analytics.identify(userId)
        }
        this.analytics.track("Return")
    }
}

private typealias UserIdProvider = () -> String?
