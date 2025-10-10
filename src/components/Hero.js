'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Button from './ui/button';
import { Input, Select } from './ui/input';

const Hero = () => {
  const [searchForm, setSearchForm] = useState({
    from: '',
    to: '',
    date: '',
    adults: 1,
    children: 0
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
      router.push(`/booking?from=${searchForm.from}&to=${searchForm.to}&date=${searchForm.date}&adults=${searchForm.adults}&children=${searchForm.children}`);
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <section className="relative bg-[url('/train.png')] bg-cover bg-center bg-no-repeat text-white">
      {/* Background Pattern */}
      <div className="absolute inset-0 bg-black opacity-40"></div>
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
            <p className="text-xl md:text-lg text-white-100 mb-12 max-w-3xl mx-auto">
              Book your train tickets easily and explore the stunning landscapes, 
              rich culture, and warm hospitality of Sri Lanka.
            </p>
          </div>

          {/* Search Form */}
          <div className="animate-slide-up bg-white/90 rounded-2xl shadow-2xl p-6 md:p-8 max-w-4xl mx-auto text-black">
            <form onSubmit={handleSearch} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
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

                {/* Adults */}
                <div>
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
                </div>
                {/* Children */}
                <div>
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
              <div className="text-3xl font-bold text-white-400 mb-2">50+</div>
              <div className="text-white-100">Daily Routes</div>
            </div>
            <div className="animate-bounce-in" style={{ animationDelay: '0.1s' }}>
              <div className="text-3xl font-bold text-white-400 mb-2">10K+</div>
              <div className="text-white-100">Happy Customers</div>
            </div>
            <div className="animate-bounce-in" style={{ animationDelay: '0.2s' }}>
              <div className="text-3xl font-bold text-white-400 mb-2">99%</div>
              <div className="text-white-100">On-Time Performance</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export { Hero };
