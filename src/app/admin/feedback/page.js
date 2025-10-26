'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card';
import Button from '../../../components/ui/button';
import { Input, Select } from '../../../components/ui/input';
import { StatusBadge } from '../../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../../components/ui/modal';
import { formatDate } from '../../../lib/utils';

export default function AdminFeedbackPage() {
  const router = useRouter();
  const [feedback, setFeedback] = useState([]);
  const [selectedFeedback, setSelectedFeedback] = useState(null);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [filterStatus, setFilterStatus] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const statusOptions = [
    { value: 'all', label: 'All Status' },
    { value: 'REVIEWED', label: 'Reviewed' },
    { value: 'PENDING', label: 'Pending' }
  ];

  useEffect(() => {
    fetchFeedback();
  }, []);

  const fetchFeedback = async () => {
    try {
      setLoading(true);
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      
      if (!authData.token) {
        router.push('/login');
        return;
      }

      const response = await fetch('http://localhost:8081/feedback/admin', {
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
        throw new Error('Failed to fetch feedback');
      }

      const result = await response.json();
      const list = (result?.data && (result.data.content ?? result.data)) || [];
      setFeedback(Array.isArray(list) ? list : []);
    } catch (error) {
      console.error('Error fetching feedback:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredFeedback = feedback.filter(item => {
    const matchesStatus = filterStatus === 'all' || item.status === filterStatus;
    const matchesSearch = 
      item.user?.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.user?.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.comment?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.train?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.title?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  const openDetailsModal = (item) => {
    setSelectedFeedback(item);
    setIsDetailsModalOpen(true);
  };

  const openDeleteModal = (item) => {
    setSelectedFeedback(item);
    setIsDeleteModalOpen(true);
  };

  const handleDeleteFeedback = async () => {
    if (selectedFeedback) {
      try {
        const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
        
        const response = await fetch(`http://localhost:8081/feedback/${selectedFeedback.feedbackId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authData.token}`
          }
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('Delete feedback error response:', errorText);
          throw new Error(`Failed to delete feedback: ${response.status} ${response.statusText}`);
        }

        // Refresh feedback list
        await fetchFeedback();
      } catch (error) {
        console.error('Error deleting feedback:', error);
        setError(error.message);
      }
    }
    setIsDeleteModalOpen(false);
    setSelectedFeedback(null);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'REVIEWED': return 'success';
      case 'PENDING': return 'warning';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading feedback...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 text-6xl mb-4">⚠️</div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Error Loading Feedback</h1>
          <p className="text-gray-600 mb-4">{error}</p>
          <Button onClick={fetchFeedback}>
            Try Again
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Feedback Management</h1>
        <p className="text-gray-600 mt-2">Review and manage customer feedback</p>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <Input
                label="Search"
                placeholder="Search by user name, comment, or train name..."
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

      {/* Feedback Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Feedback ({filteredFeedback.length})</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {filteredFeedback.map((item) => (
              <div key={item.id} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h3 className="font-medium text-gray-900">{item.title}</h3>
                    <p className="text-sm text-gray-500">
                      by {item.user?.firstName} {item.user?.lastName} • {formatDate(item.submittedDate)}
                    </p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <div className="flex items-center">
                      {[...Array(5)].map((_, i) => (
                        <svg
                          key={i}
                          className={`w-4 h-4 ${
                            i < item.rating ? 'text-yellow-400' : 'text-gray-300'
                          }`}
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      ))}
                    </div>
                    <StatusBadge status={getStatusColor(item.status)} />
                  </div>
                </div>
                
                <p className="text-gray-700 mb-3 line-clamp-2">{item.comment}</p>
                
                <div className="flex justify-between items-center">
                  <div className="flex items-center space-x-4 text-sm text-gray-500">
                    <span>Category: {item.category}</span>
                    {item.train?.name && <span>Train: {item.train.name}</span>}
                    {item.booking?.bookingId && <span>Booking: {item.booking.bookingId}</span>}
                  </div>
                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => openDetailsModal(item)}
                    >
                      View
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => openDeleteModal(item)}
                    >
                      Delete
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Feedback Details Modal */}
      <Modal
        isOpen={isDetailsModalOpen}
        onClose={() => setIsDetailsModalOpen(false)}
        title="Feedback Details"
        size="lg"
      >
        <ModalBody>
          {selectedFeedback && (
            <div className="space-y-6">
              <div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">{selectedFeedback.title}</h3>
                <div className="flex items-center space-x-4 mb-4">
                  <div className="flex items-center">
                    {[...Array(5)].map((_, i) => (
                      <svg
                        key={i}
                        className={`w-5 h-5 ${
                          i < selectedFeedback.rating ? 'text-yellow-400' : 'text-gray-300'
                        }`}
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    ))}
                  </div>
                  <StatusBadge status={getStatusColor(selectedFeedback.status)} />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">User Information</h4>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Name:</span>
                      <span className="font-medium">{selectedFeedback.user?.firstName} {selectedFeedback.user?.lastName}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Email:</span>
                      <span className="font-medium">{selectedFeedback.user?.email}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Submitted:</span>
                      <span className="font-medium">{formatDate(selectedFeedback.submittedDate)}</span>
                    </div>
                  </div>
                </div>

                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Feedback Details</h4>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Category:</span>
                      <span className="font-medium capitalize">{selectedFeedback.category}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Rating:</span>
                      <span className="font-medium">{selectedFeedback.rating}/5</span>
                    </div>
                    {selectedFeedback.train?.name && (
                      <div className="flex justify-between">
                        <span className="text-gray-600">Train:</span>
                        <span className="font-medium">{selectedFeedback.train.name}</span>
                      </div>
                    )}
                    {selectedFeedback.booking?.bookingId && (
                      <div className="flex justify-between">
                        <span className="text-gray-600">Booking ID:</span>
                        <span className="font-medium">{selectedFeedback.booking.bookingId}</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>

              <div>
                <h4 className="font-semibold text-gray-900 mb-2">Comment</h4>
                <div className="bg-gray-50 rounded-lg p-4">
                  <p className="text-gray-700">{selectedFeedback.comment}</p>
                </div>
              </div>

              {selectedFeedback.adminResponse && (
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Admin Response</h4>
                  <div className="bg-blue-50 rounded-lg p-4">
                    <p className="text-gray-700">{selectedFeedback.adminResponse}</p>
                  </div>
                </div>
              )}
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsDetailsModalOpen(false)}>
            Close
          </Button>
        </ModalFooter>
      </Modal>

      {/* Delete Feedback Modal */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Delete Feedback"
        size="md"
      >
        <ModalBody>
          {selectedFeedback && (
            <div>
              <p className="text-gray-600 mb-4">
                Are you sure you want to delete this feedback from <strong>{selectedFeedback.userName}</strong>?
              </p>
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-red-800">
                      <strong>Warning:</strong> This action cannot be undone. The feedback will be permanently deleted.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsDeleteModalOpen(false)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDeleteFeedback}>
            Delete Feedback
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}
