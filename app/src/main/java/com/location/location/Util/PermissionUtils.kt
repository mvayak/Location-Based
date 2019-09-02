package com.location.location.Util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {

    fun checkRunTimePermission(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (!checkPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    //    public static boolean isDeviceInfoGranted(Context context) {
    //        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    //    }

    @SuppressLint("NewApi")
    fun requestPermission(o: Any, permissionId: Int, vararg permissions: String) {
        if (o is Fragment) {
            o.requestPermissions(permissions, permissionId)
        } else if (o is Activity) {
            ActivityCompat.requestPermissions(o as AppCompatActivity, permissions, permissionId)
        }
    }
}