package com.vidz.base.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * String extensions for common formatting operations
 */

/**
 * Converts a string containing milliseconds to a formatted date string
 * @param pattern The date pattern to use (default: "dd/MM/yyyy HH:mm:ss")
 * @return Formatted date string or original string if parsing fails
 */
fun String.toFormattedDate(pattern: String = "dd/MM/yyyy HH:mm:ss"): String {
    return try {
        val milliseconds = this.toLongOrNull() ?: return this
        val date = Date(milliseconds)
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        this // Return original string if parsing fails
    }
}

/**
 * Converts a string containing milliseconds to a formatted date string with custom pattern
 * @param pattern The date pattern to use
 * @return Formatted date string or original string if parsing fails
 */
fun String.toDateString(pattern: String): String {
    return toFormattedDate(pattern)
}

/**
 * Converts a string containing milliseconds to a short date format (dd/MM/yyyy)
 * @return Formatted date string or original string if parsing fails
 */
fun String.toShortDate(): String {
    return toFormattedDate("dd/MM/yyyy")
}

/**
 * Converts a string containing milliseconds to a time format (HH:mm:ss)
 * @return Formatted time string or original string if parsing fails
 */
fun String.toTimeString(): String {
    return toFormattedDate("HH:mm:ss")
}

/**
 * Converts a string containing milliseconds to a date and time format with 12-hour clock
 * @return Formatted date string or original string if parsing fails
 */
fun String.toDateTime12Hour(): String {
    return toFormattedDate("dd/MM/yyyy hh:mm:ss a")
}

/**
 * Converts a string containing milliseconds to a relative time description
 * @return Relative time string (e.g., "2 hours ago", "Yesterday", etc.)
 */
fun String.toRelativeTime(): String {
    return try {
        val milliseconds = this.toLongOrNull() ?: return this
        val now = System.currentTimeMillis()
        val diff = now - milliseconds
        
        when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            diff < 172_800_000 -> "Yesterday"
            diff < 604_800_000 -> "${diff / 86_400_000} days ago"
            else -> toFormattedDate("dd/MM/yyyy")
        }
    } catch (e: Exception) {
        this
    }
} 