'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import Button from './ui/button';
import { Dropdown, DropdownItem, DropdownSeparator, DropdownHeader } from './ui/dropdown';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState('user');
  const [isScrolled, setIsScrolled] = useState(false);
  const [userDetails, setUserDetails] = useState({
    name: 'John Doe',
    email: 'john.doe@example.com',
    avatar: null
  });
  const router = useRouter();

  const navigation = [
    { name: 'Home', href: '/' },
    { name: 'Book Tickets', href: '/booking' },
    { name: 'My Tickets', href: '/tickets' }
  ];

  const adminNavigation = [
    { name: 'Dashboard', href: '/admin' },
    { name: 'Trains', href: '/admin/trains' },
    { name: 'Reservations', href: '/admin/reservations' },
    { name: 'Users', href: '/admin/users' },
    { name: 'Routes', href: '/admin/routes' },
    { name: 'Feedback', href: '/admin/feedback' },
    { name: 'Payments', href: '/admin/payments' }
  ];

  const handleLogout = () => {
    try { 
      localStorage.removeItem('lak_auth');
      // Trigger storage event to update navbar
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'lak_auth',
        newValue: null
      }));
    } catch {}
    setIsLoggedIn(false);
    setUserRole('user');
    setUserDetails({
      name: 'John Doe',
      email: 'john.doe@example.com',
      avatar: null
    });
    router.push('/');
  };

  const handleLogin = (role = 'user') => {
    setIsLoggedIn(true);
    setUserRole(role);
    if (role === 'admin') {
      setUserDetails({
        name: 'Admin User',
        email: 'admin@lakgamana.com',
        avatar: null
      });
    } else {
      setUserDetails({
        name: 'John Doe',
        email: 'user@lakgamana.com',
        avatar: null
      });
    }
    try {
      const payload = {
        isLoggedIn: true,
        role,
        user: role === 'admin'
          ? { name: 'Admin User', email: 'admin@lakgamana.com', avatar: null }
          : { name: 'John Doe', email: 'user@lakgamana.com', avatar: null }
      };
      localStorage.setItem('lak_auth', JSON.stringify(payload));
    } catch {}
  };

  const getInitials = (name) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  useEffect(() => {
    checkAuthStatus();
    
    // Listen for storage changes (when user logs in/out in another tab)
    const handleStorageChange = (e) => {
      if (e.key === 'lak_auth') {
        checkAuthStatus();
      }
    };
    
    // Listen for scroll events
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      setIsScrolled(scrollTop > 20);
    };
    
    window.addEventListener('storage', handleStorageChange);
    window.addEventListener('scroll', handleScroll);
    
    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('scroll', handleScroll);
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
              setIsLoggedIn(true);
              setUserRole(userData.role === 'ADMIN' ? 'admin' : 'user');
              setUserDetails({ 
                name: `${userData.firstName} ${userData.lastName}`, 
                email: userData.email, 
                avatar: null 
              });
            } else {
              // Token is invalid, clear auth
              localStorage.removeItem('lak_auth');
              setIsLoggedIn(false);
              setUserRole('user');
              setUserDetails({
                name: 'John Doe',
                email: 'john.doe@example.com',
                avatar: null
              });
            }
          } catch (error) {
            // API call failed, use cached data as fallback
            setIsLoggedIn(true);
            setUserRole(parsed.role === 'admin' ? 'admin' : 'user');
            if (parsed.user && parsed.user.name && parsed.user.email) {
              setUserDetails({ name: parsed.user.name, email: parsed.user.email, avatar: parsed.user.avatar ?? null });
            }
          }
        } else {
          setIsLoggedIn(false);
          setUserRole('user');
          setUserDetails({
            name: 'John Doe',
            email: 'john.doe@example.com',
            avatar: null
          });
        }
      } else {
        setIsLoggedIn(false);
        setUserRole('user');
        setUserDetails({
          name: 'John Doe',
          email: 'john.doe@example.com',
          avatar: null
        });
      }
    } catch (error) {
      console.error('Error checking auth status:', error);
      setIsLoggedIn(false);
      setUserRole('user');
      setUserDetails({
        name: 'John Doe',
        email: 'john.doe@example.com',
        avatar: null
      });
    }
  };

  return (
    <nav className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
      isScrolled 
        ? 'glass backdrop-blur-md shadow-lg border-b border-white/20' 
        : 'bg-white/90 backdrop-blur-sm shadow-sm border-b border-gray-200/50'
    }`}>
      <div className="container-custom">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 rounded-xl flex items-center justify-center transition-all duration-300 group-hover:scale-110 group-hover:rotate-3"
                 style={{ background: 'var(--gradient-primary)' }}>
              <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z" />
              </svg>
            </div>
            <span className="text-xl font-bold transition-colors duration-300"
                  style={{ color: 'var(--text-primary)' }}>
              Lakgamana
            </span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            {navigation.map((item, index) => (
              <Link
                key={item.name}
                href={item.href}
                className="relative font-medium transition-all duration-300 hover:scale-105 group animate-fade-in"
                style={{ 
                  color: 'var(--text-secondary)',
                  animationDelay: `${index * 0.1}s`
                }}
              >
                <span className="group-hover:text-green-600 transition-colors duration-300">
                  {item.name}
                </span>
                <div className="absolute -bottom-1 left-0 w-0 h-0.5 bg-gradient-to-r from-green-600 to-yellow-500 transition-all duration-300 group-hover:w-full"></div>
              </Link>
            ))}
            {isLoggedIn && userRole === 'admin' && (
              <Dropdown
                trigger={
                  <button className="font-medium flex items-center transition-all duration-300 hover:scale-105 group animate-fade-in animate-stagger-3"
                          style={{ color: 'var(--text-secondary)' }}>
                    Dashboard
                    <svg className="w-4 h-4 ml-1 transition-transform duration-300 group-hover:rotate-180" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                }
              >
                {adminNavigation.map((item) => (
                  <DropdownItem
                    key={item.name}
                    onClick={() => {
                      router.push(item.href);
                    }}
                  >
                    {item.name}
                  </DropdownItem>
                ))}
              </Dropdown>
            )}
          </div>

          {/* Desktop Actions */}
          <div className="hidden md:flex items-center space-x-4">
            {isLoggedIn ? (
              <Dropdown
                trigger={
                  <button className="flex items-center space-x-3 hover:bg-white/50 rounded-xl p-2 transition-all duration-300 hover:scale-105 group animate-fade-in animate-stagger-4">
                    <div className="w-9 h-9 rounded-xl flex items-center justify-center shadow-lg transition-all duration-300 group-hover:shadow-xl group-hover:scale-110"
                         style={{ background: 'var(--gradient-secondary)' }}>
                      <span className="text-sm font-bold text-white">
                        {getInitials(userDetails.name)}
                      </span>
                    </div>
                    <div className="text-left">
                      <p className="text-sm font-medium transition-colors duration-300"
                         style={{ color: 'var(--text-primary)' }}>
                        {userDetails.name}
                      </p>
                      <p className="text-xs transition-colors duration-300"
                         style={{ color: 'var(--text-muted)' }}>
                        {userRole === 'admin' ? 'Administrator' : 'User'}
                      </p>
                    </div>
                    <svg className="w-4 h-4 transition-all duration-300 group-hover:rotate-180"
                         style={{ color: 'var(--text-muted)' }}
                         fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                }
              >
                <div className="px-4 py-3 border-b border-gray-200">
                  <p className="text-sm font-medium text-gray-900">{userDetails.name}</p>
                  <p className="text-xs text-gray-500">{userDetails.email}</p>
                  <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mt-1">
                    {userRole === 'admin' ? 'Administrator' : 'User'}
                  </span>
                </div>
                
                <DropdownItem onClick={() => router.push('/profile')}>
                  <svg className="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  Profile Settings
                </DropdownItem>
                
                <DropdownItem onClick={() => router.push('/tickets')}>
                  <svg className="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />
                  </svg>
                  My Tickets
                </DropdownItem>
                
                {userRole === 'admin' && (
                  <>
                    <DropdownSeparator />
                    <DropdownHeader>Admin Panel</DropdownHeader>
                    <DropdownItem onClick={() => router.push('/admin')}>
                      <svg className="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                      </svg>
                      Dashboard
                    </DropdownItem>
                  </>
                )}
                
                <DropdownSeparator />
                <DropdownItem onClick={handleLogout} className="text-red-600 hover:bg-red-50">
                  <svg className="w-4 h-4 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Sign Out
                </DropdownItem>
              </Dropdown>
            ) : (
              <div className="flex items-center space-x-3 animate-fade-in animate-stagger-4">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => router.push('/login')}
                  className="hover:scale-105 transition-transform duration-300"
                >
                  Login
                </Button>
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => router.push('/register')}
                  className="hover:scale-105 transition-transform duration-300"
                >
                  Sign Up
                </Button>
              </div>
            )}
            <Button
              variant="primary"
              size="sm"
              onClick={() => router.push('/booking')}
              className="hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-5"
            >
              Book Now
            </Button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="p-2 rounded-xl transition-all duration-300 hover:bg-white/50 hover:scale-110"
              style={{ color: 'var(--text-primary)' }}
            >
              <svg className="w-6 h-6 transition-transform duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {isMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="md:hidden border-t border-gray-200/50 py-6">
            <div className="space-y-4">
              {navigation.map((item, index) => (
                <Link
                  key={item.name}
                  href={item.href}
                  className="block font-medium transition-all duration-300 hover:scale-105 hover:translate-x-2 animate-fade-in"
                  style={{ 
                    color: 'var(--text-secondary)',
                    animationDelay: `${index * 0.1}s`
                  }}
                  onClick={() => setIsMenuOpen(false)}
                >
                  <span className="hover:text-green-600 transition-colors duration-300">
                    {item.name}
                  </span>
                </Link>
              ))}
              
              {isLoggedIn && userRole === 'admin' && (
                <>
                  <div className="border-t border-gray-200/50 my-4"></div>
                  <div className="px-2">
                    <p className="text-xs font-semibold uppercase tracking-wide animate-fade-in animate-stagger-4"
                       style={{ color: 'var(--text-muted)' }}>
                      Admin Panel
                    </p>
                  </div>
                  {adminNavigation.map((item, index) => (
                    <Link
                      key={item.name}
                      href={item.href}
                      className="block font-medium transition-all duration-300 hover:scale-105 hover:translate-x-2 pl-4 animate-fade-in"
                      style={{ 
                        color: 'var(--text-secondary)',
                        animationDelay: `${(index + 3) * 0.1}s`
                      }}
                      onClick={() => setIsMenuOpen(false)}
                    >
                      <span className="hover:text-green-600 transition-colors duration-300">
                        {item.name}
                      </span>
                    </Link>
                  ))}
                </>
              )}
              
              <div className="flex flex-col space-y-3 pt-6 border-t border-gray-200/50">
                {isLoggedIn ? (
                  <>
                    <div className="flex items-center space-x-3 py-4 px-3 rounded-xl animate-fade-in animate-stagger-5"
                         style={{ background: 'var(--surface-dark)' }}>
                      <div className="w-12 h-12 rounded-xl flex items-center justify-center shadow-lg animate-scale-in"
                           style={{ background: 'var(--gradient-secondary)' }}>
                        <span className="text-sm font-bold text-white">
                          {getInitials(userDetails.name)}
                        </span>
                      </div>
                      <div className="flex-1">
                        <p className="text-sm font-medium" style={{ color: 'var(--text-primary)' }}>
                          {userDetails.name}
                        </p>
                        <p className="text-xs" style={{ color: 'var(--text-muted)' }}>
                          {userDetails.email}
                        </p>
                        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium mt-1 badge-success">
                          {userRole === 'admin' ? 'Administrator' : 'User'}
                        </span>
                      </div>
                    </div>
                    <div className="space-y-3">
                      <Button
                        variant="outline"
                        size="sm"
                        className="w-full justify-start hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-6"
                        onClick={() => {
                          router.push('/profile');
                          setIsMenuOpen(false);
                        }}
                      >
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        Profile Settings
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        className="w-full justify-start hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-7"
                        onClick={() => {
                          router.push('/tickets');
                          setIsMenuOpen(false);
                        }}
                      >
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />
                        </svg>
                        My Tickets
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        className="w-full justify-start hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-8"
                        style={{ color: 'var(--error)' }}
                        onClick={() => {
                          handleLogout();
                          setIsMenuOpen(false);
                        }}
                      >
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                        </svg>
                        Sign Out
                      </Button>
                    </div>
                  </>
                ) : (
                  <div className="space-y-3">
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-6"
                      onClick={() => {
                        router.push('/login');
                        setIsMenuOpen(false);
                      }}
                    >
                      Login
                    </Button>
                    <Button
                      variant="primary"
                      size="sm"
                      className="w-full hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-7"
                      onClick={() => {
                        router.push('/register');
                        setIsMenuOpen(false);
                      }}
                    >
                      Sign Up
                    </Button>
                  </div>
                )}
                <Button
                  variant="secondary"
                  size="sm"
                  className="w-full hover:scale-105 transition-transform duration-300 animate-fade-in animate-stagger-8"
                  onClick={() => {
                    router.push('/booking');
                    setIsMenuOpen(false);
                  }}
                >
                  Book Now
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export { Navbar };