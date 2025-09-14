package com.vineeth.expensetracker.util

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import android.view.Display

object PerformanceOptimizer {
    
    /**
     * Enable high refresh rate support for 120Hz scrolling - Immediate application
     */
    fun enableHighRefreshRate(activity: Activity) {
        // Apply hardware acceleration immediately
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        // Force 120Hz refresh rate immediately
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val display = activity.windowManager.defaultDisplay
                val supportedModes = display.supportedModes
                
                // Find the highest refresh rate mode (preferably 120Hz)
                var bestMode = display.mode
                for (mode in supportedModes) {
                    if (mode.refreshRate > bestMode.refreshRate) {
                        bestMode = mode
                    }
                }
                
                // Apply the best refresh rate immediately
                activity.window.attributes.preferredRefreshRate = bestMode.refreshRate
                
                // Force immediate refresh rate change
                activity.window.attributes = activity.window.attributes
                
            } catch (e: Exception) {
                // Fallback: try to set 120Hz directly
                try {
                    activity.window.attributes.preferredRefreshRate = 120f
                    activity.window.attributes = activity.window.attributes
                } catch (e2: Exception) {
                    // Final fallback to default
                }
            }
        }
    }
    
    /**
     * Optimize window for smooth scrolling
     */
    fun optimizeForScrolling(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }
}
