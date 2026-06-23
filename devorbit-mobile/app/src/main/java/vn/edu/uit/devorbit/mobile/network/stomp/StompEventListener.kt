package vn.edu.uit.devorbit.mobile.network.stomp

interface StompEventListener {
    fun onConnected()
    fun onMessage(destination: String, body: String)
    fun onError(message: String)
    fun onDisconnected()
    fun onConnecting(attempt: Int)
}
