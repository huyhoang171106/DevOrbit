import { Component, type ErrorInfo, type ReactNode } from "react";

interface ErrorRetryProps {
  children: ReactNode;
  fallback?: ReactNode;
  onRetry?: () => void;
  onError?: (error: Error, info: ErrorInfo) => void;
}

interface ErrorRetryState {
  hasError: boolean;
  error: Error | null;
}

export class ErrorRetryBoundary extends Component<ErrorRetryProps, ErrorRetryState> {
  constructor(props: ErrorRetryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorRetryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error("[ErrorRetryBoundary]", error, info.componentStack);
    this.props.onError?.(error, info);
  }

  handleRetry = (): void => {
    this.setState({ hasError: false, error: null });
    this.props.onRetry?.();
  };

  render(): ReactNode {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback;
      return (
        <div className="flex flex-col items-center justify-center rounded-lg border border-red-200 bg-red-50 p-8 text-center dark:border-red-800 dark:bg-red-900/20">
          <svg
            className="mb-3 h-12 w-12 text-red-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            aria-hidden="true"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <h3 className="mb-1 text-lg font-semibold text-red-800 dark:text-red-200">
            Đã xảy ra lỗi
          </h3>
          <p className="mb-4 max-w-md text-sm text-red-600 dark:text-red-300">
            {this.state.error?.message ?? "Không thể tải nội dung. Vui lòng thử lại."}
          </p>
          <button
            onClick={this.handleRetry}
            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
          >
            Thử lại
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

interface ErrorFallbackProps {
  message?: string;
  onRetry?: () => void;
}

export function ErrorFallback({ message, onRetry }: ErrorFallbackProps) {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center">
      <p className="mb-2 text-gray-500 dark:text-gray-400">
        {message ?? "Có lỗi xảy ra khi tải dữ liệu"}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="text-sm font-medium text-blue-600 hover:text-blue-500 dark:text-blue-400"
        >
          Nhấn để thử lại
        </button>
      )}
    </div>
  );
}
