# Community Channel Presence Design

## Goal

Show students who is currently online in the active community channel.

## Scope

- Track online members per chat channel only.
- Use the existing community STOMP connection at `/ws/community`.
- Broadcast presence updates to subscribers of the active channel.
- Do not persist online state in the database.
- Do not expose global community presence.

## Architecture

Backend presence is ephemeral application state. A small tracker records which authenticated student sessions are subscribed to each channel. WebSocket lifecycle events update the tracker on subscribe, unsubscribe, and disconnect.

The backend broadcasts channel presence on `/topic/channel/{channelId}/presence`. The existing chat topic `/topic/channel/{channelId}` remains unchanged.

The web client subscribes to both topics for the selected channel. `OnlineMembers` renders the presence payload instead of the current placeholder.

## Data Shape

```json
{
  "channelId": 1,
  "members": [
    {
      "studentId": 42,
      "studentCode": "22520001",
      "displayName": "Nguyen Van A"
    }
  ]
}
```

## Error Handling

- If the WebSocket is disconnected, the UI shows a short disconnected state and preserves an empty member list.
- If a presence payload cannot be parsed, the client logs it and keeps the last valid state.
- If a user disconnects without an explicit unsubscribe, backend disconnect cleanup removes every channel subscription owned by that STOMP session.

## Testing

- Backend contract test covers subscribe, unsubscribe, duplicate sessions, and disconnect cleanup.
- Frontend tests cover rendering online members and empty/disconnected states.
- Existing community chat tests must continue passing.
