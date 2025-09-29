'use client';

import { useState, useEffect, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import Button from '../../components/ui/button';
import { Input, Select } from '../../components/ui/input';
import { StatusBadge } from '../../components/ui/badge';
import trainsData from '../../data/trains.json';
import { formatCurrency, formatTime, calculateDuration } from '../../lib/utils';

function BookingForm() {
  const searchParams = useSearchParams();
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [bookingStep, setBookingStep] = useState(1);
  const [searchForm, setSearchForm] = useState({
    from: searchParams.get('from') || '',
    to: searchParams.get('to') || '',
    date: searchParams.get('date') || '',
    adults: parseInt(searchParams.get('adults')) || 1,
    children: parseInt(searchParams.get('children')) || 0
  });
  const [passengerDetails, setPassengerDetails] = useState([]);
  const [seatClass, setSeatClass] = useState('economy');

  const stations = [
    { value: '', label: 'Select Station' },
    { value: 'colombo', label: 'Colombo Fort' },
    { value: 'kandy', label: 'Kandy' },
    { value: 'galle', label: 'Galle' },
    { value: 'nuwara-eliya', label: 'Nuwara Eliya' },
    { value: 'trincomalee', label: 'Trincomalee' },
    { value: 'jaffna', label: 'Jaffna' }
  ];

  const seatClasses = [
    { value: 'economy', label: 'Economy Class', price: 'from 150 LKR' },
    { value: 'business', label: 'Business Class', price: 'from 250 LKR' },
    { value: 'first', label: 'First Class', price: 'from 400 LKR' }
  ];

  // Initialize passenger details
  useEffect(() => {
    const totalPassengers = (parseInt(searchForm.adults) || 0) + (parseInt(searchForm.children) || 0);
    const details = Array.from({ length: totalPassengers }, (_, index) => ({
      name: '',
      age: '',
      gender: '',
      idType: '',
      idNumber: ''
    }));
    setPassengerDetails(details);
  }, [searchForm.adults, searchForm.children]);

  const handleInputChange = (field, value) => {
    setSearchForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handlePassengerChange = (index, field, value) => {
    const updated = [...passengerDetails];
    updated[index][field] = value;
    setPassengerDetails(updated);
  };

  const searchTrains = () => {
    if (searchForm.from && searchForm.to && searchForm.date) {
      setBookingStep(2);
    }
  };

  const selectTrain = (train) => {
    setSelectedTrain(train);
    setBookingStep(3);
  };

  const proceedToPayment = () => {
    if (passengerDetails.every(p => p.name && p.age && p.gender && p.idType && p.idNumber)) {
      // Redirect to payment page with booking details
      const bookingData = {
        train: selectedTrain,
        passengers: passengerDetails,
        seatClass,
        adultsCount: parseInt(searchForm.adults) || 0,
        childrenCount: parseInt(searchForm.children) || 0,
        totalAmount: (() => {
          const adultUnit = selectedTrain.price[seatClass];
          const childUnit = Math.round(adultUnit / 2);
          const numAdults = parseInt(searchForm.adults) || 0;
          const numChildren = parseInt(searchForm.children) || 0;
          return adultUnit * numAdults + childUnit * numChildren;
        })(),
        // Persist the selected date for later display/formatting
        departureDate: searchForm.date
      };
      
      // Store booking data in localStorage for payment page
      localStorage.setItem('bookingData', JSON.stringify(bookingData));
      window.location.href = '/payment';
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container-custom">
        <div className="max-w-6xl mx-auto">
          {/* Progress Steps */}
          <div className="mb-8">
            <div className="flex items-center justify-center space-x-8">
              {[1, 2, 3, 4].map((step) => (
                <div key={step} className="flex items-center">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                    bookingStep >= step 
                      ? 'bg-blue-600 text-white' 
                      : 'bg-gray-200 text-gray-600'
                  }`}>
                    {step}
                  </div>
                  {step < 4 && (
                    <div className={`w-16 h-1 ml-2 ${
                      bookingStep > step ? 'bg-blue-600' : 'bg-gray-200'
                    }`} />
                  )}
                </div>
              ))}
            </div>
            <div className="flex justify-center mt-4 space-x-16 text-sm text-gray-600">
              <span>Search</span>
              <span>Select Train</span>
              <span>Passenger Details</span>
              <span>Payment</span>
            </div>
          </div>

          {bookingStep === 1 && (
            <Card className="max-w-4xl mx-auto">
              <CardHeader>
                <CardTitle className="text-2xl text-center">Search for Trains</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                  <Select
                    label="From Station"
                    value={searchForm.from}
                    onChange={(e) => handleInputChange('from', e.target.value)}
                    options={stations}
                    required
                  />
                  <Select
                    label="To Station"
                    value={searchForm.to}
                    onChange={(e) => handleInputChange('to', e.target.value)}
                    options={stations}
                    required
                  />
                  <Input
                    label="Departure Date"
                    type="date"
                    value={searchForm.date}
                    onChange={(e) => handleInputChange('date', e.target.value)}
                    min={today}
                    required
                  />
                  <Select
                    label="Adults"
                    value={searchForm.adults}
                    onChange={(e) => handleInputChange('adults', parseInt(e.target.value))}
                    options={[
                      { value: 1, label: '1 Adult' },
                      { value: 2, label: '2 Adults' },
                      { value: 3, label: '3 Adults' },
                      { value: 4, label: '4 Adults' },
                      { value: 5, label: '5 Adults' }
                    ]}
                    required
                  />
                  <Select
                    label="Children"
                    value={searchForm.children}
                    onChange={(e) => handleInputChange('children', parseInt(e.target.value))}
                    options={[
                      { value: 0, label: '0 Children' },
                      { value: 1, label: '1 Child' },
                      { value: 2, label: '2 Children' },
                      { value: 3, label: '3 Children' },
                      { value: 4, label: '4 Children' },
                      { value: 5, label: '5 Children' }
                    ]}
                    required
                  />
                </div>
                <div className="text-center">
                  <Button onClick={searchTrains} size="lg">
                    Search Trains
                  </Button>
                </div>
              </CardContent>
            </Card>
          )}

          {bookingStep === 2 && (
            <div>
              <div className="mb-6">
                <Button variant="outline" onClick={() => setBookingStep(1)}>
                  ← Back to Search
                </Button>
              </div>
              
              <h2 className="text-2xl font-bold mb-6">Available Trains</h2>
              <div className="space-y-4">
                {trainsData.map((train) => (
                  <Card key={train.id} hover>
                    <CardContent className="p-6">
                      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                        <div className="flex-1">
                          <div className="flex items-center justify-between mb-3">
                            <h3 className="text-xl font-semibold">{train.name}</h3>
                            <StatusBadge status="active" />
                          </div>
                          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                            <div>
                              <p className="text-sm text-gray-500">Route</p>
                              <p className="font-medium">{train.route}</p>
                            </div>
                            <div>
                              <p className="text-sm text-gray-500">Duration</p>
                              <p className="font-medium">{train.duration}</p>
                            </div>
                            <div>
                              <p className="text-sm text-gray-500">Distance</p>
                              <p className="font-medium">{train.distance}</p>
                            </div>
                          </div>
                          <div className="flex flex-wrap gap-2 mb-4">
                            {train.features.map((feature, index) => (
                              <span key={index} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                                {feature}
                              </span>
                            ))}
                          </div>
                        </div>
                        <div className="lg:ml-6">
                          <div className="text-center mb-4">
                            <p className="text-sm text-gray-500">Departure</p>
                            <p className="font-semibold">{formatTime(train.departureTime)}</p>
                          </div>
                          <div className="text-center mb-4">
                            <p className="text-sm text-gray-500">Arrival</p>
                            <p className="font-semibold">{formatTime(train.arrivalTime)}</p>
                          </div>
                          <div className="text-center mb-4">
                            <p className="text-2xl font-bold text-green-600">
                              {formatCurrency(train.price.economy)}
                            </p>
                            <p className="text-sm text-gray-500">from</p>
                          </div>
                          <Button onClick={() => selectTrain(train)} className="w-full">
                            Select Train
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          )}

          {bookingStep === 3 && selectedTrain && (
            <div>
              <div className="mb-6">
                <Button variant="outline" onClick={() => setBookingStep(2)}>
                  ← Back to Trains
                </Button>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Selected Train Info */}
                <div className="lg:col-span-1">
                  <Card>
                    <CardHeader>
                      <CardTitle>Selected Train</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        <div>
                          <h3 className="font-semibold">{selectedTrain.name}</h3>
                          <p className="text-gray-600">{selectedTrain.route}</p>
                        </div>
                        <div className="grid grid-cols-2 gap-4 text-sm">
                          <div>
                            <p className="text-gray-500">Departure</p>
                            <p className="font-medium">{formatTime(selectedTrain.departureTime)}</p>
                          </div>
                          <div>
                            <p className="text-gray-500">Arrival</p>
                            <p className="font-medium">{formatTime(selectedTrain.arrivalTime)}</p>
                          </div>
                        </div>
                        <div>
                          <p className="text-gray-500">Seat Class</p>
                          <Select
                            value={seatClass}
                            onChange={(e) => setSeatClass(e.target.value)}
                            options={seatClasses}
                          />
                        </div>
                        <div className="border-t pt-4">
                          <div className="flex justify-between">
                            <span>Total Amount:</span>
                            <span className="font-bold text-lg">
                              {(() => {
                                const adultUnit = selectedTrain.price[seatClass];
                                const childUnit = Math.round(adultUnit / 2);
                                const numAdults = parseInt(searchForm.adults) || 0;
                                const numChildren = parseInt(searchForm.children) || 0;
                                const total = adultUnit * numAdults + childUnit * numChildren;
                                return formatCurrency(total);
                              })()}
                            </span>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </div>

                {/* Passenger Details Form */}
                <div className="lg:col-span-2">
                  <Card>
                    <CardHeader>
                      <CardTitle>Passenger Details</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-6">
                        {passengerDetails.map((passenger, index) => (
                          <div key={index} className="border rounded-lg p-4">
                            <h4 className="font-medium mb-4">Passenger {index + 1}</h4>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                              <Input
                                label="Full Name"
                                value={passenger.name}
                                onChange={(e) => handlePassengerChange(index, 'name', e.target.value)}
                                required
                              />
                              <Input
                                label="Age"
                                type="number"
                                value={passenger.age}
                                onChange={(e) => handlePassengerChange(index, 'age', e.target.value)}
                                min="1"
                                max="120"
                                required
                              />
                              <Select
                                label="Gender"
                                value={passenger.gender}
                                onChange={(e) => handlePassengerChange(index, 'gender', e.target.value)}
                                options={[
                                  { value: '', label: 'Select Gender' },
                                  { value: 'male', label: 'Male' },
                                  { value: 'female', label: 'Female' },
                                  { value: 'other', label: 'Other' }
                                ]}
                                required
                              />
                              <Select
                                label="ID Type"
                                value={passenger.idType}
                                onChange={(e) => handlePassengerChange(index, 'idType', e.target.value)}
                                options={[
                                  { value: '', label: 'Select ID Type' },
                                  { value: 'passport', label: 'Passport' },
                                  { value: 'driving_license', label: 'Driving License' },
                                  { value: 'national_id', label: 'National ID' }
                                ]}
                                required
                              />
                              <div className="md:col-span-2">
                                <Input
                                  label="ID Number"
                                  value={passenger.idNumber}
                                  onChange={(e) => handlePassengerChange(index, 'idNumber', e.target.value)}
                                  required
                                />
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                      
                      <div className="mt-8 text-center">
                        <Button onClick={proceedToPayment} size="lg">
                          Proceed to Payment
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default function BookingPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-gray-50 py-8 flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
        <p className="text-gray-600">Loading booking form...</p>
      </div>
    </div>}>
      <BookingForm />
    </Suspense>
  );
}
