package vn.edu.uit.devorbit.mobile.network.stomp

import org.junit.Assert.*
import org.junit.Test

class StompFrameTest {

    @Test
    fun `parse CONNECTED frame`() {
        val raw = "CONNECTED\nversion:1.2\nheart-beat:10000,10000\n\n\u0000"
        val frame = StompFrame.parse(raw)
        assertNotNull(frame)
        assertEquals("CONNECTED", frame!!.command)
        assertEquals("1.2", frame.headers["version"])
        assertEquals("10000,10000", frame.headers["heart-beat"])
    }

    @Test
    fun `parse MESSAGE frame with body`() {
        val raw = "MESSAGE\ndestination:/topic/channel/1\ncontent-type:application/json\n\n{\"id\":1,\"content\":\"hello\"}\n\u0000"
        val frame = StompFrame.parse(raw)
        assertNotNull(frame)
        assertEquals("MESSAGE", frame!!.command)
        assertEquals("{\"id\":1,\"content\":\"hello\"}", frame.body)
    }

    @Test
    fun `serialize CONNECT frame`() {
        val frame = StompFrame.connect("test-token")
        val serialized = frame.serialize()
        assertTrue(serialized.startsWith("CONNECT\n"))
        assertTrue(serialized.contains("Authorization:Bearer test-token"))
        assertTrue(serialized.endsWith("\u0000"))
    }

    @Test
    fun `serialize SEND frame`() {
        val frame = StompFrame.send("/app/chat.send/1", "{\"content\":\"hi\"}")
        val serialized = frame.serialize()
        assertTrue(serialized.startsWith("SEND\n"))
        assertTrue(serialized.contains("destination:/app/chat.send/1"))
        assertTrue(serialized.contains("{\"content\":\"hi\"}"))
    }

    @Test
    fun `parse null on blank input`() {
        assertNull(StompFrame.parse(""))
        assertNull(StompFrame.parse("\u0000"))
    }

    @Test
    fun `subscribe frame has correct headers`() {
        val frame = StompFrame.subscribe("/topic/channel/1", "sub-1")
        assertEquals("SUBSCRIBE", frame.command)
        assertEquals("/topic/channel/1", frame.headers["destination"])
        assertEquals("sub-1", frame.headers["id"])
    }
}
