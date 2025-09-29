'use client';

import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import Button from '../../components/ui/button';
import { StatusBadge } from '../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../components/ui/modal';
import bookingsData from '../../data/bookings.json';
import { formatCurrency, formatDate, formatTime } from '../../lib/utils';

export default function MyTicketsPage() {
  const [bookings, setBookings] = useState([]);
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState(false);

  useEffect(() => {
    // Simulate user bookings (in real app, this would be filtered by user ID)
    setBookings(bookingsData.filter(booking => booking.userId === 'U001'));
  }, []);

  const handleCancelBooking = (booking) => {
    setSelectedBooking(booking);
    setIsCancelModalOpen(true);
  };

  const handleRescheduleBooking = (booking) => {
    setSelectedBooking(booking);
    setIsRescheduleModalOpen(true);
  };

  const confirmCancel = () => {
    if (selectedBooking) {
      setBookings(prev => 
        prev.map(booking => 
          booking.id === selectedBooking.id 
            ? { ...booking, status: 'cancelled' }
            : booking
        )
      );
    }
    setIsCancelModalOpen(false);
    setSelectedBooking(null);
  };

  const downloadTicket = (booking) => {
    // Simulate PDF download
    alert(`Downloading ticket for booking ${booking.id}`);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'confirmed': return 'success';
      case 'cancelled': return 'error';
      case 'pending': return 'warning';
      default: return 'default';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container-custom">
        <div className="max-w-6xl mx-auto">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">My Tickets</h1>
            <p className="text-gray-600">Manage your train bookings and reservations</p>
          </div>

          {bookings.length === 0 ? (
            <Card className="text-center py-12">
              <CardContent>
                <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">No tickets found</h3>
                <p className="text-gray-600 mb-6">You haven&apos;t made any bookings yet.</p>
                <Button onClick={() => window.location.href = '/booking'}>
                  Book Your First Ticket
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-6">
              {bookings.map((booking) => (
                <Card key={booking.id} hover>
                  <CardContent className="p-6">
                    <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                      <div className="flex-1">
                        <div className="flex items-center justify-between mb-4">
                          <div>
                            <h3 className="text-xl font-semibold text-gray-900">
                              {booking.trainName}
                            </h3>
                            <p className="text-gray-600">{booking.route}</p>
                          </div>
                          <StatusBadge status={getStatusColor(booking.status)} />
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                          <div>
                            <p className="text-sm text-gray-500">Booking ID</p>
                            <p className="font-medium">{booking.id}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Date</p>
                            <p className="font-medium">{formatDate(booking.departureDate)}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Time</p>
                            <p className="font-medium">
                              {formatTime(booking.departureTime)} - {formatTime(booking.arrivalTime)}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Seat</p>
                            <p className="font-medium">
                              {booking.seatClass.charAt(0).toUpperCase() + booking.seatClass.slice(1)} - {booking.seatNumber}
                            </p>
                          </div>
                        </div>

                        <div className="flex flex-wrap gap-2 mb-4">
                          {booking.passengers.map((passenger, index) => (
                            <span key={index} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                              {passenger.name}
                            </span>
                          ))}
                        </div>

                        <div className="flex items-center justify-between">
                          <div>
                            <p className="text-sm text-gray-500">Total Amount</p>
                            <p className="text-2xl font-bold text-green-600">
                              {formatCurrency(booking.totalAmount)}
                            </p>
                          </div>
                        </div>
                      </div>

                      <div className="lg:ml-6 mt-4 lg:mt-0">
                        <div className="flex flex-col space-y-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => downloadTicket(booking)}
                            className="w-full lg:w-auto"
                          >
                            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            Download PDF
                          </Button>
                          
                          {booking.status === 'confirmed' && (
                            <>
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleRescheduleBooking(booking)}
                                className="w-full lg:w-auto"
                              >
                                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                Reschedule
                              </Button>
                              
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => handleCancelBooking(booking)}
                                className="w-full lg:w-auto"
                              >
                                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                                Cancel
                              </Button>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      </div>

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
                Are you sure you want to cancel your booking for <strong>{selectedBooking.trainName}</strong>?
              </p>
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-yellow-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-yellow-800">
                      <strong>Refund Policy:</strong> Cancellations made more than 24 hours before departure 
                      are eligible for full refund. Cancellations made within 24 hours will incur a 25% cancellation fee.
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
          <Button variant="danger" onClick={confirmCancel}>
            Cancel Booking
          </Button>
        </ModalFooter>
      </Modal>

      {/* Reschedule Booking Modal */}
      <Modal
        isOpen={isRescheduleModalOpen}
        onClose={() => setIsRescheduleModalOpen(false)}
        title="Reschedule Booking"
        size="lg"
      >
        <ModalBody>
          {selectedBooking && (
            <div>
              <p className="text-gray-600 mb-6">
                Reschedule your booking for <strong>{selectedBooking.trainName}</strong>
              </p>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    New Date
                  </label>
                  <input
                    type="date"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    min={new Date().toISOString().split('T')[0]}
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    New Time
                  </label>
                  <select className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                    <option value="">Select Time</option>
                    <option value="08:00">08:00 AM</option>
                    <option value="10:30">10:30 AM</option>
                    <option value="14:00">02:00 PM</option>
                    <option value="16:30">04:30 PM</option>
                  </select>
                </div>
              </div>
              
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mt-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-blue-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-blue-800">
                      <strong>Reschedule Policy:</strong> You can reschedule your booking up to 2 hours before departure. 
                      Any fare difference will be charged or refunded accordingly.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsRescheduleModalOpen(false)}>
            Cancel
          </Button>
          <Button variant="primary" onClick={() => setIsRescheduleModalOpen(false)}>
            Confirm Reschedule
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}
