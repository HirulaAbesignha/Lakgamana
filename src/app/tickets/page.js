'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import Button from '../../components/ui/button';
import { StatusBadge } from '../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../components/ui/modal';
import { formatCurrency, formatDate, formatTime } from '../../lib/utils';

export default function MyTicketsPage() {
  const router = useRouter();
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isFeedbackModalOpen, setIsFeedbackModalOpen] = useState(false);
  const [isRefundModalOpen, setIsRefundModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchMyTickets();
  }, []);

  const fetchMyTickets = async () => {
    try {
      setLoading(true);
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      
      if (!authData.token) {
        router.push('/login');
        return;
      }

      const response = await fetch('http://localhost:8081/bookings/user', {
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
      const tickets = Array.isArray(result.data) ? result.data : [];
      
      // Sort tickets by creation date (newest first)
      const sortedTickets = tickets.sort((a, b) => {
        const dateA = new Date(a.bookingDate || a.createdAt || 0);
        const dateB = new Date(b.bookingDate || b.createdAt || 0);
        return dateB - dateA; // Newest first
      });
      
      setTickets(sortedTickets);
    } catch (error) {
      console.error('Error fetching bookings:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelTicket = (ticket) => {
    setSelectedTicket(ticket);
    setIsCancelModalOpen(true);
  };

  const handleRescheduleTicket = (ticket) => {
    setSelectedTicket(ticket);
    setIsRescheduleModalOpen(true);
  };

  const handleEditTicket = (ticket) => {
    setSelectedTicket(ticket);
    setIsEditModalOpen(true);
  };

  const handleFeedback = (ticket) => {
    setSelectedTicket(ticket);
    setIsFeedbackModalOpen(true);
  };

  const handleRefund = (ticket) => {
    setSelectedTicket(ticket);
    setIsRefundModalOpen(true);
  };

  const handleSubmitFeedback = async (feedbackData) => {
    if (selectedTicket) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch('http://localhost:8081/feedback', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          },
          body: JSON.stringify({
            bookingId: selectedTicket.bookingId,
            rating: feedbackData.rating,
            title: feedbackData.title,
            comment: feedbackData.comment,
            category: feedbackData.category
          })
        });

        if (!response.ok) {
          let backendMessage = '';
          try {
            const responseText = await response.text();
            try {
              const errJson = JSON.parse(responseText);
              backendMessage = errJson?.message || JSON.stringify(errJson);
            } catch {
              backendMessage = responseText;
            }
          } catch (_) {
            backendMessage = `HTTP ${response.status}: ${response.statusText}`;
          }
          setError(backendMessage || 'Failed to submit feedback');
          return false;
        }

        setIsFeedbackModalOpen(false);
        setSelectedTicket(null);
        return true;
      } catch (error) {
        console.error('Error submitting feedback:', error);
        setError(error.message);
        return false;
      }
    }
    return false;
  };

  const handleSubmitRefund = async (refundData) => {
    if (selectedTicket) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch('http://localhost:8081/bookings/refund', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          },
          body: JSON.stringify({
            bookingId: selectedTicket.bookingId,
            accountNumber: refundData.accountNumber,
            bankName: refundData.bankName,
            accountHolderName: refundData.accountHolderName,
            reason: refundData.reason
          })
        });

        if (!response.ok) {
          let errorMessage = '';
          try {
            const responseText = await response.text();
            try {
              const errorData = JSON.parse(responseText);
              errorMessage = errorData.message || JSON.stringify(errorData);
            } catch {
              errorMessage = responseText || `HTTP ${response.status}: ${response.statusText}`;
            }
          } catch (_) {
            errorMessage = `HTTP ${response.status}: ${response.statusText}`;
          }
          console.error('Refund API Error:', errorMessage);
          throw new Error(errorMessage || `Failed to process refund: ${response.status} ${response.statusText}`);
        }

        const result = await response.json();
        alert('Refund processed successfully! ' + result.data);
        
        // Refresh tickets list
        await fetchMyTickets();

        setIsRefundModalOpen(false);
        setSelectedTicket(null);
        return true;
      } catch (error) {
        console.error('Error processing refund:', error);
        setError(error.message);
        return false;
      }
    }
    return false;
  };

  const handleDeleteTicket = async (ticket) => {
    if (ticket?.status?.toUpperCase() !== 'PENDING') {
      setError('Only pending bookings can be cancelled/deleted.');
      return;
    }
    if (confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const bookingId = ticket?.bookingId || ticket?.id;
        if (!bookingId) {
          setError('Missing booking id for this ticket');
          return;
        }
        
        const response = await fetch(`http://localhost:8081/bookings/${bookingId}/cancel?reason=Deleted by user`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          }
        });

        if (!response.ok) {
          let backendMessage = '';
          try {
            const responseText = await response.text();
            try {
              const errJson = JSON.parse(responseText);
              backendMessage = errJson?.message || JSON.stringify(errJson);
            } catch {
              backendMessage = responseText;
            }
          } catch (_) {
            backendMessage = `HTTP ${response.status}: ${response.statusText}`;
          }
          setError(backendMessage || 'Failed to delete booking');
          return;
        }

        // Refresh tickets list
        await fetchMyTickets();
      } catch (error) {
        console.error('Error deleting booking:', error);
        setError(error.message);
      }
    }
  };

  const confirmCancel = async () => {
    if (selectedTicket?.status !== 'PENDING') {
      setError('Only pending bookings can be cancelled.');
      setIsCancelModalOpen(false);
      setSelectedTicket(null);
      return;
    }
    if (selectedTicket) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch(`http://localhost:8081/bookings/${selectedTicket.bookingId}/cancel?reason=Cancelled by user`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          }
        });

        if (!response.ok) {
          let backendMessage = '';
          try {
            const responseText = await response.text();
            try {
              const errJson = JSON.parse(responseText);
              backendMessage = errJson?.message || JSON.stringify(errJson);
            } catch {
              backendMessage = responseText;
            }
          } catch (_) {
            backendMessage = `HTTP ${response.status}: ${response.statusText}`;
          }
          setError(backendMessage || 'Failed to cancel booking');
          setIsCancelModalOpen(false);
          setSelectedTicket(null);
          return;
        }

        // Refresh tickets list
        await fetchMyTickets();
      } catch (error) {
        console.error('Error cancelling booking:', error);
        setError(error.message);
      }
    }
    setIsCancelModalOpen(false);
    setSelectedTicket(null);
  };

  const downloadTicket = (ticket) => {
    // Simulate PDF download
    alert(`Downloading ticket for ${ticket.bookingId}`);
  };

  const getStatusColor = (status) => {
    const statusLower = status?.toLowerCase();
    switch (statusLower) {
      case 'confirmed': return 'success';
      case 'cancelled': return 'error';
      case 'pending': return 'success'; // Changed from warning to success for ongoing
      default: return 'default';
    }
  };

  const getStatusText = (status) => {
    const statusLower = status?.toLowerCase();
    switch (statusLower) {
      case 'confirmed': return 'Confirmed';
      case 'cancelled': return 'Cancelled';
      case 'pending': return 'Ongoing'; // Changed from Pending to Ongoing
      default: return status;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading your tickets...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md">
          <div className="flex">
            <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <div>
              <h3 className="text-sm font-medium text-red-800">Error loading tickets</h3>
              <p className="text-sm text-red-700 mt-1">{error}</p>
              <button 
                onClick={fetchMyTickets}
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
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container-custom">
        <div className="max-w-6xl mx-auto">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">My Tickets</h1>
            <p className="text-gray-600">Manage your train bookings and reservations</p>
          </div>

          {tickets.length === 0 ? (
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
              {tickets.map((ticket) => (
                <Card key={ticket.id} hover>
                  <CardContent className="p-6">
                    <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                      <div className="flex-1">
                        <div className="flex items-center justify-between mb-4">
                          <div>
                            <h3 className="text-xl font-semibold text-gray-900">
                              {ticket.train.name}
                            </h3>
                            <p className="text-gray-600">{ticket.train.fromStation} → {ticket.train.toStation}</p>
                          </div>
                          <StatusBadge status={getStatusColor(ticket.status.toLowerCase())}>
                            {getStatusText(ticket.status)}
                          </StatusBadge>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                          <div>
                            <p className="text-sm text-gray-500">Booking ID</p>
                            <p className="font-medium">{ticket.bookingId}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Journey Date</p>
                            <p className="font-medium">{formatDate(ticket.departureDate)}</p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Departure Time</p>
                            <p className="font-medium">
                              {formatTime(ticket.departureTime)} - {formatTime(ticket.arrivalTime)}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Seat</p>
                            <p className="font-medium">
                              {ticket.seatNumber} ({ticket.seatClass})
                            </p>
                          </div>
                        </div>

                        <div className="flex items-center justify-between">
                          <div className="flex items-center space-x-4">
                            <div>
                              <p className="text-sm text-gray-500">Total Amount</p>
                              <p className="text-lg font-semibold text-gray-900">
                                {formatCurrency(ticket.totalAmount)}
                              </p>
                            </div>
                            <div>
                              <p className="text-sm text-gray-500">Passengers</p>
                              <p className="font-medium">{ticket.passengers?.length || 0}</p>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div className="lg:ml-6 mt-4 lg:mt-0">
                        <div className="flex flex-col space-y-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => downloadTicket(ticket)}
                            className="w-full lg:w-auto"
                          >
                            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            Download PDF
                          </Button>

                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleFeedback(ticket)}
                            className="w-full lg:w-auto"
                          >
                            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                            </svg>
                            Feedback
                          </Button>

                          {(ticket.status?.toUpperCase() === 'PENDING') && (
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleRefund(ticket)}
                              className="w-full lg:w-auto bg-red-50 border-red-200 text-red-700 hover:bg-red-100"
                            >
                              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                              </svg>
                              Request Refund
                            </Button>
                          )}
                          
                          {(ticket.status?.toUpperCase() === 'PENDING') && (
                            <>
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditTicket(ticket)}
                                className="w-full lg:w-auto"
                              >
                                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                </svg>
                                Edit
                              </Button>

                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleRescheduleTicket(ticket)}
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
                                onClick={() => handleCancelTicket(ticket)}
                                className="w-full lg:w-auto"
                              >
                                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                                Cancel
                              </Button>

                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => handleDeleteTicket(ticket)}
                                className="w-full lg:w-auto"
                              >
                                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                                Delete
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

      {/* Cancel Ticket Modal */}
      <Modal
        isOpen={isCancelModalOpen}
        onClose={() => setIsCancelModalOpen(false)}
        title="Cancel Ticket"
        size="md"
      >
        <ModalBody>
          {selectedTicket && (
            <div>
              <p className="text-gray-600 mb-4">
                Are you sure you want to cancel your booking for <strong>{selectedTicket.train?.name}</strong>?
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
            Keep Ticket
          </Button>
          <Button variant="danger" onClick={confirmCancel}>
            Cancel Ticket
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
          {selectedTicket && (
            <div>
              <p className="text-gray-600 mb-6">
                Reschedule your booking for <strong>{selectedTicket.train?.name}</strong>
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

      {/* Edit Booking Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title="Edit Booking"
        size="lg"
      >
        <ModalBody>
          {selectedTicket && (
            <div className="space-y-6">
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-blue-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-blue-800">
                      <strong>Note:</strong> You can only edit passenger details and contact information. 
                      Train and date changes require rescheduling.
                    </p>
                  </div>
                </div>
              </div>

              <div>
                <h3 className="font-semibold text-gray-900 mb-4">Passenger Information</h3>
                <div className="space-y-4">
                  {selectedTicket.passengers?.map((passenger, index) => (
                    <div key={index} className="border rounded-lg p-4">
                      <h4 className="font-medium mb-3">Passenger {index + 1}</h4>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Full Name
                          </label>
                          <input
                            type="text"
                            defaultValue={passenger.name}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Age
                          </label>
                          <input
                            type="number"
                            defaultValue={passenger.age}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Gender
                          </label>
                          <select
                            defaultValue={passenger.gender}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          >
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                            <option value="OTHER">Other</option>
                          </select>
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            ID Number
                          </label>
                          <input
                            type="text"
                            defaultValue={passenger.idNumber}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
            Cancel
          </Button>
          <Button onClick={() => {
            // Here you would implement the save functionality
            alert('Edit functionality would be implemented here');
            setIsEditModalOpen(false);
          }}>
            Save Changes
          </Button>
        </ModalFooter>
      </Modal>

      {/* Feedback Modal */}
      <Modal isOpen={isFeedbackModalOpen} onClose={() => setIsFeedbackModalOpen(false)}>
        <ModalHeader>
          <h3 className="text-lg font-semibold text-gray-900">Submit Feedback</h3>
        </ModalHeader>
        <ModalBody>
          <FeedbackForm 
            ticket={selectedTicket}
            onSubmit={handleSubmitFeedback}
            onCancel={() => setIsFeedbackModalOpen(false)}
          />
        </ModalBody>
      </Modal>

      {/* Refund Modal */}
      <Modal isOpen={isRefundModalOpen} onClose={() => setIsRefundModalOpen(false)}>
        <ModalHeader>
          <h3 className="text-lg font-semibold text-gray-900">Request Refund</h3>
        </ModalHeader>
        <ModalBody>
          <RefundForm 
            ticket={selectedTicket}
            onSubmit={handleSubmitRefund}
            onCancel={() => setIsRefundModalOpen(false)}
          />
        </ModalBody>
      </Modal>
    </div>
  );
}

// Feedback Form Component
function FeedbackForm({ ticket, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    rating: 5,
    title: '',
    comment: '',
    category: 'SERVICE_QUALITY'
  });

  const categories = [
    { value: 'SERVICE_QUALITY', label: 'Service Quality' },
    { value: 'CLEANLINESS', label: 'Cleanliness' },
    { value: 'PUNCTUALITY', label: 'Punctuality' },
    { value: 'STAFF_BEHAVIOR', label: 'Staff Behavior' },
    { value: 'FOOD_QUALITY', label: 'Food Quality' },
    { value: 'OTHER', label: 'Other' }
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title.trim() || !formData.comment.trim()) {
      alert('Please fill in all required fields');
      return;
    }
    
    const success = await onSubmit(formData);
    if (success) {
      setFormData({ rating: 5, title: '', comment: '', category: 'SERVICE_QUALITY' });
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {ticket && (
        <div className="bg-gray-50 p-3 rounded-lg mb-4">
          <p className="text-sm text-gray-600">
            <strong>Booking:</strong> {ticket.bookingId} | 
            <strong> Train:</strong> {ticket.train?.name || 'N/A'} | 
            <strong> Date:</strong> {formatDate(ticket.departureDate)}
          </p>
        </div>
      )}

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Rating *
        </label>
        <div className="flex space-x-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              type="button"
              onClick={() => handleChange('rating', star)}
              className={`text-2xl ${
                star <= formData.rating 
                  ? 'text-yellow-400' 
                  : 'text-gray-300 hover:text-yellow-400'
              }`}
            >
              ★
            </button>
          ))}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Category *
        </label>
        <select
          value={formData.category}
          onChange={(e) => handleChange('category', e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          required
        >
          {categories.map(category => (
            <option key={category.value} value={category.value}>
              {category.label}
            </option>
          ))}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Title *
        </label>
        <input
          type="text"
          value={formData.title}
          onChange={(e) => handleChange('title', e.target.value)}
          placeholder="Brief title for your feedback"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          maxLength={200}
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Comment *
        </label>
        <textarea
          value={formData.comment}
          onChange={(e) => handleChange('comment', e.target.value)}
          placeholder="Share your detailed feedback..."
          rows={4}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          maxLength={1000}
          required
        />
        <p className="text-xs text-gray-500 mt-1">
          {formData.comment.length}/1000 characters
        </p>
      </div>

      <div className="flex justify-end space-x-3 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit">
          Submit Feedback
        </Button>
      </div>
    </form>
  );
}

// Refund Form Component
function RefundForm({ ticket, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    accountNumber: '',
    bankName: '',
    accountHolderName: '',
    reason: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.accountNumber.trim() || !formData.bankName.trim() || 
        !formData.accountHolderName.trim() || !formData.reason.trim()) {
      alert('Please fill in all required fields');
      return;
    }
    
    const success = await onSubmit(formData);
    if (success) {
      setFormData({ accountNumber: '', bankName: '', accountHolderName: '', reason: '' });
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {ticket && (
        <div className="bg-gray-50 p-4 rounded-lg">
          <h4 className="font-medium text-gray-900 mb-2">Ticket Details</h4>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-500">Booking ID:</span>
              <span className="ml-2 font-medium">{ticket.bookingId}</span>
            </div>
            <div>
              <span className="text-gray-500">Amount:</span>
              <span className="ml-2 font-medium">LKR {ticket.totalAmount}</span>
            </div>
            <div>
              <span className="text-gray-500">Train:</span>
              <span className="ml-2 font-medium">{ticket.train.name}</span>
            </div>
            <div>
              <span className="text-gray-500">Date:</span>
              <span className="ml-2 font-medium">{new Date(ticket.departureDate).toLocaleDateString()}</span>
            </div>
          </div>
        </div>
      )}

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Account Number *
          </label>
          <input
            type="text"
            value={formData.accountNumber}
            onChange={(e) => handleChange('accountNumber', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter your account number"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Bank Name *
          </label>
          <input
            type="text"
            value={formData.bankName}
            onChange={(e) => handleChange('bankName', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter your bank name"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Account Holder Name *
          </label>
          <input
            type="text"
            value={formData.accountHolderName}
            onChange={(e) => handleChange('accountHolderName', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter account holder name"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Reason for Refund *
          </label>
          <textarea
            value={formData.reason}
            onChange={(e) => handleChange('reason', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Please explain why you need a refund"
            rows={3}
            required
          />
        </div>
      </div>

      <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg className="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-yellow-800">
              Important Notice
            </h3>
            <div className="mt-2 text-sm text-yellow-700">
              <p>
                • Refund will be processed to the account details provided above<br/>
                • Processing time: 3-5 business days<br/>
                • Your booking will be cancelled immediately after refund approval<br/>
                • Refund amount: LKR {ticket?.totalAmount || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="flex justify-end space-x-3">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          className="bg-red-600 hover:bg-red-700 text-white"
        >
          Request Refund
        </Button>
      </div>
    </form>
  );
}
