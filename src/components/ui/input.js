'use client';

import { cn } from '../../lib/utils';

const Input = ({ 
  label, 
  error, 
  helperText, 
  className = '', 
  size = 'md',
  ...props 
}) => {
  const baseClasses = 'w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors duration-200 bg-white';
  
  const sizes = {
    sm: 'px-3 py-2 text-sm',
    md: 'px-4 py-3 text-sm',
    lg: 'px-4 py-3 text-base'
  };
  
  const inputClasses = cn(
    baseClasses,
    sizes[size],
    error && 'border-red-500 focus:ring-red-500',
    className
  );
  
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {label}
          {props.required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <input
        className={inputClasses}
        {...props}
      />
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
      {helperText && !error && (
        <p className="mt-1 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
};

const TextArea = ({ 
  label, 
  error, 
  helperText, 
  className = '', 
  rows = 4,
  ...props 
}) => {
  const baseClasses = 'w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors duration-200 bg-white resize-none';
  
  const textareaClasses = cn(
    baseClasses,
    'px-4 py-3 text-sm',
    error && 'border-red-500 focus:ring-red-500',
    className
  );
  
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {label}
          {props.required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <textarea
        className={textareaClasses}
        rows={rows}
        {...props}
      />
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
      {helperText && !error && (
        <p className="mt-1 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
};

const Select = ({ 
  label, 
  error, 
  helperText, 
  options = [],
  placeholder = 'Select an option',
  className = '', 
  ...props 
}) => {
  const baseClasses = 'w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors duration-200 bg-white px-4 py-3 text-sm';
  
  const selectClasses = cn(
    baseClasses,
    error && 'border-red-500 focus:ring-red-500',
    className
  );
  
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {label}
          {props.required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <select
        className={selectClasses}
        {...props}
      >
        <option value="" disabled>{placeholder}</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
      {helperText && !error && (
        <p className="mt-1 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
};

export { Input, TextArea, Select };
