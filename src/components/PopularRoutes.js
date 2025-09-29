'use client';

import { Card, CardContent } from './ui/card';
import Button from './ui/button';
import { useRouter } from 'next/navigation';
import { formatCurrency } from '../lib/utils';

const PopularRoutes = () => {
  const router = useRouter();
  
  const popularRoutes = [
    {
      id: 'T001',
      name: 'Express Train',
      route: 'Colombo - Kandy',
      from: 'Colombo Fort',
      to: 'Kandy',
      departureTime: '08:00',
      arrivalTime: '10:30',
      duration: '2h 30m',
      price: 150,
      image: '/api/placeholder/300/200',
      features: ['AC', 'WiFi', 'Food Service'],
      rating: 4.8
    },
    {
      id: 'T002',
      name: 'Intercity Express',
      route: 'Colombo - Galle',
      from: 'Colombo Fort',
      to: 'Galle',
      departureTime: '09:30',
      arrivalTime: '11:45',
      duration: '2h 15m',
      price: 120,
      image: '/api/placeholder/300/200',
      features: ['AC', 'WiFi'],
      rating: 4.6
    },
    {
      id: 'T003',
      name: 'Mountain Express',
      route: 'Kandy - Nuwara Eliya',
      from: 'Kandy',
      to: 'Nuwara Eliya',
      departureTime: '07:00',
      arrivalTime: '10:00',
      duration: '3h 00m',
      price: 100,
      image: '/api/placeholder/300/200',
      features: ['Scenic Views', 'AC'],
      rating: 4.9
    }
  ];

  const handleBookNow = (trainId) => {
    router.push(`/booking?train=${trainId}`);
  };

  return (
    <section className="section-padding">
      <div className="container-custom">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            Popular Routes
          </h2>
          <p className="text-base text-gray-600 max-w-3xl mx-auto">
            Discover our most popular train routes and experience the beauty of Sri Lanka 
            with comfortable and reliable train services.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {popularRoutes.map((route, index) => (
            <Card 
              key={route.id} 
              hover 
              className="overflow-hidden"
              style={{ animationDelay: `${index * 0.1}s` }}
            >
              <div className="bg-[url('/train-1.png')] bg-cover bg-center bg-no-repeat text-white h-40">
              </div>
              
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-xl font-semibold text-gray-900">
                    {route.name}
                  </h3>
                  <div className="flex items-center text-yellow-500">
                    <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                    <span className="text-sm font-medium">{route.rating}</span>
                  </div>
                </div>

                <div className="mb-4">
                  <p className="text-lg font-medium text-gray-700 mb-1">
                    {route.route}
                  </p>
                  <div className="flex items-center text-sm text-gray-500 mb-2">
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>{route.departureTime} - {route.arrivalTime} ({route.duration})</span>
                  </div>
                </div>

                <div className="mb-4">
                  <div className="flex flex-wrap gap-2">
                    {route.features.map((feature, idx) => (
                      <span 
                        key={idx}
                        className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full"
                      >
                        {feature}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <span className="text-2xl font-bold text-green-600">
                      {formatCurrency(route.price)}
                    </span>
                    <span className="text-sm text-gray-500 ml-1">from</span>
                  </div>
                  <Button 
                    variant="primary" 
                    size="sm"
                    onClick={() => handleBookNow(route.id)}
                  >
                    Book Now
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="text-center mt-12">
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push('/booking')}
          >
            View All Routes
          </Button>
        </div>
      </div>
    </section>
  );
};

export { PopularRoutes };
