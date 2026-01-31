import { clsx } from 'clsx'
import { Loader2 } from 'lucide-react'

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  fullScreen?: boolean
  className?: string
}

export default function LoadingSpinner({
  size = 'md',
  fullScreen = false,
  className,
}: LoadingSpinnerProps) {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
  }

  if (fullScreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-white/80 backdrop-blur-sm z-50">
        <Loader2
          className={clsx('animate-spin text-primary-600', sizeClasses.lg, className)}
        />
      </div>
    )
  }

  return (
    <div className={clsx('flex items-center justify-center p-4', className)}>
      <Loader2
        className={clsx('animate-spin text-primary-600', sizeClasses[size])}
      />
    </div>
  )
}
