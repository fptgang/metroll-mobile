package com.vidz.metroll_mobile

import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import com.vidz.base.components.NotificationBannerManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : MultiDexApplication() {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize notification channels for banner notifications
        NotificationBannerManager.initializeNotificationChannels(this)
    }
}
