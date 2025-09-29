'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card';
import Button from '../../../components/ui/button';
import { Input, Select, TextArea } from '../../../components/ui/input';
import { StatusBadge } from '../../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../../components/ui/modal';
import trainsData from '../../../data/trains.json';
import { formatCurrency } from '../../../lib/utils';

export default function AdminTrainsPage() {
  const [trains, setTrains] = useState(trainsData);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [trainForm, setTrainForm] = useState({
    name: '',
    type: '',
    route: '',
    from: '',
    to: '',
    departureTime: '',
    arrivalTime: '',
    duration: '',
    distance: '',
    price: {
      economy: '',
      business: '',
      first: ''
    },
    seats: {
      total: '',
      available: {
        economy: '',
        business: '',
        first: ''
      }
    },
    features: [],
    status: 'active'
  });

  const trainTypes = [
    { value: 'express', label: 'Express' },
    { value: 'intercity', label: 'Intercity' },
    { value: 'scenic', label: 'Scenic' },
    { value: 'local', label: 'Local' }
  ];

  const trainFeatures = [
    'AC', 'WiFi', 'Food Service', 'Charging Points', 'Luggage Storage', 'Entertainment', 'Scenic Views'
  ];

  const handleInputChange = (field, value) => {
    if (field.includes('.')) {
      const [parent, child] = field.split('.');
      setTrainForm(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: value
        }
      }));
    } else {
      setTrainForm(prev => ({
        ...prev,
        [field]: value
      }));
    }
  };

  const handleFeatureToggle = (feature) => {
    setTrainForm(prev => ({
      ...prev,
      features: prev.features.includes(feature)
        ? prev.features.filter(f => f !== feature)
        : [...prev.features, feature]
    }));
  };

  const openAddModal = () => {
    setTrainForm({
      name: '',
      type: '',
      route: '',
      from: '',
      to: '',
      departureTime: '',
      arrivalTime: '',
      duration: '',
      distance: '',
      price: {
        economy: '',
        business: '',
        first: ''
      },
      seats: {
        total: '',
        available: {
          economy: '',
          business: '',
          first: ''
        }
      },
      features: [],
      status: 'active'
    });
    setIsAddModalOpen(true);
  };

  const openEditModal = (train) => {
    setSelectedTrain(train);
    setTrainForm({
      name: train.name,
      type: train.type,
      route: train.route,
      from: train.from,
      to: train.to,
      departureTime: train.departureTime,
      arrivalTime: train.arrivalTime,
      duration: train.duration,
      distance: train.distance,
      price: {
        economy: train.price.economy.toString(),
        business: train.price.business.toString(),
        first: train.price.first.toString()
      },
      seats: {
        total: train.seats.total.toString(),
        available: {
          economy: train.seats.available.economy.toString(),
          business: train.seats.available.business.toString(),
          first: train.seats.available.first.toString()
        }
      },
      features: train.features,
      status: train.status
    });
    setIsEditModalOpen(true);
  };

  const openDeleteModal = (train) => {
    setSelectedTrain(train);
    setIsDeleteModalOpen(true);
  };

  const handleAddTrain = () => {
    const newTrain = {
      id: `T${String(trains.length + 1).padStart(3, '0')}`,
      ...trainForm,
      price: {
        economy: parseInt(trainForm.price.economy),
        business: parseInt(trainForm.price.business),
        first: parseInt(trainForm.price.first)
      },
      seats: {
        total: parseInt(trainForm.seats.total),
        available: {
          economy: parseInt(trainForm.seats.available.economy),
          business: parseInt(trainForm.seats.available.business),
          first: parseInt(trainForm.seats.available.first)
        }
      }
    };
    setTrains(prev => [...prev, newTrain]);
    setIsAddModalOpen(false);
  };

  const handleEditTrain = () => {
    setTrains(prev => 
      prev.map(train => 
        train.id === selectedTrain.id 
          ? {
              ...train,
              ...trainForm,
              price: {
                economy: parseInt(trainForm.price.economy),
                business: parseInt(trainForm.price.business),
                first: parseInt(trainForm.price.first)
              },
              seats: {
                total: parseInt(trainForm.seats.total),
                available: {
                  economy: parseInt(trainForm.seats.available.economy),
                  business: parseInt(trainForm.seats.available.business),
                  first: parseInt(trainForm.seats.available.first)
                }
              }
            }
          : train
      )
    );
    setIsEditModalOpen(false);
    setSelectedTrain(null);
  };

  const handleDeleteTrain = () => {
    setTrains(prev => prev.filter(train => train.id !== selectedTrain.id));
    setIsDeleteModalOpen(false);
    setSelectedTrain(null);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Trains Management</h1>
          <p className="text-gray-600 mt-2">Manage your train fleet and schedules</p>
        </div>
        <Button onClick={openAddModal}>
          Add New Train
        </Button>
      </div>

      {/* Trains Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Trains</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Train</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Route</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Type</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Schedule</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Seats</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Price (from)</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Status</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody>
                {trains.map((train) => (
                  <tr key={train.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4">
                      <div>
                        <p className="font-medium text-gray-900">{train.name}</p>
                        <p className="text-sm text-gray-500">ID: {train.id}</p>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{train.route}</p>
                      <p className="text-sm text-gray-500">{train.distance}</p>
                    </td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                        {train.type}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{train.departureTime} - {train.arrivalTime}</p>
                      <p className="text-sm text-gray-500">{train.duration}</p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{train.seats.available.economy + train.seats.available.business + train.seats.available.first}/{train.seats.total}</p>
                      <p className="text-sm text-gray-500">Available</p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="font-medium text-gray-900">{formatCurrency(train.price.economy)}</p>
                    </td>
                    <td className="py-3 px-4">
                      <StatusBadge status={train.status} />
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex space-x-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => openEditModal(train)}
                        >
                          Edit
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => openDeleteModal(train)}
                        >
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      {/* Add Train Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Add New Train"
        size="lg"
      >
        <ModalBody>
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Train Name"
                value={trainForm.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                required
              />
              <Select
                label="Train Type"
                value={trainForm.type}
                onChange={(e) => handleInputChange('type', e.target.value)}
                options={trainTypes}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="From Station"
                value={trainForm.from}
                onChange={(e) => handleInputChange('from', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={trainForm.to}
                onChange={(e) => handleInputChange('to', e.target.value)}
                required
              />
            </div>
            
            <Input
              label="Route"
              value={trainForm.route}
              onChange={(e) => handleInputChange('route', e.target.value)}
              required
            />
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Departure Time"
                type="time"
                value={trainForm.departureTime}
                onChange={(e) => handleInputChange('departureTime', e.target.value)}
                required
              />
              <Input
                label="Arrival Time"
                type="time"
                value={trainForm.arrivalTime}
                onChange={(e) => handleInputChange('arrivalTime', e.target.value)}
                required
              />
              <Input
                label="Duration"
                placeholder="2h 30m"
                value={trainForm.duration}
                onChange={(e) => handleInputChange('duration', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Distance"
                placeholder="120 km"
                value={trainForm.distance}
                onChange={(e) => handleInputChange('distance', e.target.value)}
                required
              />
              <Input
                label="Total Seats"
                type="number"
                value={trainForm.seats.total}
                onChange={(e) => handleInputChange('seats.total', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Economy Price (LKR)"
                type="number"
                value={trainForm.price.economy}
                onChange={(e) => handleInputChange('price.economy', e.target.value)}
                required
              />
              <Input
                label="Business Price (LKR)"
                type="number"
                value={trainForm.price.business}
                onChange={(e) => handleInputChange('price.business', e.target.value)}
                required
              />
              <Input
                label="First Class Price (LKR)"
                type="number"
                value={trainForm.price.first}
                onChange={(e) => handleInputChange('price.first', e.target.value)}
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Features</label>
              <div className="flex flex-wrap gap-2">
                {trainFeatures.map((feature) => (
                  <button
                    key={feature}
                    type="button"
                    onClick={() => handleFeatureToggle(feature)}
                    className={`px-3 py-1 rounded-full text-sm transition-colors ${
                      trainForm.features.includes(feature)
                        ? 'bg-blue-100 text-blue-800 border border-blue-200'
                        : 'bg-gray-100 text-gray-600 border border-gray-200 hover:bg-gray-200'
                    }`}
                  >
                    {feature}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsAddModalOpen(false)}>
            Cancel
          </Button>
          <Button onClick={handleAddTrain}>
            Add Train
          </Button>
        </ModalFooter>
      </Modal>

      {/* Edit Train Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title="Edit Train"
        size="lg"
      >
        <ModalBody>
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Train Name"
                value={trainForm.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                required
              />
              <Select
                label="Train Type"
                value={trainForm.type}
                onChange={(e) => handleInputChange('type', e.target.value)}
                options={trainTypes}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="From Station"
                value={trainForm.from}
                onChange={(e) => handleInputChange('from', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={trainForm.to}
                onChange={(e) => handleInputChange('to', e.target.value)}
                required
              />
            </div>
            
            <Input
              label="Route"
              value={trainForm.route}
              onChange={(e) => handleInputChange('route', e.target.value)}
              required
            />
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Departure Time"
                type="time"
                value={trainForm.departureTime}
                onChange={(e) => handleInputChange('departureTime', e.target.value)}
                required
              />
              <Input
                label="Arrival Time"
                type="time"
                value={trainForm.arrivalTime}
                onChange={(e) => handleInputChange('arrivalTime', e.target.value)}
                required
              />
              <Input
                label="Duration"
                placeholder="2h 30m"
                value={trainForm.duration}
                onChange={(e) => handleInputChange('duration', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Distance"
                placeholder="120 km"
                value={trainForm.distance}
                onChange={(e) => handleInputChange('distance', e.target.value)}
                required
              />
              <Input
                label="Total Seats"
                type="number"
                value={trainForm.seats.total}
                onChange={(e) => handleInputChange('seats.total', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Economy Price (LKR)"
                type="number"
                value={trainForm.price.economy}
                onChange={(e) => handleInputChange('price.economy', e.target.value)}
                required
              />
              <Input
                label="Business Price (LKR)"
                type="number"
                value={trainForm.price.business}
                onChange={(e) => handleInputChange('price.business', e.target.value)}
                required
              />
              <Input
                label="First Class Price (LKR)"
                type="number"
                value={trainForm.price.first}
                onChange={(e) => handleInputChange('price.first', e.target.value)}
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Features</label>
              <div className="flex flex-wrap gap-2">
                {trainFeatures.map((feature) => (
                  <button
                    key={feature}
                    type="button"
                    onClick={() => handleFeatureToggle(feature)}
                    className={`px-3 py-1 rounded-full text-sm transition-colors ${
                      trainForm.features.includes(feature)
                        ? 'bg-blue-100 text-blue-800 border border-blue-200'
                        : 'bg-gray-100 text-gray-600 border border-gray-200 hover:bg-gray-200'
                    }`}
                  >
                    {feature}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
            Cancel
          </Button>
          <Button onClick={handleEditTrain}>
            Save Changes
          </Button>
        </ModalFooter>
      </Modal>

      {/* Delete Train Modal */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Delete Train"
        size="md"
      >
        <ModalBody>
          {selectedTrain && (
            <div>
              <p className="text-gray-600 mb-4">
                Are you sure you want to delete <strong>{selectedTrain.name}</strong>?
              </p>
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-red-800">
                      <strong>Warning:</strong> This action cannot be undone. All associated bookings and data will be permanently deleted.
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
          <Button variant="danger" onClick={handleDeleteTrain}>
            Delete Train
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}
