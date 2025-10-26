'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card';
import Button from '../../../components/ui/button';
import { Input, Select, TextArea } from '../../../components/ui/input';
import { StatusBadge } from '../../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../../components/ui/modal';
import { formatCurrency } from '../../../lib/utils';

export default function AdminTrainsPage() {
  const router = useRouter();
  const [trains, setTrains] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [trainForm, setTrainForm] = useState({
    name: '',
    type: 'EXPRESS',
    route: '',
    fromStation: '',
    toStation: '',
    departureTime: '',
    arrivalTime: '',
    duration: '',
    distance: '',
    pricing: {
      economy: '',
      business: '',
      first: ''
    },
    seatInfo: {
      totalSeats: '',
      availableEconomy: '',
      availableBusiness: '',
      availableFirst: ''
    },
    features: [],
    status: 'ACTIVE'
  });

  useEffect(() => {
    // Check authentication first
    const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
    console.log('Initial auth check:', authData);
    
    if (!authData.token) {
      console.log('No token found, redirecting to login');
      router.push('/login');
      return;
    }
    
    fetchTrains();
  }, [router]);

  const fetchTrains = async () => {
    try {
      setLoading(true);
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      
      console.log('Auth data from localStorage:', authData);
      
      if (!authData.token) {
        console.error('No authentication token found');
        router.push('/login');
        return;
      }

      const response = await fetch('http://localhost:8081/trains', {
        headers: {
          'Authorization': `Bearer ${authData.token}`
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          console.error('Unauthorized access - redirecting to login');
          router.push('/login');
          return;
        }
        throw new Error('Failed to fetch trains');
      }

      const result = await response.json();
      const trainsData = result.data?.content || result.data || [];
      console.log('Trains data from API:', trainsData);
      setTrains(trainsData);
    } catch (error) {
      console.error('Error fetching trains:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const trainTypes = [
    { value: 'EXPRESS', label: 'Express' },
    { value: 'PASSENGER', label: 'Passenger' },
    { value: 'MAIL', label: 'Mail' },
    { value: 'SUPERFAST', label: 'Superfast' }
  ];

  const trainStatuses = [
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'MAINTENANCE', label: 'Maintenance' }
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
      type: 'EXPRESS',
      route: '',
      fromStation: '',
      toStation: '',
      departureTime: '',
      arrivalTime: '',
      duration: '',
      distance: '',
      pricing: {
        economy: '',
        business: '',
        first: ''
      },
      seatInfo: {
        totalSeats: '',
        availableEconomy: '',
        availableBusiness: '',
        availableFirst: ''
      },
      features: [],
      status: 'ACTIVE'
    });
    setIsAddModalOpen(true);
  };

  const openEditModal = (train) => {
    setSelectedTrain(train);
    setTrainForm({
      name: train.name,
      type: train.type,
      route: train.route,
      fromStation: train.fromStation,
      toStation: train.toStation,
      departureTime: train.departureTime,
      arrivalTime: train.arrivalTime,
      duration: train.duration,
      distance: train.distance,
      pricing: {
        economy: train.pricing?.economyPrice?.toString() || train.pricing?.economy?.toString() || '',
        business: train.pricing?.businessPrice?.toString() || train.pricing?.business?.toString() || '',
        first: train.pricing?.firstPrice?.toString() || train.pricing?.first?.toString() || ''
      },
      seatInfo: {
        totalSeats: train.seatInfo?.totalSeats?.toString() || '',
        availableEconomy: train.seatInfo?.availableEconomy?.toString() || '',
        availableBusiness: train.seatInfo?.availableBusiness?.toString() || '',
        availableFirst: train.seatInfo?.availableFirst?.toString() || ''
      },
      features: train.features || [],
      status: train.status
    });
    setIsEditModalOpen(true);
  };

  const openDeleteModal = (train) => {
    setSelectedTrain(train);
    setIsDeleteModalOpen(true);
  };

  const handleAddTrain = async () => {
    try {
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');

      const toNumber = (val) => {
        if (val === null || val === undefined) return 0;
        const cleaned = String(val).replace(/[^0-9.\-]/g, '');
        const parsed = cleaned === '' ? NaN : Number(cleaned);
        return Number.isFinite(parsed) ? parsed : 0;
      };

      const normalizeTime = (t) => {
        if (!t) return '00:00:00';
        // Accept HH:mm or HH:mm:ss
        const parts = String(t).split(':');
        if (parts.length === 2) return `${parts[0].padStart(2,'0')}:${parts[1].padStart(2,'0')}:00`;
        if (parts.length >= 3) return `${parts[0].padStart(2,'0')}:${parts[1].padStart(2,'0')}:${parts[2].padStart(2,'0')}`;
        return '00:00:00';
      };

      const distanceNum = toNumber(trainForm.distance);
      const priceEco = toNumber(trainForm.pricing.economy);
      const priceBus = toNumber(trainForm.pricing.business);
      const priceFirst = toNumber(trainForm.pricing.first);
      const totalSeats = toNumber(trainForm.seatInfo.totalSeats);
      const availEco = toNumber(trainForm.seatInfo.availableEconomy);
      const availBus = toNumber(trainForm.seatInfo.availableBusiness);
      const availFirst = toNumber(trainForm.seatInfo.availableFirst);

      // Front-end validations mirroring typical backend constraints
      if (!trainForm.name?.trim()) throw new Error('Train name is required');
      if (!trainForm.fromStation?.trim() || !trainForm.toStation?.trim()) throw new Error('From and To stations are required');
      if (distanceNum <= 0) throw new Error('Distance must be greater than 0');
      if (priceEco < 0 || priceBus < 0 || priceFirst < 0) throw new Error('Prices must be non-negative');
      if (totalSeats <= 0) throw new Error('Total seats must be greater than 0');
      if (availEco < 0 || availBus < 0 || availFirst < 0) throw new Error('Available seats must be non-negative');
      if (availEco + availBus + availFirst > totalSeats) throw new Error('Sum of available seats cannot exceed total seats');

      const trainData = {
        name: trainForm.name?.trim(),
        type: trainForm.type,
        route: trainForm.route?.trim(),
        fromStation: trainForm.fromStation?.trim(),
        toStation: trainForm.toStation?.trim(),
        departureTime: normalizeTime(trainForm.departureTime),
        arrivalTime: normalizeTime(trainForm.arrivalTime),
        duration: trainForm.duration?.trim(),
        distance: `${distanceNum} km`,
        pricing: {
          economyPrice: priceEco,
          businessPrice: priceBus,
          firstPrice: priceFirst
        },
        seatInfo: {
          totalSeats: totalSeats,
          availableEconomy: availEco,
          availableBusiness: availBus,
          availableFirst: availFirst
        },
        features: trainForm.features,
        status: trainForm.status
      };

      const response = await fetch('http://localhost:8081/trains', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        },
        body: JSON.stringify(trainData)
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
        throw new Error(backendMessage || 'Failed to create train');
      }

      await fetchTrains(); // Refresh the list
      setIsAddModalOpen(false);
    } catch (error) {
      console.error('Error creating train:', error);
      alert('Failed to create train: ' + (error?.message || 'Unknown error'));
    }
  };

  const handleEditTrain = async () => {
    try {
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');

      const toNumber = (val) => {
        if (val === null || val === undefined) return 0;
        const cleaned = String(val).replace(/[^0-9.\-]/g, '');
        const parsed = cleaned === '' ? NaN : Number(cleaned);
        return Number.isFinite(parsed) ? parsed : 0;
      };

      const normalizeTime = (t) => {
        if (!t) return '00:00:00';
        const parts = String(t).split(':');
        if (parts.length === 2) return `${parts[0].padStart(2,'0')}:${parts[1].padStart(2,'0')}:00`;
        if (parts.length >= 3) return `${parts[0].padStart(2,'0')}:${parts[1].padStart(2,'0')}:${parts[2].padStart(2,'0')}`;
        return '00:00:00';
      };

      const distanceNum = toNumber(trainForm.distance);
      const priceEco = toNumber(trainForm.pricing.economy);
      const priceBus = toNumber(trainForm.pricing.business);
      const priceFirst = toNumber(trainForm.pricing.first);
      const totalSeats = toNumber(trainForm.seatInfo.totalSeats);
      const availEco = toNumber(trainForm.seatInfo.availableEconomy);
      const availBus = toNumber(trainForm.seatInfo.availableBusiness);
      const availFirst = toNumber(trainForm.seatInfo.availableFirst);

      if (!trainForm.name?.trim()) throw new Error('Train name is required');
      if (!trainForm.fromStation?.trim() || !trainForm.toStation?.trim()) throw new Error('From and To stations are required');
      if (distanceNum <= 0) throw new Error('Distance must be greater than 0');
      if (priceEco < 0 || priceBus < 0 || priceFirst < 0) throw new Error('Prices must be non-negative');
      if (totalSeats <= 0) throw new Error('Total seats must be greater than 0');
      if (availEco < 0 || availBus < 0 || availFirst < 0) throw new Error('Available seats must be non-negative');
      if (availEco + availBus + availFirst > totalSeats) throw new Error('Sum of available seats cannot exceed total seats');

      const trainData = {
        name: trainForm.name?.trim(),
        type: trainForm.type,
        route: trainForm.route?.trim(),
        fromStation: trainForm.fromStation?.trim(),
        toStation: trainForm.toStation?.trim(),
        departureTime: normalizeTime(trainForm.departureTime),
        arrivalTime: normalizeTime(trainForm.arrivalTime),
        duration: trainForm.duration?.trim(),
        distance: `${distanceNum} km`,
        pricing: {
          economyPrice: priceEco,
          businessPrice: priceBus,
          firstPrice: priceFirst
        },
        seatInfo: {
          totalSeats: totalSeats,
          availableEconomy: availEco,
          availableBusiness: availBus,
          availableFirst: availFirst
        },
        features: trainForm.features,
        status: trainForm.status
      };

      const response = await fetch(`http://localhost:8081/trains/${selectedTrain.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        },
        body: JSON.stringify(trainData)
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
        throw new Error(backendMessage || 'Failed to update train');
      }

      await fetchTrains(); // Refresh the list
      setIsEditModalOpen(false);
      setSelectedTrain(null);
    } catch (error) {
      console.error('Error updating train:', error);
      alert('Failed to update train: ' + (error?.message || 'Unknown error'));
    }
  };

  const handleDeleteTrain = async () => {
    try {
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      
      const response = await fetch(`http://localhost:8081/trains/${selectedTrain.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${authData.token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete train');
      }

      await fetchTrains(); // Refresh the list
      setIsDeleteModalOpen(false);
      setSelectedTrain(null);
    } catch (error) {
      console.error('Error deleting train:', error);
      alert('Failed to delete train: ' + error.message);
    }
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Trains Management</h1>
            <p className="text-gray-600 mt-2">Loading trains...</p>
          </div>
        </div>
        <Card>
          <CardContent className="p-8">
            <div className="text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="text-gray-600 mt-2">Loading trains...</p>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

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

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-800">Error: {error}</p>
          <Button variant="outline" size="sm" onClick={fetchTrains} className="mt-2">
            Retry
          </Button>
        </div>
      )}

      {/* Trains Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Trains ({trains.length})</CardTitle>
        </CardHeader>
        <CardContent>
          {trains.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">No trains found. Add your first train to get started.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Train</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Route</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Type</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Schedule</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Seats</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Economy</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Business</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">First Class</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Status</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-900">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {trains.map((train) => {
                    console.log('Train pricing data for', train.name, ':', train.pricing);
                    const getNumber = (v) => {
                      const n = typeof v === 'number' ? v : Number(v);
                      return Number.isFinite(n) ? n : 0;
                    };
                    const priceEconomy = getNumber(train?.pricing?.economyPrice ?? train?.pricing?.economy ?? train?.economy ?? train?.economyPrice);
                    const priceBusiness = getNumber(train?.pricing?.businessPrice ?? train?.pricing?.business ?? train?.business ?? train?.businessPrice);
                    const priceFirst = getNumber(train?.pricing?.firstPrice ?? train?.pricing?.first ?? train?.first ?? train?.firstClassPrice);
                    return (
                    <tr key={train.id} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="py-3 px-4">
                        <div>
                          <p className="font-medium text-gray-900">{train.name}</p>
                          <p className="text-sm text-gray-500">ID: {train.id}</p>
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <p className="text-gray-900">{train.route}</p>
                        <p className="text-sm text-gray-500">{train.distance} km</p>
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
                        <p className="text-gray-900">
                          {(train.seatInfo?.availableEconomy || 0) + (train.seatInfo?.availableBusiness || 0) + (train.seatInfo?.availableFirst || 0)}/{train.seatInfo?.totalSeats || 0}
                        </p>
                        <p className="text-sm text-gray-500">Available</p>
                      </td>
                      <td className="py-3 px-4">
                        <p className="font-medium text-gray-900">{formatCurrency(priceEconomy)}</p>
                      </td>
                      <td className="py-3 px-4">
                        <p className="font-medium text-gray-900">{formatCurrency(priceBusiness)}</p>
                      </td>
                      <td className="py-3 px-4">
                        <p className="font-medium text-gray-900">{formatCurrency(priceFirst)}</p>
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
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
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
                value={trainForm.fromStation}
                onChange={(e) => handleInputChange('fromStation', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={trainForm.toStation}
                onChange={(e) => handleInputChange('toStation', e.target.value)}
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
                value={trainForm.seatInfo.totalSeats}
                onChange={(e) => handleInputChange('seatInfo.totalSeats', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Economy Price (LKR)"
                type="number"
                value={trainForm.pricing.economy}
                onChange={(e) => handleInputChange('pricing.economy', e.target.value)}
                required
              />
              <Input
                label="Business Price (LKR)"
                type="number"
                value={trainForm.pricing.business}
                onChange={(e) => handleInputChange('pricing.business', e.target.value)}
                required
              />
              <Input
                label="First Class Price (LKR)"
                type="number"
                value={trainForm.pricing.first}
                onChange={(e) => handleInputChange('pricing.first', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Available Economy Seats"
                type="number"
                value={trainForm.seatInfo.availableEconomy}
                onChange={(e) => handleInputChange('seatInfo.availableEconomy', e.target.value)}
                required
              />
              <Input
                label="Available Business Seats"
                type="number"
                value={trainForm.seatInfo.availableBusiness}
                onChange={(e) => handleInputChange('seatInfo.availableBusiness', e.target.value)}
                required
              />
              <Input
                label="Available First Class Seats"
                type="number"
                value={trainForm.seatInfo.availableFirst}
                onChange={(e) => handleInputChange('seatInfo.availableFirst', e.target.value)}
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
            
            <div>
              <Select
                label="Status"
                value={trainForm.status}
                onChange={(e) => handleInputChange('status', e.target.value)}
                options={trainStatuses}
                required
              />
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
                value={trainForm.fromStation}
                onChange={(e) => handleInputChange('fromStation', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={trainForm.toStation}
                onChange={(e) => handleInputChange('toStation', e.target.value)}
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
                value={trainForm.seatInfo.totalSeats}
                onChange={(e) => handleInputChange('seatInfo.totalSeats', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Economy Price (LKR)"
                type="number"
                value={trainForm.pricing.economy}
                onChange={(e) => handleInputChange('pricing.economy', e.target.value)}
                required
              />
              <Input
                label="Business Price (LKR)"
                type="number"
                value={trainForm.pricing.business}
                onChange={(e) => handleInputChange('pricing.business', e.target.value)}
                required
              />
              <Input
                label="First Class Price (LKR)"
                type="number"
                value={trainForm.pricing.first}
                onChange={(e) => handleInputChange('pricing.first', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Input
                label="Available Economy Seats"
                type="number"
                value={trainForm.seatInfo.availableEconomy}
                onChange={(e) => handleInputChange('seatInfo.availableEconomy', e.target.value)}
                required
              />
              <Input
                label="Available Business Seats"
                type="number"
                value={trainForm.seatInfo.availableBusiness}
                onChange={(e) => handleInputChange('seatInfo.availableBusiness', e.target.value)}
                required
              />
              <Input
                label="Available First Class Seats"
                type="number"
                value={trainForm.seatInfo.availableFirst}
                onChange={(e) => handleInputChange('seatInfo.availableFirst', e.target.value)}
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
            
            <div>
              <Select
                label="Status"
                value={trainForm.status}
                onChange={(e) => handleInputChange('status', e.target.value)}
                options={trainStatuses}
                required
              />
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
