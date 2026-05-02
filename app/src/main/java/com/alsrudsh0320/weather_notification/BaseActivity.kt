package com.alsrudsh0320.weather_notification

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    protected fun setupEdgeToEdge(rootView: View) {
        enableEdgeToEdge()      // 화면 전체를 활용하기 위함

        // EdgeToEdge 레이아웃 모드에서 사용자 설정 XML코드의 padding을 적용하기 위한 코드
        val origPadding = intArrayOf(
            rootView.paddingLeft,
            rootView.paddingTop,
            rootView.paddingRight,
            rootView.paddingBottom
        )

        // 시스템 바 높이 만큼 적절히 안쪽여백 주기 + 추가 XML코드 설정
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                origPadding[0] + sys.left,
                origPadding[1] + sys.top,
                origPadding[2] + sys.right,
                origPadding[3] + sys.bottom
            )
            insets
        }
    }

    /** 여기 부터 사용자 알림 권한 */
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                openAppNotificationSettings()
            }
        }

    protected fun ensureNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
                return false
            }
        }

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            openAppNotificationSettings()
            return false
        }

        return true
    }

    protected fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }
    /** 여기 까지 사용자 알림 권한 */
}