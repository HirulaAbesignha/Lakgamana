'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/card';
import Button from '../../../components/ui/button';
import { Input, TextArea } from '../../../components/ui/input';
import { StatusBadge } from '../../../components/ui/badge';
import { Modal, ModalHeader, ModalBody, ModalFooter } from '../../../components/ui/modal';
import { useEffect } from 'react';

export default function AdminRoutesPage() {
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedRoute, setSelectedRoute] = useState(null);
  const [routeForm, setRouteForm] = useState({
    name: '',
    from: '',
    to: '',
    distance: '',
    duration: '',
    status: 'active',
    schedule: {
      monday: true,
      tuesday: true,
      wednesday: true,
      thursday: true,
      friday: true,
      saturday: true,
      sunday: true
    }
  });

  const handleInputChange = (field, value) => {
    if (field.includes('.')) {
      const [parent, child] = field.split('.');
      setRouteForm(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: value
        }
      }));
    } else {
      setRouteForm(prev => ({
        ...prev,
        [field]: value
      }));
    }
  };

  const handleScheduleToggle = (day) => {
    setRouteForm(prev => ({
      ...prev,
      schedule: {
        ...prev.schedule,
        [day]: !prev.schedule[day]
      }
    }));
  };

  const fetchRoutes = async () => {
    try {
      setLoading(true);
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      const res = await fetch('/backend/routes', {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        }
      });
      if (!res.ok) throw new Error('Failed to load routes');
      const data = await res.json();
      const list = Array.isArray(data.data) ? data.data : data.data?.content || [];
      setRoutes(list);
      setError(null);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchRoutes(); }, []);

  const openAddModal = () => {
    setRouteForm({
      name: '',
      from: '',
      to: '',
      distance: '',
      duration: '',
      status: 'active',
      schedule: {
        monday: true,
        tuesday: true,
        wednesday: true,
        thursday: true,
        friday: true,
        saturday: true,
        sunday: true
      }
    });
    setIsAddModalOpen(true);
  };

  const openEditModal = (route) => {
    setSelectedRoute(route);
    setRouteForm({
      name: route.name,
      from: route.from,
      to: route.to,
      distance: route.distance,
      duration: route.duration,
      status: route.status,
      schedule: route.schedule
    });
    setIsEditModalOpen(true);
  };

  const openDeleteModal = (route) => {
    setSelectedRoute(route);
    setIsDeleteModalOpen(true);
  };

  const handleAddRoute = async () => {
    try {
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      const body = {
        name: routeForm.name,
        fromStation: routeForm.from,
        toStation: routeForm.to,
        distance: routeForm.distance,
        duration: routeForm.duration,
        status: routeForm.status?.toUpperCase() === 'INACTIVE' ? 'INACTIVE' : 'ACTIVE',
        schedule: {
          monday: !!routeForm.schedule.monday,
          tuesday: !!routeForm.schedule.tuesday,
          wednesday: !!routeForm.schedule.wednesday,
          thursday: !!routeForm.schedule.thursday,
          friday: !!routeForm.schedule.friday,
          saturday: !!routeForm.schedule.saturday,
          sunday: !!routeForm.schedule.sunday,
        }
      };
      const res = await fetch('/backend/routes', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        },
        body: JSON.stringify(body)
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || 'Failed to create route');
      }
      setIsAddModalOpen(false);
      await fetchRoutes();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleEditRoute = async () => {
    try {
      if (!selectedRoute) return;
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      const body = {
        name: routeForm.name,
        fromStation: routeForm.from,
        toStation: routeForm.to,
        distance: routeForm.distance,
        duration: routeForm.duration,
        status: routeForm.status?.toUpperCase() === 'INACTIVE' ? 'INACTIVE' : 'ACTIVE',
        schedule: routeForm.schedule
      };
      const res = await fetch(`/backend/routes/${selectedRoute.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authData.token}`
        },
        body: JSON.stringify(body)
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || 'Failed to update route');
      }
      setIsEditModalOpen(false);
      setSelectedRoute(null);
      await fetchRoutes();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDeleteRoute = async () => {
    try {
      if (!selectedRoute) return;
      const authData = JSON.parse(localStorage.getItem('lak_auth') || '{}');
      const res = await fetch(`/backend/routes/${selectedRoute.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${authData.token}`
        }
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || 'Failed to delete route');
      }
      setIsDeleteModalOpen(false);
      setSelectedRoute(null);
      await fetchRoutes();
    } catch (e) {
      setError(e.message);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'active': return 'success';
      case 'inactive': return 'error';
      default: return 'default';
    }
  };

  const days = [
    { key: 'monday', label: 'Monday' },
    { key: 'tuesday', label: 'Tuesday' },
    { key: 'wednesday', label: 'Wednesday' },
    { key: 'thursday', label: 'Thursday' },
    { key: 'friday', label: 'Friday' },
    { key: 'saturday', label: 'Saturday' },
    { key: 'sunday', label: 'Sunday' }
  ];

  return (
    <div className="space-y-6">
      {error && (
        <div className="bg-red-50 border border-red-200 rounded p-3 text-red-700">{error}</div>
      )}
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Routes Management</h1>
          <p className="text-gray-600 mt-2">Manage train routes and schedules</p>
        </div>
        <Button onClick={openAddModal}>
          Add New Route
        </Button>
      </div>

      {/* Routes Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Routes</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            {loading ? (
              <div className="p-6 text-gray-600">Loading routes...</div>
            ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Route</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">From - To</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Distance</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Duration</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Schedule</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Status</th>
                  <th className="text-left py-3 px-4 font-medium text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody>
                {routes.map((route) => (
                  <tr key={route.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4">
                      <div>
                        <p className="font-medium text-gray-900">{route.name}</p>
                        <p className="text-sm text-gray-500">ID: {route.id}</p>
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{route.fromStation} → {route.toStation}</p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{route.distance}</p>
                    </td>
                    <td className="py-3 px-4">
                      <p className="text-gray-900">{route.duration}</p>
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex space-x-1">
                        {days.map(day => (
                          <span
                            key={day.key}
                            className={`w-6 h-6 rounded-full text-xs flex items-center justify-center ${
                              route.schedule?.[day.key] 
                                ? 'bg-green-100 text-green-800' 
                                : 'bg-gray-100 text-gray-400'
                            }`}
                            title={day.label}
                          >
                            {day.label.charAt(0)}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <StatusBadge status={getStatusColor(String(route.status).toLowerCase())} />
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex space-x-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => openEditModal(route)}
                        >
                          Edit
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => openDeleteModal(route)}
                        >
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Add Route Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Add New Route"
        size="lg"
      >
        <ModalBody>
          <div className="space-y-4">
            <Input
              label="Route Name"
              placeholder="Colombo-Kandy Route"
              value={routeForm.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              required
            />
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="From Station"
                value={routeForm.from}
                onChange={(e) => handleInputChange('from', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={routeForm.to}
                onChange={(e) => handleInputChange('to', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Distance"
                placeholder="120 km"
                value={routeForm.distance}
                onChange={(e) => handleInputChange('distance', e.target.value)}
                required
              />
              <Input
                label="Duration"
                placeholder="2h 30m"
                value={routeForm.duration}
                onChange={(e) => handleInputChange('duration', e.target.value)}
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Schedule</label>
              <div className="grid grid-cols-7 gap-2">
                {days.map(day => (
                  <button
                    key={day.key}
                    type="button"
                    onClick={() => handleScheduleToggle(day.key)}
                    className={`p-2 rounded-lg text-sm font-medium transition-colors ${
                      routeForm.schedule[day.key]
                        ? 'bg-blue-100 text-blue-800 border border-blue-200'
                        : 'bg-gray-100 text-gray-600 border border-gray-200 hover:bg-gray-200'
                    }`}
                  >
                    {day.label.charAt(0)}
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
          <Button onClick={handleAddRoute}>
            Add Route
          </Button>
        </ModalFooter>
      </Modal>

      {/* Edit Route Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title="Edit Route"
        size="lg"
      >
        <ModalBody>
          <div className="space-y-4">
            <Input
              label="Route Name"
              value={routeForm.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              required
            />
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="From Station"
                value={routeForm.from}
                onChange={(e) => handleInputChange('from', e.target.value)}
                required
              />
              <Input
                label="To Station"
                value={routeForm.to}
                onChange={(e) => handleInputChange('to', e.target.value)}
                required
              />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Distance"
                value={routeForm.distance}
                onChange={(e) => handleInputChange('distance', e.target.value)}
                required
              />
              <Input
                label="Duration"
                value={routeForm.duration}
                onChange={(e) => handleInputChange('duration', e.target.value)}
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Schedule</label>
              <div className="grid grid-cols-7 gap-2">
                {days.map(day => (
                  <button
                    key={day.key}
                    type="button"
                    onClick={() => handleScheduleToggle(day.key)}
                    className={`p-2 rounded-lg text-sm font-medium transition-colors ${
                      routeForm.schedule[day.key]
                        ? 'bg-blue-100 text-blue-800 border border-blue-200'
                        : 'bg-gray-100 text-gray-600 border border-gray-200 hover:bg-gray-200'
                    }`}
                  >
                    {day.label.charAt(0)}
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
          <Button onClick={handleEditRoute}>
            Save Changes
          </Button>
        </ModalFooter>
      </Modal>

      {/* Delete Route Modal */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Delete Route"
        size="md"
      >
        <ModalBody>
          {selectedRoute && (
            <div>
              <p className="text-gray-600 mb-4">
                Are you sure you want to delete route <strong>{selectedRoute.name}</strong>?
              </p>
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <div className="flex">
                  <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                  </svg>
                  <div>
                    <p className="text-sm text-red-800">
                      <strong>Warning:</strong> This action cannot be undone. All associated trains and bookings will be affected.
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
          <Button variant="danger" onClick={handleDeleteRoute}>
            Delete Route
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}