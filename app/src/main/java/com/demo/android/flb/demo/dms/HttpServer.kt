package com.demo.android.flb.demo.dms

import com.demo.android.flb.demo.utils.KeyValueUtils
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.resource.FileResource
import org.eclipse.jetty.util.resource.Resource
import java.io.File

/**
 * @author Created by 25011 on 2018/8/6.
 * 应用内部服务器，向外部分享资源
 */
class HttpServer constructor(port: Int) : Runnable {
    private var mServer: Server? = null

    init {
        mServer = Server(port)
    }

    override fun run() {
        val context = ServletContextHandler()
        context.contextPath = "/"
        context.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false")
        mServer!!.handler = context
        context.addServlet(VideoResourceServlet::class.java, "/${KeyValueUtils.VIDEO}/*")
        if (!mServer!!.isStarted && !mServer!!.isStarting) {
            mServer!!.start()
        }
    }

}

class VideoResourceServlet : DefaultServlet() {

    override fun getResource(pathInContext: String): Resource? {
        var resource: Resource? = null
        println("pathInContext:$pathInContext")
        if (pathInContext.startsWith("/${KeyValueUtils.VIDEO}/")) {
            val id = pathInContext.replace("/${KeyValueUtils.VIDEO}/", "")
            if (ContentTree.instance.contain(id)) {
                val contentItem = ContentTree.instance.getContentItem(id)
                if (contentItem!!.isItem) {
                    val thumbPath = contentItem.thumbPath
                    resource = FileResource.newResource(File(thumbPath))
                }
            } else {
                val file = File(id)
                if (file.exists()) {
                    resource = FileResource.newResource(file)
                }
            }
        }
        return resource
    }
}
