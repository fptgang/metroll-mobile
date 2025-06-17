package com.vidz.base.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationBannerManager {
    
    // Notification Channels
    private const val CART_CHANNEL_ID = "cart_notifications"
    private const val ORDER_CHANNEL_ID = "order_notifications"
    private const val PROMOTION_CHANNEL_ID = "promotion_notifications"
    private const val GENERAL_CHANNEL_ID = "general_notifications"
    
    // Notification IDs
    private const val CART_NOTIFICATION_ID = 1001
    private const val ORDER_NOTIFICATION_ID = 1002
    private const val PROMOTION_NOTIFICATION_ID = 1003
    private const val GENERAL_NOTIFICATION_ID = 1004
    
    /**
     * Check if heads-up notifications are enabled for the app
     */
    fun areHeadsUpNotificationsEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Check if notifications are enabled for the app
            if (!notificationManager.areNotificationsEnabled()) {
                return false
            }
            
            // Check if the high-importance channels can show heads-up notifications
            val cartChannel = notificationManager.getNotificationChannel(CART_CHANNEL_ID)
            val orderChannel = notificationManager.getNotificationChannel(ORDER_CHANNEL_ID)
            
            return (cartChannel?.importance == NotificationManager.IMPORTANCE_HIGH) &&
                   (orderChannel?.importance == NotificationManager.IMPORTANCE_HIGH)
        }
        return true // Pre-O devices don't have channel importance
    }

    /**
     * Check if the app can draw over other apps (for true floating notifications)
     */
    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // No permission needed on older versions
        }
    }

    /**
     * Open notification settings for the app
     */
    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Open overlay permission settings
     */
    fun openOverlaySettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    /**
     * Get detailed notification status
     */
    fun getNotificationStatus(context: Context): NotificationStatus {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        return NotificationStatus(
            notificationsEnabled = notificationManager.areNotificationsEnabled(),
            headsUpEnabled = areHeadsUpNotificationsEnabled(context),
            overlayEnabled = canDrawOverlays(context),
            doNotDisturbEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
            } else false
        )
    }

    /**
     * Data class for notification status
     */
    data class NotificationStatus(
        val notificationsEnabled: Boolean,
        val headsUpEnabled: Boolean,
        val overlayEnabled: Boolean,
        val doNotDisturbEnabled: Boolean
    ) {
        val allEnabled: Boolean
            get() = notificationsEnabled && headsUpEnabled && !doNotDisturbEnabled
        
        val bannerNotificationsWork: Boolean
            get() = notificationsEnabled && headsUpEnabled && !doNotDisturbEnabled
    }
    
    /**
     * Initialize all notification channels with Shopee Vietnam styling
     */
    fun initializeNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Cart Notifications Channel - Shopee Orange theme
            val cartChannel = NotificationChannel(
                CART_CHANNEL_ID,
                "🛒 Shopping Cart & Promotions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important notifications about your cart and special offers"
                enableLights(true)
                lightColor = 0xFFFF5722.toInt() // Shopee Orange
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200, 100, 200) // Shopee pattern
                setShowBadge(true)
                setBypassDnd(true)
            }
            
            // Order Notifications Channel - Success Green
            val orderChannel = NotificationChannel(
                ORDER_CHANNEL_ID,
                "📦 Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important notifications about your order status"
                enableLights(true)
                lightColor = 0xFF4CAF50.toInt() // Success Green
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300) // Success pattern
                setShowBadge(true)
                setBypassDnd(true)
            }
            
            // Promotion Notifications Channel - Hot Red
            val promotionChannel = NotificationChannel(
                PROMOTION_CHANNEL_ID,
                "🔥 Deals & Flash Sales",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special offers and flash sale notifications"
                enableLights(true)
                lightColor = 0xFFE91E63.toInt() // Hot Pink/Red
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 50, 100, 50, 100, 50, 100) // Exciting pattern
                setShowBadge(true)
            }
            
            // General Notifications Channel - Shopee Blue
            val generalChannel = NotificationChannel(
                GENERAL_CHANNEL_ID,
                "📱 General Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important notifications from metroll app"
                enableLights(true)
                lightColor = 0xFF2196F3.toInt() // Shopee Blue
                enableVibration(false)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannels(listOf(
                cartChannel,
                orderChannel,
                promotionChannel,
                generalChannel
            ))
        }
    }
    
    /**
     * Show a Shopee-style cart reminder notification with banner
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showCartReminderBanner(
        context: Context,
        title: String,
        message: String,
        itemCount: Int,
        iconResId: Int,
        intent: Intent? = null
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val notification = NotificationCompat.Builder(context, CART_CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$message\n\n💥 Shopee-style: Shop now for exclusive deals!"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setVibrate(longArrayOf(0, 200, 100, 200, 100, 200)) // Shopee vibration
            .setLights(0xFFFF5722.toInt(), 1000, 500) // Shopee orange light
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setNumber(itemCount)
            .setColor(0xFFFF5722.toInt()) // Shopee brand color
            .build()
        
        notificationManager.notify(CART_NOTIFICATION_ID, notification)
    }
    
    /**
     * Show a Shopee-style order update notification with banner
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showOrderUpdateBanner(
        context: Context,
        title: String,
        message: String,
        orderNumber: String,
        iconResId: Int,
        intent: Intent? = null
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val notification = NotificationCompat.Builder(context, ORDER_CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle("📦 $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$message\n\n🚚 Order Number: #$orderNumber\n✨ Thank you for shopping with metroll!"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setVibrate(longArrayOf(0, 300, 150, 300))
            .setLights(0xFF4CAF50.toInt(), 1000, 500) // Success green
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0xFF4CAF50.toInt()) // Success color
            .build()
        
        notificationManager.notify(ORDER_NOTIFICATION_ID, notification)
    }
    
    /**
     * Show a Shopee-style promotion notification
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showPromotionNotification(
        context: Context,
        title: String,
        message: String,
        iconResId: Int,
        intent: Intent? = null
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val notification = NotificationCompat.Builder(context, PROMOTION_CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle("🔥 $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$message\n\n⚡ FLASH SALE - Limited Time Only!\n🎁 Don't miss this golden opportunity!"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 100, 50, 100, 50, 100, 50, 100)) // Exciting pattern
            .setLights(0xFFE91E63.toInt(), 500, 200) // Hot pink flashing
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(0xFFE91E63.toInt()) // Hot promotion color
            .build()
        
        notificationManager.notify(PROMOTION_NOTIFICATION_ID, notification)
    }
    
    /**
     * Show a general notification
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showGeneralNotification(
        context: Context,
        title: String,
        message: String,
        iconResId: Int,
        intent: Intent? = null,
        isImportant: Boolean = false
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val builder = NotificationCompat.Builder(context, GENERAL_CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        
        if (isImportant) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
        
        notificationManager.notify(GENERAL_NOTIFICATION_ID, builder.build())
    }
    
    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }
    
    /**
     * Cancel specific notification
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }
    
    /**
     * Test method to show a Shopee-style banner notification instantly
     * Use this to test if banner notifications are working properly
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showTestBannerNotification(context: Context, iconResId: Int) {
        showCartReminderBanner(
            context = context,
            title = "🎉 metroll - Test Notification!",
            message = "Congratulations! Banner notifications are working perfectly! 🚀 You'll receive notifications about cart reminders and special offers!",
            itemCount = 1,
            iconResId = iconResId,
            intent = null
        )
    }
} 
