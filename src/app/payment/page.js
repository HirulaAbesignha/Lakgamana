'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import Button from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { StatusBadge } from '../../components/ui/badge';
import { formatCurrency, formatDate, formatTime } from '../../lib/utils';

export default function PaymentPage() {
  const router = useRouter();
  const [bookingData, setBookingData] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('credit_card');
  const [paymentForm, setPaymentForm] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardHolderName: '',
    upiId: '',
    walletProvider: ''
  });
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);

  useEffect(() => {
    const storedBookingData = localStorage.getItem('bookingData');
    if (storedBookingData) {
      setBookingData(JSON.parse(storedBookingData));
    } else {
      router.push('/booking');
    }
  }, [router]);

  const handleInputChange = (field, value) => {
    setPaymentForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const formatCardNumber = (value) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const matches = v.match(/\d{4,16}/g);
    const match = matches && matches[0] || '';
    const parts = [];
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4));
    }
    if (parts.length) {
      return parts.join(' ');
    } else {
      return v;
    }
  };

  const formatExpiryDate = (value) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (v.length >= 2) {
      return v.substring(0, 2) + '/' + v.substring(2, 4);
    }
    return v;
  };

  const handlePayment = async () => {
    setIsProcessing(true);
    
    // Simulate payment processing
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    setIsProcessing(false);
    setPaymentSuccess(true);
    
    // Clear booking data after successful payment
    localStorage.removeItem('bookingData');
  };

  if (paymentSuccess) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="container-custom">
          <div className="max-w-2xl mx-auto">
            <Card className="text-center">
              <CardContent className="p-12">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                  <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-gray-900 mb-4">Payment Successful!</h2>
                <p className="text-gray-600 mb-6">
                  Your train booking has been confirmed. You will receive a confirmation email shortly.
                </p>
                <div className="space-y-3">
                  <Button onClick={() => router.push('/tickets')} className="w-full">
                    View My Tickets
                  </Button>
                  <Button variant="outline" onClick={() => router.push('/')} className="w-full">
                    Back to Home
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    );
  }

  if (!bookingData) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="container-custom">
          <div className="max-w-2xl mx-auto">
            <Card className="text-center">
              <CardContent className="p-12">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">No Booking Data Found</h2>
                <p className="text-gray-600 mb-6">
                  Please complete your booking first.
                </p>
                <Button onClick={() => router.push('/booking')}>
                  Go to Booking
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    );
  }

  const paymentMethods = [
    {
      id: 'credit_card',
      name: 'Credit Card',
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
        </svg>
      )
    },
    {
      id: 'upi',
      name: 'UPI',
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
        </svg>
      )
    },
    {
      id: 'wallet',
      name: 'Digital Wallet',
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
      )
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container-custom">
        <div className="max-w-4xl mx-auto">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Complete Your Payment</h1>
            <p className="text-gray-600">Secure payment processing for your train booking</p>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Booking Summary */}
            <div className="lg:col-span-1">
              <Card>
                <CardHeader>
                  <CardTitle>Booking Summary</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div>
                      <h3 className="font-semibold">{bookingData.train.name}</h3>
                      <p className="text-gray-600">{bookingData.train.route}</p>
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4 text-sm">
                      <div>
                        <p className="text-gray-500">Date</p>
                        <p className="font-medium">{formatDate(bookingData.departureDate)}</p>
                      </div>
                      <div>
                        <p className="text-gray-500">Time</p>
                        <p className="font-medium">
                          {formatTime(bookingData.train.departureTime)} - {formatTime(bookingData.train.arrivalTime)}
                        </p>
                      </div>
                    </div>

                    <div>
                      <p className="text-gray-500">Passengers</p>
                      <div className="flex flex-wrap gap-1 mt-1">
                        {bookingData.passengers.map((passenger, index) => (
                          <span key={index} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                            {passenger.name}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div className="border-t pt-4">
                      <div className="space-y-2">
                        <div className="flex justify-between text-sm">
                          <span>Base Fare ({bookingData.passengers.length} × {formatCurrency(bookingData.train.price[bookingData.seatClass])})</span>
                          <span>{formatCurrency(bookingData.train.price[bookingData.seatClass] * bookingData.passengers.length)}</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span>Service Fee</span>
                          <span>{formatCurrency(25)}</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span>Taxes</span>
                          <span>{formatCurrency(15)}</span>
                        </div>
                        <div className="border-t pt-2">
                          <div className="flex justify-between font-semibold">
                            <span>Total Amount</span>
                            <span className="text-lg">{formatCurrency(bookingData.totalAmount + 40)}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Payment Form */}
            <div className="lg:col-span-2">
              <Card>
                <CardHeader>
                  <CardTitle>Payment Details</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-6">
                    {/* Payment Method Selection */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-3">
                        Select Payment Method
                      </label>
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {paymentMethods.map((method) => (
                          <div
                            key={method.id}
                            className={`border-2 rounded-lg p-4 cursor-pointer transition-colors ${
                              paymentMethod === method.id
                                ? 'border-blue-500 bg-blue-50'
                                : 'border-gray-200 hover:border-gray-300'
                            }`}
                            onClick={() => setPaymentMethod(method.id)}
                          >
                            <div className="flex items-center space-x-3">
                              <div className={`p-2 rounded-lg ${
                                paymentMethod === method.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'
                              }`}>
                                {method.icon}
                              </div>
                              <span className="font-medium">{method.name}</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Payment Form Fields */}
                    {paymentMethod === 'credit_card' && (
                      <div className="space-y-4">
                        <Input
                          label="Card Number"
                          placeholder="1234 5678 9012 3456"
                          value={paymentForm.cardNumber}
                          onChange={(e) => handleInputChange('cardNumber', formatCardNumber(e.target.value))}
                          maxLength="19"
                          required
                        />
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          <Input
                            label="Expiry Date"
                            placeholder="MM/YY"
                            value={paymentForm.expiryDate}
                            onChange={(e) => handleInputChange('expiryDate', formatExpiryDate(e.target.value))}
                            maxLength="5"
                            required
                          />
                          <Input
                            label="CVV"
                            placeholder="123"
                            value={paymentForm.cvv}
                            onChange={(e) => handleInputChange('cvv', e.target.value.replace(/\D/g, '').slice(0, 4))}
                            maxLength="4"
                            required
                          />
                        </div>
                        <Input
                          label="Card Holder Name"
                          placeholder="John Doe"
                          value={paymentForm.cardHolderName}
                          onChange={(e) => handleInputChange('cardHolderName', e.target.value)}
                          required
                        />
                      </div>
                    )}

                    {paymentMethod === 'upi' && (
                      <div>
                        <Input
                          label="UPI ID"
                          placeholder="yourname@paytm"
                          value={paymentForm.upiId}
                          onChange={(e) => handleInputChange('upiId', e.target.value)}
                          required
                        />
                      </div>
                    )}

                    {paymentMethod === 'wallet' && (
                      <div>
                        <select
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          value={paymentForm.walletProvider}
                          onChange={(e) => handleInputChange('walletProvider', e.target.value)}
                          required
                        >
                          <option value="">Select Wallet Provider</option>
                          <option value="paypal">PayPal</option>
                          <option value="google_pay">Google Pay</option>
                          <option value="apple_pay">Apple Pay</option>
                          <option value="amazon_pay">Amazon Pay</option>
                        </select>
                      </div>
                    )}

                    {/* Security Notice */}
                    <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                      <div className="flex">
                        <svg className="w-5 h-5 text-green-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                        </svg>
                        <div>
                          <p className="text-sm text-green-800">
                            <strong>Secure Payment:</strong> Your payment information is encrypted and secure. 
                            We use industry-standard SSL encryption to protect your data.
                          </p>
                        </div>
                      </div>
                    </div>

                    {/* Payment Button */}
                    <Button
                      onClick={handlePayment}
                      disabled={isProcessing}
                      loading={isProcessing}
                      className="w-full"
                      size="lg"
                    >
                      {isProcessing ? 'Processing Payment...' : `Pay ${formatCurrency(bookingData.totalAmount + 40)}`}
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
