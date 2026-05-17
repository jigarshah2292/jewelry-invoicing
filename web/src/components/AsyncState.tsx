import type { ReactNode } from 'react'
import { ApiError } from '../api/client'

interface AsyncStateProps {
  loading: boolean
  error: unknown
  empty: boolean
  emptyMessage: string
  children: ReactNode
}

export function AsyncState({
  loading,
  error,
  empty,
  emptyMessage,
  children,
}: AsyncStateProps) {
  if (loading) {
    return <p className="state-message">Loading…</p>
  }

  if (error) {
    const message =
      error instanceof ApiError
        ? error.message
        : error instanceof Error
          ? error.message
          : 'Something went wrong'
    return <p className="state-message state-message--error">{message}</p>
  }

  if (empty) {
    return <p className="state-message">{emptyMessage}</p>
  }

  return <>{children}</>
}
