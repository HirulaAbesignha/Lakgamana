'use client';

import { useState } from 'react';

export default function TestLoginPage() {
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(false);

  const testLogin = async () => {
    setLoading(true);
    setResult('Testing...');
    
    try {
      console.log('Starting login test...');
      
      const response = await fetch('http://localhost:8081/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: 'admin@lakgamana.com',
          password: 'Admin123!'
        }),
      });

      console.log('Response status:', response.status);
      console.log('Response ok:', response.ok);
      
      const data = await response.json();
      console.log('Response data:', data);

      if (response.ok) {
        setResult(`✅ SUCCESS: ${JSON.stringify(data, null, 2)}`);
      } else {
        setResult(`❌ FAILED: ${JSON.stringify(data, null, 2)}`);
      }
    } catch (error) {
      console.error('Error:', error);
      setResult(`❌ ERROR: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-lg">
        <h1 className="text-2xl font-bold mb-4">Login Test</h1>
        
        <button 
          onClick={testLogin}
          disabled={loading}
          className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? 'Testing...' : 'Test Login'}
        </button>
        
        <div className="mt-4">
          <h3 className="font-semibold mb-2">Result:</h3>
          <pre className="bg-gray-100 p-4 rounded text-sm overflow-auto max-h-96">
            {result}
          </pre>
        </div>
        
        <div className="mt-4 text-sm text-gray-600">
          <p><strong>Testing:</strong> admin@lakgamana.com / Admin123!</p>
          <p><strong>Endpoint:</strong> http://localhost:8081/auth/login</p>
          <p><strong>Check browser console for detailed logs</strong></p>
        </div>
      </div>
    </div>
  );
}
