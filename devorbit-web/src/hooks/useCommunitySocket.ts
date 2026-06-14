import { useEffect, useRef, useCallback, useState } from 'react'
import { Client, type IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getStudentToken } from '../lib/auth'
import type { ChannelPresenceResponse, ChatMessageResponse } from '../types/api'

type UseCommunitySocketOptions = {
  channelId: number | null
  enabled: boolean
  onMessage: (msg: ChatMessageResponse) => void
  onPresence?: (presence: ChannelPresenceResponse) => void
}

export function useCommunitySocket({ channelId, enabled, onMessage, onPresence }: UseCommunitySocketOptions) {
  const clientRef = useRef<Client | null>(null)
  const messageSubscriptionRef = useRef<{ id: string; channelId: number } | null>(null)
  const presenceSubscriptionRef = useRef<{ id: string; channelId: number } | null>(null)
  const onMessageRef = useRef(onMessage)
  const onPresenceRef = useRef(onPresence)
  const channelIdRef = useRef(channelId)
  channelIdRef.current = channelId
  onMessageRef.current = onMessage
  onPresenceRef.current = onPresence
  const [error, setError] = useState<string | null>(null)
  const [connected, setConnected] = useState(false)

  const unsubscribeCurrent = useCallback((client: Client) => {
    if (messageSubscriptionRef.current) {
      try {
        client.unsubscribe(messageSubscriptionRef.current.id)
      } catch (e) {
        console.error('[WS] unsubscribe error', e)
      }
      messageSubscriptionRef.current = null
    }

    if (presenceSubscriptionRef.current) {
      try {
        client.unsubscribe(presenceSubscriptionRef.current.id)
      } catch (e) {
        console.error('[WS] presence unsubscribe error', e)
      }
      presenceSubscriptionRef.current = null
    }
  }, [])

  const doSubscribe = useCallback((client: Client, cid: number) => {
    if (
      messageSubscriptionRef.current?.channelId === cid &&
      presenceSubscriptionRef.current?.channelId === cid
    ) {
      return
    }

    unsubscribeCurrent(client)

    const messageSub = client.subscribe(`/topic/channel/${cid}`, (message: IMessage) => {
      try {
        const data: ChatMessageResponse = JSON.parse(message.body)
        onMessageRef.current(data)
      } catch (e) {
        console.error('[WS] parse message error', e)
      }
    })
    messageSubscriptionRef.current = { id: messageSub.id, channelId: cid }

    const presenceSub = client.subscribe(`/topic/channel/${cid}/presence`, (message: IMessage) => {
      try {
        const data: ChannelPresenceResponse = JSON.parse(message.body)
        onPresenceRef.current?.(data)
      } catch (e) {
        console.error('[WS] parse presence error', e)
      }
    })
    presenceSubscriptionRef.current = { id: presenceSub.id, channelId: cid }
  }, [unsubscribeCurrent])

  useEffect(() => {
    if (!enabled) return

    const token = getStudentToken()
    if (!token) return

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws/community'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        clientRef.current = client
        setConnected(true)
        setError(null)
        const cid = channelIdRef.current
        if (cid !== null) {
          doSubscribe(client, cid)
        }
      },
      onDisconnect: () => {
        messageSubscriptionRef.current = null
        presenceSubscriptionRef.current = null
        setConnected(false)
      },
      onStompError: (frame) => {
        console.error('[STOMP Error]', frame)
        setError('Ket noi that bai')
        setConnected(false)
      },
    })

    clientRef.current = client
    client.activate()

    return () => {
      client.deactivate()
      clientRef.current = null
      messageSubscriptionRef.current = null
      presenceSubscriptionRef.current = null
      setConnected(false)
    }
  }, [doSubscribe, enabled])

  useEffect(() => {
    if (!clientRef.current || !clientRef.current.connected || channelId === null) return
    doSubscribe(clientRef.current, channelId)
  }, [channelId, doSubscribe])

  const sendMessage = useCallback((channelId: number, content: string) => {
    const client = clientRef.current
    if (!client || !client.connected) {
      console.error('[WS] cannot send - client not connected')
      return false
    }

    try {
      client.publish({
        destination: `/app/chat.send/${channelId}`,
        body: JSON.stringify({ content }),
      })
      return true
    } catch (e) {
      console.error('[WS] publish error', e)
      return false
    }
  }, [])

  return { sendMessage, error, connected }
}
