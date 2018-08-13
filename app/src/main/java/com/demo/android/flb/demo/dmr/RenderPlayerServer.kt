package com.demo.android.flb.demo.dmr

import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Binder
import android.os.IBinder
import com.demo.android.flb.demo.activity.PlayerActivity
import com.demo.android.flb.demo.utils.KeyValueUtils

/**
 * @author FLB
 * 用户启动播放器的广播
 */
class RenderPlayerServer : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.getStringExtra(KeyValueUtils.TYPE)) {
                KeyValueUtils.MP4 -> {
                    val intent1 = Intent(this, PlayerActivity::class.java)
                    intent1.putExtra(KeyValueUtils.PATH, intent.getStringExtra(KeyValueUtils.PATH))
                    intent1.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent1)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder = Binder()
}
