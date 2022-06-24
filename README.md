# Kraftful Analytics for Kotlin

## Installation
To install Kraftful Analytics add this to your `build.gradle.kts`:
```
implementation("com.kraftful.analytics.kotlin:kraftful-analytics:<latest_version>")
```

## Usage

The KraftfulAnalytics API exposes the following methods:

### initialize
This will initialize a singleton instance of `KraftfulAnalytics` accessible via `KraftfulAnalytics.singleton`.
In your application class:
```kotlin
package com.example.myfirstapp

import android.app.Application
import com.kraftful.analytics.KraftfulAnalytics


class MyFirstApplication : Application() {
    companion object {
        var userId: String? = null
    }

    override fun onCreate() {
        super.onCreate()

        KraftfulAnalytics.initialize(
            writeKey = "<SEGMENT_WRITE_KEY>",
            applicationContext = applicationContext,
            userIdProvider = { userId },
        )
    }
}
```

### trackSignInStart & trackSignInSuccess
Add these calls to your login and registration flows.
Typically, the start call happens when your login/register screen loads and the success call happens when the user successfully logs in/registers. 
```kotlin
class SignInActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        KraftfulAnalytics.singleton.trackSignInStart()
    }

    fun handleSuccessfulSignIn(userId: String) {
            MyFirstApplication.userId = userId

            KraftfulAnalytics.singleton.trackSignInSuccess(userId)
    }
}
```
```kotlin
class RegisterActivity : AppCompatActivity() {

    fun handleSuccessfulRegistration(userId: String) {
            MyFirstApplication.userId = userId

            KraftfulAnalytics.singleton.trackSignInSuccess(userId)
    }
}
```

### trackConnectionStart & trackConnectionSuccess
Add these calls to your device connection flows.
```kotlin
class ConnectionActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        KraftfulAnalytics.singleton.trackConnectionStart()
    }

    fun handleSuccessfulDeviceConnection() {
            KraftfulAnalytics.singleton.trackConnectionSuccess()
    }
}
```

### trackFeatureUse
Add this call to where a feature use occurs.
```kotlin
class HomeActivity : AppCompatActivity() {

    fun handleButtonClick() {
            KraftfulAnalytics.singleton.trackFeatureUse("Light color change")
    }
}
```

### trackAppReturn
Usually you don't need to make this call explicitly if you use the `initialize` method to initialize `KraftfulAnalytics`.
It is automatically invoked whenever your app is foregrounded.

If you initialized `KraftfulAnalytics` yourself then add this call to where your app is foregrounded.
This should be done where you rehydrate your user information,
so you can pass the logged in userId if they are already logged in.

```kotlin
//...
/**
 * Runs when a user returns to the app.
 */
fun handleUserReturn(userId: String) {
    KraftfulAnalytics.singleton.trackAppReturn(userId)
}
//...
```

## Custom initialization
If you need more control over the initialization process you can instantiate `KraftfulAnalytics` yourself:

```kotlin
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.segment.analytics.kotlin.android.Analytics
import com.segment.analytics.kotlin.core.Analytics

class MyFirstApplication : Application() {
    companion object {
        var userId: String? = null
    }

    override fun onCreate() {
        super.onCreate()

        val analytics = Analytics("<SEGMENT_WRITE_KEY>", applicationContext) {
            // Automatically track when the app is foregrounded and backgrounded
            trackApplicationLifecycleEvents = true
            //...
        }

        // Automatically track screens
        analytics.add(AndroidRecordScreenPlugin())
        
        // Automatically track user sessions
        analytics.add(KraftfulSessionPlugin())
        //...

        val kraftfulAnalytics = KraftfulAnalytics(analytics)

        // Automatically call trackAppReturn when the app is foregrounded
        if (userIdProvider != null) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    kraftfulAnalytics.trackAppReturn(userIdProvider())
                }
            })
        }

        KraftfulAnalytics.singleton = kraftfulAnalytics
    }
}
```
