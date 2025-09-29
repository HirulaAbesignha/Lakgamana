'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Button from './ui/button';
import { Input, Select } from './ui/input';
import trainsData from '../data/trains.json';

const Hero = () => {
  const [searchForm, setSearchForm] = useState({
    from: '',
    to: '',
    date: '',
    passengers: 1
  });
  const router = useRouter();

  const stations = [
    { value: '', label: 'Select Station' },
    { value: 'colombo', label: 'Colombo Fort' },
    { value: 'kandy', label: 'Kandy' },
    { value: 'galle', label: 'Galle' },
    { value: 'nuwara-eliya', label: 'Nuwara Eliya' },
    { value: 'trincomalee', label: 'Trincomalee' },
    { value: 'jaffna', label: 'Jaffna' }
  ];

  const handleInputChange = (field, value) => {
    setSearchForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchForm.from && searchForm.to && searchForm.date) {
      router.push(`/booking?from=${searchForm.from}&to=${searchForm.to}&date=${searchForm.date}&passengers=${searchForm.passengers}`);
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <section className="relative bg-gradient-to-br from-blue-600 via-blue-700 to-blue-800 text-white">
      {/* Background Pattern */}
      <div className="absolute inset-0 bg-black opacity-20"></div>
      <div className="absolute inset-0" style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Ccircle cx='30' cy='30' r='2'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
      }}></div>
      
      <div className="relative container-custom section-padding">
        <div className="max-w-4xl mx-auto text-center">
          {/* Hero Content */}
          <div className="animate-fade-in">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6">
              Journey Across{' '}
              <span className="text-green-400">Beautiful Sri Lanka</span>
            </h1>
            <p className="text-xl md:text-2xl text-blue-100 mb-12 max-w-3xl mx-auto">
              Book your train tickets easily and explore the stunning landscapes, 
              rich culture, and warm hospitality of Sri Lanka.
            </p>
          </div>

          {/* Search Form */}
          <div className="animate-slide-up bg-white rounded-2xl shadow-2xl p-6 md:p-8 max-w-4xl mx-auto">
            <form onSubmit={handleSearch} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {/* From Station */}
                <div>
                  <Select
                    label="From"
                    value={searchForm.from}
                    onChange={(e) => handleInputChange('from', e.target.value)}
                    options={stations}
                    required
                  />
                </div>

                {/* To Station */}
                <div>
                  <Select
                    label="To"
                    value={searchForm.to}
                    onChange={(e) => handleInputChange('to', e.target.value)}
                    options={stations}
                    required
                  />
                </div>

                {/* Date */}
                <div>
                  <Input
                    label="Departure Date"
                    type="date"
                    value={searchForm.date}
                    onChange={(e) => handleInputChange('date', e.target.value)}
                    min={today}
                    required
                  />
                </div>

                {/* Passengers */}
                <div>
                  <Select
                    label="Passengers"
                    value={searchForm.passengers}
                    onChange={(e) => handleInputChange('passengers', parseInt(e.target.value))}
                    options={[
                      { value: 1, label: '1 Passenger' },
                      { value: 2, label: '2 Passengers' },
                      { value: 3, label: '3 Passengers' },
                      { value: 4, label: '4 Passengers' },
                      { value: 5, label: '5 Passengers' }
                    ]}
                    required
                  />
                </div>
              </div>

              <Button
                type="submit"
                variant="primary"
                size="lg"
                className="w-full md:w-auto md:px-12"
              >
                Search Trains
              </Button>
            </form>
          </div>

          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-12">
            <div className="animate-bounce-in">
              <div className="text-3xl font-bold text-green-400 mb-2">50+</div>
              <div className="text-blue-100">Daily Routes</div>
            </div>
            <div className="animate-bounce-in" style={{ animationDelay: '0.1s' }}>
              <div className="text-3xl font-bold text-green-400 mb-2">10K+</div>
              <div className="text-blue-100">Happy Customers</div>
            </div>
            <div className="animate-bounce-in" style={{ animationDelay: '0.2s' }}>
              <div className="text-3xl font-bold text-green-400 mb-2">99%</div>
              <div className="text-blue-100">On-Time Performance</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export { Hero };
