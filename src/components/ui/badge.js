'use client';

import { cn } from '../../lib/utils';

const Badge = ({ 
  children, 
  variant = 'default', 
  size = 'md', 
  className = '' 
}) => {
  const baseClasses = 'inline-flex items-center font-medium rounded-full';
  
  const variants = {
    default: 'bg-gray-100 text-gray-800',
    primary: 'bg-blue-100 text-blue-800',
    secondary: 'bg-green-100 text-green-800',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    error: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800'
  };
  
  const sizes = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-0.5 text-xs',
    lg: 'px-3 py-1 text-sm'
  };
  
  return (
    <span
      className={cn(
        baseClasses,
        variants[variant],
        sizes[size],
        className
      )}
    >
      {children}
    </span>
  );
};

const StatusBadge = ({ status, className = '' }) => {
  const statusConfig = {
    active: { variant: 'success', text: 'Active' },
    inactive: { variant: 'error', text: 'Inactive' },
    pending: { variant: 'warning', text: 'Pending' },
    confirmed: { variant: 'success', text: 'Confirmed' },
    cancelled: { variant: 'error', text: 'Cancelled' },
    completed: { variant: 'success', text: 'Completed' },
    paid: { variant: 'success', text: 'Paid' },
    unpaid: { variant: 'warning', text: 'Unpaid' },
    refunded: { variant: 'info', text: 'Refunded' },
    approved: { variant: 'success', text: 'Approved' },
    suspended: { variant: 'error', text: 'Suspended' }
  };
  
  const config = statusConfig[status] || { variant: 'default', text: status };
  
  return (
    <Badge variant={config.variant} className={className}>
      {config.text}
    </Badge>
  );
};

export { Badge, StatusBadge };
