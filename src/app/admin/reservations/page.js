'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card';
import Button from '../../../components/ui/button';
import { Input, Select } from '../../../components/ui/input';
import { StatusBadge } from '../../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../../components/ui/modal';
import { formatCurrency, formatDate, formatTime } from '../../../lib/utils';

export default function AdminReservationsPage() {
  const router = useRouter();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [filterStatus, setFilterStatus] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  const statusOptions = [
    { value: 'all', label: 'All Status' },
    { value: 'CONFIRMED', label: 'Confirmed' },
    { value: 'CANCELLED', label: 'Cancelled' },
    { value: 'PENDING', label: 'Pending' }
  ];

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      
      if (!authData.token) {
        router.push('/login');
        return;
      }

      const response = await fetch('http://localhost:8081/bookings/admin', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('lak_auth');
          router.push('/login');
          return;
        }
        throw new Error('Failed to fetch bookings');
      }

      const result = await response.json();
      const list = (result?.data && (result.data.content ?? result.data)) || [];
      setBookings(Array.isArray(list) ? list : []);
    } catch (error) {
      console.error('Error fetching bookings:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredBookings = (bookings || []).filter(booking => {
    const matchesStatus = filterStatus === 'all' || booking.status === filterStatus;
    const matchesSearch = 
      booking.user?.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.user?.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.train?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.bookingId?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  const openDetailsModal = (booking) => {
    setSelectedBooking(booking);
    setIsDetailsModalOpen(true);
  };

  const openCancelModal = (booking) => {
    setSelectedBooking(booking);
    setIsCancelModalOpen(true);
  };

  const handleCancelBooking = async () => {
    if (selectedBooking) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch(`http://localhost:8081/bookings/${selectedBooking.bookingId}/cancel?reason=Cancelled by admin`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          }
        });

        if (!response.ok) {
          throw new Error('Failed to cancel booking');
        }

        // Refresh bookings list
        await fetchBookings();
      } catch (error) {
        console.error('Error cancelling booking:', error);
        setError(error.message);
      }
    }
    setIsCancelModalOpen(false);
    setSelectedBooking(null);
  };

  const handleConfirmBooking = async (booking) => {
    if (confirm(`Are you sure you want to confirm booking ${booking.bookingId}?`)) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch(`http://localhost:8081/bookings/${booking.id}/confirm`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          }
        });

        if (!response.ok) {
          throw new Error('Failed to confirm booking');
        }

        // Refresh bookings list
        await fetchBookings();
        alert('Booking confirmed successfully!');
      } catch (error) {
        console.error('Error confirming booking:', error);
        setError(error.message);
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'success';
      case 'CANCELLED': return 'error';
      case 'PENDING': return 'warning';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading bookings...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-64">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md">
          <div className="flex">
            <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <div>
              <h3 className="text-sm font-medium text-red-800">Error loading bookings</h3>
              <p className="text-sm text-red-700 mt-1">{error}</p>
              <button 
                onClick={fetchBookings}
                className="mt-2 text-sm text-red-600 hover:text-red-500 underline"
              >
                Try again
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Reservations Management</h1>
        <p className="text-gray-600 mt-2">Manage all train reservations and bookings</p>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <Input
                label="Search"
                placeholder="Search by user name, train name, or booking ID..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div className="md:w-48">
              <Select
                label="Status"
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                options={statusOptions}
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Reservations Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Reservations ({filteredBookings.length})</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Booking ID</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">User</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Train</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Route</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Date & Time</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Passengers</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Amount</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Status</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredBookings.map((booking) => (
                  <tr key={booking.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4">
                      <p className="font-medium text-gray-900">{booking.bookingId}</p>
                      <p className="text-sm text-gray-500">{formatDate(booking.bookingDate)}</p>
                    </td>
                    <td className="py-3 px-4">
                      <div>
                        <p className="font-medium text-gray-900">{booking.user?.firstName} {booking.user?.lastName}</p>
                        <p className="text-sm text-gray-500">ID: {booking.user?.userId}</p>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <div>
                        <p className="font-medium text-gray-900">{booking.train?.name}</p>
                        <p className="text-sm text-gray-500">Train ID: {booking.train?.trainId}</p>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{booking.train?.route}</p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{formatDate(booking.departureDate)}</p>
                      <p className="text-sm text-gray-500">
                        {formatTime(booking.departureTime)} - {formatTime(booking.arrivalTime)}
                      </p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{booking.passengers?.length || 0} passenger(s)</p>
                      <p className="text-sm text-gray-500">
                        {booking.seatClass?.charAt(0).toUpperCase() + booking.seatClass?.slice(1)} - {booking.seatNumber}
                      </p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="font-medium text-gray-900">{formatCurrency(booking.totalAmount)}</p>
                      <p className="text-sm text-gray-500">Paid</p>
                    </td>
                    <td className="py-3 px-4">
                      <StatusBadge status={getStatusColor(booking.status)} />
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex space-x-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => openDetailsModal(booking)}
                        >
                          View
                        </Button>
                        {booking.status === 'PENDING' && (
                          <Button
                            variant="success"
                            size="sm"
                            onClick={() => handleConfirmBooking(booking)}
                          >
                            Done
                          </Button>
                        )}
                        {booking.status === 'CONFIRMED' && (
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => openCancelModal(booking)}
                          >
                            Cancel
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      {/* Booking Details Modal */}
      <Modal
        isOpen={isDetailsModalOpen}
        onClose={() => setIsDetailsModalOpen(false)}
        title="Booking Details"
        size="lg"
      >
        <ModalBody>
          {selectedBooking && (
            <div className="space-y-6">
              {/* Booking Info */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Booking Information</h3>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Booking ID:</span>
                      <span className="font-medium">{selectedBooking.bookingId}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Booking Date:</span>
                      <span className="font-medium">{formatDate(selectedBooking.bookingDate)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Status:</span>
                      <StatusBadge status={getStatusColor(selectedBooking.status)} />
                    </div>
                  </div>
                </div>
                
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Train Information</h3>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Train:</span>
                      <span className="font-medium">{selectedBooking.train?.name}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Route:</span>
                      <span className="font-medium">{selectedBooking.train?.route}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Date:</span>
                      <span className="font-medium">{formatDate(selectedBooking.departureDate)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Time:</span>
                      <span className="font-medium">
                        {formatTime(selectedBooking.departureTime)} - {formatTime(selectedBooking.arrivalTime)}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              {/* User Info */}
              <div>
                <h3 className="font-semibold text-gray-900 mb-2">User Information</h3>
                <div className="bg-gray-50 rounded-lg p-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div>
                      <p className="text-gray-600">Name: <span className="font-medium">{selectedBooking.user?.firstName} {selectedBooking.user?.lastName}</span></p>
                      <p className="text-gray-600">User ID: <span className="font-medium">{selectedBooking.user?.userId}</span></p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Passengers */}
              <div>
                <h3 className="font-semibold text-gray-900 mb-2">Passenger Details</h3>
                <div className="space-y-3">
                  {selectedBooking.passengers?.map((passenger, index) => (
                    <div key={index} className="bg-gray-50 rounded-lg p-4">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                        <div>
                          <p className="text-gray-600">Name: <span className="font-medium">{passenger.name}</span></p>
                          <p className="text-gray-600">Age: <span className="font-medium">{passenger.age}</span></p>
                        </div>
                        <div>
                          <p className="text-gray-600">Gender: <span className="font-medium capitalize">{passenger.gender}</span></p>
                          <p className="text-gray-600">ID: <span className="font-medium">{passenger.idNumber}</span></p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Payment Info */}
              <div>
                <h3 className="font-semibold text-gray-900 mb-2">Payment Information</h3>
                <div className="bg-gray-50 rounded-lg p-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div>
                      <p className="text-gray-600">Total Amount: <span className="font-medium">{formatCurrency(selectedBooking.totalAmount)}</span></p>
                      <p className="text-gray-600">Payment Method: <span className="font-medium capitalize">Credit Card</span></p>
                    </div>
                    <div>
                      <p className="text-gray-600">Payment Status: <StatusBadge status="success" /></p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsDetailsModalOpen(false)}>
            Close
          </Button>
        </ModalFooter>
      </Modal>

      {/* Cancel Booking Modal */}
      <Modal
        isOpen={isCancelModalOpen}
        onClose={() => setIsCancelModalOpen(false)}
        title="Cancel Booking"
        size="md"
      >
        <ModalBody>
          {selectedBooking && (
            <div>
              <p className="text-gray-600 mb-4">
                Are you sure you want to cancel booking <strong>{selectedBooking.bookingId}</strong> for <strong>{selectedBooking.user?.firstName} {selectedBooking.user?.lastName}</strong>?
              </p>
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-yellow-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-yellow-800">
                      <strong>Note:</strong> This action will cancel the booking and may trigger a refund process. 
                      The user will be notified via email.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsCancelModalOpen(false)}>
            Keep Booking
          </Button>
          <Button variant="danger" onClick={handleCancelBooking}>
            Cancel Booking
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}
