package vn.edu.uit.devorbit.mobile.network.stomp

data class StompFrame(
    val command: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    fun serialize(): String {
        val sb = StringBuilder(command).append('\n')
        headers.forEach { (k, v) -> sb.append(k).append(':').append(v).append('\n') }
        sb.append('\n')
        body?.let { sb.append(it) }
        sb.append('\u0000')
        return sb.toString()
    }

    companion object {
        fun parse(raw: String): StompFrame? {
            val trimmed = raw.trimStart('\u0000', '\n', '\r')
            if (trimmed.isBlank()) return null
            val lines = trimmed.lines()
            val command = lines.first().trim()
            val headers = mutableMapOf<String, String>()
            var bodyStart = -1
            for (i in 1 until lines.size) {
                val line = lines[i]
                if (line.isBlank()) { bodyStart = i + 1; break }
                val colonIdx = line.indexOf(':')
                if (colonIdx > 0) {
                    headers[line.substring(0, colonIdx).trim()] = line.substring(colonIdx + 1).trim()
                }
            }
            val body = if (bodyStart >= 0 && bodyStart < lines.size) {
                lines.subList(bodyStart, lines.size).joinToString("\n").trimEnd('\u0000', '\n', '\r')
            } else null
            return StompFrame(command, headers, body)
        }

        fun connect(token: String, heartbeat: Pair<Long, Long> = 10000L to 10000L): StompFrame {
            return StompFrame(
                command = "CONNECT",
                headers = mapOf(
                    "accept-version" to "1.2",
                    "heart-beat" to "${heartbeat.first},${heartbeat.second}",
                    "Authorization" to "Bearer $token"
                )
            )
        }

        fun subscribe(destination: String, id: String): StompFrame {
            return StompFrame(
                command = "SUBSCRIBE",
                headers = mapOf("id" to id, "destination" to destination)
            )
        }

        fun unsubscribe(id: String): StompFrame {
            return StompFrame(
                command = "UNSUBSCRIBE",
                headers = mapOf("id" to id)
            )
        }

        fun send(destination: String, body: String): StompFrame {
            return StompFrame(
                command = "SEND",
                headers = mapOf(
                    "destination" to destination,
                    "content-type" to "application/json"
                ),
                body = body
            )
        }

        fun disconnect(receipt: String? = null): StompFrame {
            val headers = mutableMapOf<String, String>()
            receipt?.let { headers["receipt"] = it }
            return StompFrame(command = "DISCONNECT", headers = headers)
        }
    }
}
