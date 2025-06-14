package com.kylecorry.trail_sense_dem.app

import android.content.Context
import com.kylecorry.andromeda.core.cache.AppServiceRegistry
import com.kylecorry.trail_sense_dem.infrastructure.persistence.AppDatabase
import com.kylecorry.trail_sense_dem.ui.FormatService

object ServiceRegister {
    fun setup(context: Context) {
        val appContext = context.applicationContext

        // Shared services
        AppServiceRegistry.register(FormatService.getInstance(appContext))
        AppServiceRegistry.register(AppDatabase.getInstance(appContext))
    }
}