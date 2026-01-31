import { cn } from '@/utils';

interface ProgressBarProps {
  value: number;
  max: number;
  className?: string;
  showLabel?: boolean;
  color?: 'primary' | 'success' | 'danger';
}

export default function ProgressBar({
  value,
  max,
  className,
  showLabel = false,
  color = 'primary',
}: ProgressBarProps) {
  const percentage = Math.min((value / max) * 100, 100);

  const colorClasses = {
    primary: 'bg-primary-500',
    success: 'bg-success-500',
    danger: 'bg-danger-500',
  };

  return (
    <div className={cn('w-full', className)}>
      <div className="relative h-2 bg-gray-200 dark:bg-dark-border rounded-full overflow-hidden">
        <div
          className={cn('h-full rounded-full transition-all duration-500', colorClasses[color])}
          style={{ width: `${percentage}%` }}
        />
      </div>
      {showLabel && (
        <div className="flex justify-between mt-1 text-sm text-gray-500 dark:text-dark-text-secondary">
          <span>{value}</span>
          <span>{max}</span>
        </div>
      )}
    </div>
  );
}
