'use client';

import { useState, useEffect } from 'react';

export const useAuth = () => {
  const [authState, setAuthState] = useState({
    isLoggedIn: false,
    user: null,
    role: 'user',
    token: null,
    loading: true
  });

  useEffect(() => {
    checkAuthStatus();
    
    // Listen for storage changes
    const handleStorageChange = (e) => {
      if (e.key === 'lak_auth') {
        checkAuthStatus();
      }
    };
    
    window.addEventListener('storage', handleStorageChange);
    
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const checkAuthStatus = async () => {
    try {
      const raw = localStorage.getItem('lak_auth');
      if (raw) {
        const parsed = JSON.parse(raw);
        if (parsed && parsed.isLoggedIn && parsed.token) {
          // Fetch fresh user data from API
          try {
            const response = await fetch('http://localhost:8081/profile', {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${parsed.token}`
              }
            });

            if (response.ok) {
              const result = await response.json();
              const userData = result.data;
              setAuthState({
                isLoggedIn: true,
                user: userData,
                role: userData.role === 'ADMIN' ? 'admin' : 'user',
                token: parsed.token,
                loading: false
              });
            } else {
              // Token is invalid, clear auth
              logout();
            }
          } catch (error) {
            // API call failed, use cached data as fallback
            setAuthState({
              isLoggedIn: true,
              user: parsed.user,
              role: parsed.role || 'user',
              token: parsed.token,
              loading: false
            });
          }
        } else {
          logout();
        }
      } else {
        logout();
      }
    } catch (error) {
      console.error('Error checking auth status:', error);
      logout();
    }
  };

  const login = (authData) => {
    try {
      localStorage.setItem('lak_auth', JSON.stringify(authData));
      
      // Trigger storage event
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'lak_auth',
        newValue: localStorage.getItem('lak_auth')
      }));
      
      checkAuthStatus();
    } catch (error) {
      console.error('Error storing auth data:', error);
    }
  };

  const logout = () => {
    try {
      localStorage.removeItem('lak_auth');
      
      // Trigger storage event
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'lak_auth',
        newValue: null
      }));
      
      setAuthState({
        isLoggedIn: false,
        user: null,
        role: 'user',
        token: null,
        loading: false
      });
    } catch (error) {
      console.error('Error clearing auth data:', error);
    }
  };

  const refreshAuth = () => {
    checkAuthStatus();
  };

  return {
    ...authState,
    login,
    logout,
    refreshAuth
  };
};


