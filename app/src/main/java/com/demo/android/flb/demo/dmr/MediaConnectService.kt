package com.demo.android.flb.demo.dmr

import org.fourthline.cling.support.connectionmanager.ConnectionManagerService
import org.fourthline.cling.support.model.ProtocolInfo
import org.seamless.util.MimeType


/**
 * @author Created by 25011 on 2018/8/9.
 * 支持的协议
 */
class MediaConnectService : ConnectionManagerService() {
    init {
        sinkProtocolInfo.add(ProtocolInfo(MimeType.valueOf("video/mp4")))
        sinkProtocolInfo.add(ProtocolInfo(MimeType.valueOf("image/png")))
    }
}