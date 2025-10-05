package com.kylecorry.trail_sense.plugin.sample.service

import android.content.Context
import com.kylecorry.andromeda.core.system.Package
import com.kylecorry.andromeda.permissions.Permissions

object PluginPermissions {

    fun enforceSignature(context: Context) {
        Permissions.enforceCallingSignature(
            context,
            Package.getSelfSignatureSha256Fingerprints(context) + listOf<String>(
                // TODO: Put all the allowed signatures here
            )
        )
    }

    fun enforcePermissions(context: Context, vararg permissions: String) {
        for (permission in permissions) {
            Permissions.enforceCallingPermission(context, permission, callerOnly = true)
        }
    }

}