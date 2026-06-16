// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, render, screen } from '@testing-library/react'
import { afterEach, describe, expect, test } from 'vitest'
import { OnlineMembers } from './CommunityPage'
import type { OnlineMemberResponse } from '../../types/api'

afterEach(() => {
  cleanup()
})

describe('OnlineMembers', () => {
  test('renders online members for the active channel', () => {
    const members: OnlineMemberResponse[] = [
      { studentId: 1, studentCode: '24520554', displayName: 'Bao Nguyen', avatar: null },
      { studentId: 2, studentCode: '24520001', displayName: 'An Tran', avatar: null },
    ]

    render(<OnlineMembers members={members} connected={true} />)

    expect(screen.getByText('Bao Nguyen')).toBeInTheDocument()
    expect(screen.getByText('24520554')).toBeInTheDocument()
    expect(screen.getByText('An Tran')).toBeInTheDocument()
    expect(screen.getByText('2 online')).toBeInTheDocument()
  })

  test('renders empty state when nobody is online in the channel', () => {
    render(<OnlineMembers members={[]} connected={true} />)

    expect(screen.getByText('Chưa có ai online trong kênh này')).toBeInTheDocument()
  })

  test('renders disconnected state when socket is not connected', () => {
    render(<OnlineMembers members={[]} connected={false} />)

    expect(screen.getByText('Đang kết nối lại...')).toBeInTheDocument()
  })

  test('shows "(Bạn)" label next to current user', () => {
    const members: OnlineMemberResponse[] = [
      { studentId: 1, studentCode: '24520554', displayName: 'Bao Nguyen', avatar: null },
      { studentId: 2, studentCode: '24520001', displayName: 'An Tran', avatar: null },
    ]

    render(<OnlineMembers members={members} connected={true} currentUserId={1} />)

    expect(screen.getByText('(Bạn)')).toBeInTheDocument()
    expect(screen.getByText('An Tran')).toBeInTheDocument()
    expect(screen.queryByText('(Bạn)', { selector: 'span' })).toBeInTheDocument()
  })
})
