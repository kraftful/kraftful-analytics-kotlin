package com.kraftful.analytics.kotlin

import com.segment.analytics.kotlin.core.Analytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class KraftfulAnalyticsTest {

    @Test
    fun trackSignInStart() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        KraftfulAnalytics(analyticsMock).trackSignInStart()

        verify { analyticsMock.track("Sign In Start") }
    }

    @Test
    fun trackSignInSuccess() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        KraftfulAnalytics(analyticsMock).trackSignInSuccess("1")

        verify { analyticsMock.identify("1") }
        verify { analyticsMock.track("Sign In Success") }
    }

    @Test
    fun trackConnectionStart() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        KraftfulAnalytics(analyticsMock).trackConnectionStart()

        verify { analyticsMock.track("Connection Start") }
    }

    @Test
    fun trackConnectionSuccess() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        KraftfulAnalytics(analyticsMock).trackConnectionSuccess()

        verify { analyticsMock.track("Connection Success") }
    }

    @Test
    fun trackFeatureUse() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        KraftfulAnalytics(analyticsMock).trackFeatureUse("foo")

        verify { analyticsMock.track("foo") }
    }

    @Test
    fun trackAppReturn() {
        val analyticsMock = mockk<Analytics>(relaxed = true)

        val userIdMaybe: String? = "1" // forcing a compile-time check with the '?'
        KraftfulAnalytics(analyticsMock).trackAppReturn(userIdMaybe)

        verify { analyticsMock.identify("1") }
        verify { analyticsMock.track("Return") }
    }
}
