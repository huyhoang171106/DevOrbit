package vn.edu.uit.devorbit.mobile.network.stomp

import org.junit.Assert.*
import org.junit.Test

class StompClientTest {

    @Test
    fun `initial state is disconnected`() {
        val client = StompClient()
        assertFalse(client.isConnected())
    }

    @Test
    fun `subscriptions tracked before connect`() {
        val client = StompClient()
        client.subscribe("/topic/channel/1", "sub-1")
        client.subscribe("/topic/channel/2", "sub-2")
        client.disconnect()
        assertFalse(client.isConnected())
    }

    @Test
    fun `send without connection reports error`() {
        val client = StompClient()
        var errorReceived: String? = null
        client.setListener(object : StompEventListener {
            override fun onConnected() {}
            override fun onMessage(destination: String, body: String) {}
            override fun onError(message: String) { errorReceived = message }
            override fun onDisconnected() {}
            override fun onConnecting(attempt: Int) {}
        })
        client.send("/app/chat.send/1", "{\"content\":\"hi\"}")
        assertEquals("Not connected", errorReceived)
    }
}
