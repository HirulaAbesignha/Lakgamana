'use client';

import { useState, useRef, useEffect } from 'react';
import { cn } from '../../lib/utils';

const Dropdown = ({ trigger, children, align = 'right', className }) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div className="relative" ref={dropdownRef}>
      <div onClick={() => setIsOpen(!isOpen)}>
        {trigger}
      </div>
      
      {isOpen && (
        <div
          className={cn(
            'absolute z-50 mt-2 w-64 bg-white rounded-lg shadow-lg border border-gray-200 py-2',
            align === 'right' ? 'right-0' : 'left-0',
            className
          )}
        >
          {children}
        </div>
      )}
    </div>
  );
};

const DropdownItem = ({ children, onClick, className, ...props }) => {
  return (
    <button
      className={cn(
        'w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 transition-colors flex items-center',
        className
      )}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
};

const DropdownSeparator = () => {
  return <div className="border-t border-gray-200 my-1" />;
};

const DropdownHeader = ({ children, className }) => {
  return (
    <div className={cn('px-4 py-2 text-xs font-semibold text-gray-500 uppercase tracking-wide', className)}>
      {children}
    </div>
  );
};

export { Dropdown, DropdownItem, DropdownSeparator, DropdownHeader };
