import { useEffect, useRef, useCallback, useState } from 'react'
import { Client, type IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getStudentToken } from '../lib/auth'
import type { ChatMessageResponse } from '../types/api'

type UseCommunitySocketOptions = {
  channelId: number | null
  enabled: boolean
  onMessage: (msg: ChatMessageResponse) => void
}

export function useCommunitySocket({ channelId, enabled, onMessage }: UseCommunitySocketOptions) {
  const clientRef = useRef<Client | null>(null)
  const subscriptionRef = useRef<{ id: string; channelId: number } | null>(null)
  const onMessageRef = useRef(onMessage)
  const channelIdRef = useRef(channelId)
  channelIdRef.current = channelId
  onMessageRef.current = onMessage
  const [error, setError] = useState<string | null>(null)

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
        setError(null)
        const cid = channelIdRef.current
        if (cid !== null) {
          doSubscribe(client, cid)
        }
      },
      onDisconnect: () => {
        subscriptionRef.current = null
      },
      onStompError: (frame) => {
        console.error('[STOMP Error]', frame)
        setError('Kết nối thất bại')
      },
    })

    clientRef.current = client
    client.activate()

    return () => {
      client.deactivate()
      clientRef.current = null
      subscriptionRef.current = null
    }
  }, [enabled])

  const doSubscribe = useCallback((client: Client, cid: number) => {
    if (subscriptionRef.current) {
      const prev = subscriptionRef.current
      if (prev.channelId === cid) return
      try {
        client.unsubscribe(prev.id)
      } catch (e) {
        console.error('[WS] unsubscribe error', e)
      }
      subscriptionRef.current = null
    }

    const sub = client.subscribe(`/topic/channel/${cid}`, (message: IMessage) => {
      try {
        const data: ChatMessageResponse = JSON.parse(message.body)
        onMessageRef.current(data)
      } catch (e) {
        console.error('[WS] parse message error', e)
      }
    })

    subscriptionRef.current = { id: sub.id, channelId: cid }
  }, [])

  useEffect(() => {
    if (!clientRef.current || !clientRef.current.connected || channelId === null) return
    doSubscribe(clientRef.current, channelId)
  }, [channelId, doSubscribe])

  const sendMessage = useCallback((channelId: number, content: string) => {
    const client = clientRef.current
    if (!client || !client.connected) {
      console.error('[WS] cannot send — client not connected')
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

  return { sendMessage, error }
}
