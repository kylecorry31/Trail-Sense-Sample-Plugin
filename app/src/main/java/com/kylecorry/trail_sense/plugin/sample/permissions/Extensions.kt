package com.kylecorry.trail_sense.plugin.sample.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import com.kylecorry.andromeda.core.system.Package
import com.kylecorry.andromeda.core.tryOrDefault
import java.security.MessageDigest

fun requireCallerPermission(context: Context, permission: String) {
    context.enforceCallingPermission(permission, "Permission $permission required")
}

fun requireCallerSignature(context: Context, allowedSignatures: List<String>) {
    if (!binderCallerHasSignature(context, allowedSignatures)) {
        throw SecurityException("Caller is not allowed'")
    }
}

fun binderCallerHasSignature(
    context: Context,
    allowedSignatures: List<String>
): Boolean {
    val callingUid = Binder.getCallingUid()
    val packages = context.packageManager.getPackagesForUid(callingUid) ?: return false
    for (packageName in packages) {
        val signatures = Package.getSignatureSha256Fingerprints(context, packageName)
        if (signatures.any { it in allowedSignatures }) {
            return true
        }
    }
    return false
}

fun Package.getSelfSignatureSha256Fingerprints(context: Context): List<String> {
    return getSignatureSha256Fingerprints(context, context.packageName)
}

@Suppress("DEPRECATION")
fun Package.getSignatureSha256Fingerprints(
    context: Context,
    packageName: String
): List<String> {
    val info = tryOrDefault(null) {
        Package.getPackageInfo(
            context,
            packageName,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
        )
    } ?: return emptyList()
    val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val signingInfo = info.signingInfo ?: return emptyList()
        if (signingInfo.hasMultipleSigners()) {
            signingInfo.apkContentsSigners
        } else {
            signingInfo.signingCertificateHistory
        }
    } else {
        info.signatures ?: return emptyList()
    }
    val digest = MessageDigest.getInstance("SHA-256")
    val signatureHashes = mutableListOf<String>()
    for (sig in signatures) {
        val digest = digest.digest(sig.toByteArray())
        val hash = digest.joinToString(":") { String.format("%02X", it) }
        signatureHashes.add(hash)
    }
    return signatureHashes.distinct()
}