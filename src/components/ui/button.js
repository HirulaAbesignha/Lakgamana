'use client';

import { cn } from '../../lib/utils';

const Button = ({ 
  children, 
  variant = 'primary', 
  size = 'md', 
  className = '', 
  disabled = false,
  loading = false,
  ...props 
}) => {
  const baseClasses = 'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-0.5 active:translate-y-0';
  
  const variants = {
    primary: 'text-white shadow-md hover:shadow-lg focus:ring-opacity-50',
    secondary: 'text-white shadow-md hover:shadow-lg focus:ring-opacity-50',
    outline: 'bg-white hover:bg-gray-50 focus:ring-opacity-50',
    ghost: 'bg-transparent hover:bg-white/50 focus:ring-opacity-50',
    danger: 'text-white shadow-md hover:shadow-lg focus:ring-opacity-50'
  };
  
  const sizes = {
    sm: 'px-4 py-2 text-sm',
    md: 'px-6 py-3 text-sm',
    lg: 'px-8 py-4 text-base',
    xl: 'px-10 py-5 text-lg'
  };
  
  // Get variant-specific styles
  const getVariantStyles = (variant) => {
    switch (variant) {
      case 'primary':
        return {
          background: 'var(--gradient-primary)',
          border: '1px solid var(--primary-dark)',
          color: 'white',
          focusRingColor: 'var(--primary)'
        };
      case 'secondary':
        return {
          background: 'var(--gradient-secondary)',
          border: '1px solid var(--secondary-dark)',
          color: 'white',
          focusRingColor: 'var(--secondary)'
        };
      case 'outline':
        return {
          background: 'var(--surface)',
          border: '2px solid var(--border)',
          color: 'var(--text-primary)',
          focusRingColor: 'var(--primary)',
          hoverBorderColor: 'var(--primary)',
          hoverColor: 'var(--primary)'
        };
      case 'ghost':
        return {
          background: 'transparent',
          border: 'none',
          color: 'var(--text-secondary)',
          focusRingColor: 'var(--primary)',
          hoverColor: 'var(--primary)',
          hoverBackground: 'var(--surface-dark)'
        };
      case 'danger':
        return {
          background: 'var(--error)',
          border: '1px solid var(--error)',
          color: 'white',
          focusRingColor: 'var(--error)'
        };
      default:
        return {};
    }
  };
  
  const variantStyles = getVariantStyles(variant);
  
  return (
    <button
      className={cn(
        baseClasses,
        variants[variant],
        sizes[size],
        className
      )}
      style={{
        ...variantStyles,
        boxShadow: variantStyles.shadow || 'var(--shadow-md)'
      }}
      disabled={disabled || loading}
      {...props}
    >
      {loading && (
        <svg className="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      )}
      {children}
    </button>
  );
};

export default Button;
