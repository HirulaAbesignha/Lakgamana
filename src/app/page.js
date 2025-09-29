'use client';

import { Hero } from "../components/Hero";
import { Features } from "../components/Features";
import { PopularRoutes } from "../components/PopularRoutes";
import { useState } from 'react';

export default function HomePage() {
	const [showDemo, setShowDemo] = useState(false);

	return (
		<div className="min-h-screen">
			<Hero />
			<Features />
			<PopularRoutes />
			
			{/* Demo Login Section */}
			<div className="bg-gray-50 py-16">
				<div className="container-custom">
					<div className="text-center mb-8">
						<h2 className="text-3xl font-bold text-gray-900 mb-4">
							Try the Demo
						</h2>
						<p className="text-lg text-gray-600 mb-6">
							Test the authentication system with demo accounts
						</p>
						<button
							onClick={() => setShowDemo(!showDemo)}
							className="bg-blue-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-blue-700 transition-colors"
						>
							{showDemo ? 'Hide Demo' : 'Show Demo Accounts'}
						</button>
					</div>
					
					{showDemo && (
						<div className="max-w-4xl mx-auto">
							<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
								<div className="bg-white rounded-lg shadow-md p-6">
									<div className="flex items-center mb-4">
										<div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mr-4">
											<svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
												<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
											</svg>
										</div>
										<div>
											<h3 className="text-lg font-semibold text-gray-900">Regular User</h3>
											<p className="text-sm text-gray-500">Book tickets and manage reservations</p>
										</div>
									</div>
									<div className="space-y-2 text-sm">
										<p><strong>Email:</strong> user@lakgamana.com</p>
										<p><strong>Password:</strong> password123</p>
									</div>
									<div className="mt-4">
										<a
											href="/login"
											className="inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
										>
											Login as User
											<svg className="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
												<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
											</svg>
										</a>
									</div>
								</div>
								
								<div className="bg-white rounded-lg shadow-md p-6">
									<div className="flex items-center mb-4">
										<div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
											<svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
												<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
											</svg>
										</div>
										<div>
											<h3 className="text-lg font-semibold text-gray-900">Administrator</h3>
											<p className="text-sm text-gray-500">Manage trains, users, and system</p>
										</div>
									</div>
									<div className="space-y-2 text-sm">
										<p><strong>Email:</strong> admin@lakgamana.com</p>
										<p><strong>Password:</strong> admin123</p>
									</div>
									<div className="mt-4">
										<a
											href="/login"
											className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
										>
											Login as Admin
											<svg className="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
												<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
											</svg>
										</a>
									</div>
								</div>
							</div>
						</div>
					)}
				</div>
			</div>
		</div>
	);
}