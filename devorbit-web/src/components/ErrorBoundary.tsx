import { Component, ErrorInfo, ReactNode } from 'react'
import { ArrowClockwise, WarningCircle } from '@phosphor-icons/react'

interface Props {
    children: ReactNode
    fallback?: ReactNode
}

interface State {
    hasError: boolean
    error: Error | null
}

export class ErrorBoundary extends Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = { hasError: false, error: null }
    }

    static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error }
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error('[ErrorBoundary] Caught error:', error, errorInfo)
    }

    render() {
        if (this.state.hasError) {
            if (this.props.fallback) return this.props.fallback
            return (
                <div className="flex flex-col items-center justify-center min-h-[40vh] px-10">
                    <div className="orbit-card p-12 md:p-16 max-w-md text-center">
                        <div className="h-16 w-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center mx-auto mb-8">
                            <WarningCircle className="h-8 w-8 text-rose-500" weight="duotone" />
                        </div>
                        <h2 className="heading-4 mb-4 text-orbit-text">Đã xảy ra lỗi</h2>
                        <p className="body-md text-[14px] mb-8">{this.state.error?.message || 'Vui lòng thử lại.'}</p>
                        <button
                            onClick={() => this.setState({ hasError: false, error: null })}
                            className="btn-primary"
                        >
                            <ArrowClockwise className="h-4 w-4" weight="bold" />
                            Thử lại
                        </button>
                    </div>
                </div>
            )
        }
        return this.props.children
    }
}
