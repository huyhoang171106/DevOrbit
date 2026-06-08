import { useEffect, useRef, useCallback } from 'react'
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
  onMessageRef.current = onMessage

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
        if (channelId !== null) {
          doSubscribe(client, channelId)
        }
      },
      onDisconnect: () => {
        subscriptionRef.current = null
      },
      onStompError: () => {},
    })

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
      } catch {}
      subscriptionRef.current = null
    }

    const sub = client.subscribe(`/topic/channel/${cid}`, (message: IMessage) => {
      try {
        const data: ChatMessageResponse = JSON.parse(message.body)
        onMessageRef.current(data)
      } catch {}
    })

    subscriptionRef.current = { id: sub.id, channelId: cid }
  }, [])

  useEffect(() => {
    if (!clientRef.current || !clientRef.current.connected || channelId === null) return
    doSubscribe(clientRef.current, channelId)
  }, [channelId, doSubscribe])

  const sendMessage = useCallback((channelId: number, content: string) => {
    const client = clientRef.current
    if (!client || !client.connected) return false

    client.publish({
      destination: `/app/chat.send/${channelId}`,
      body: JSON.stringify({ content }),
    })
    return true
  }, [])

  return { sendMessage }
}
