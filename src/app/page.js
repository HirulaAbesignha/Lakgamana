'use client';

import { Hero } from "../components/Hero";
import { Features } from "../components/Features";
import { PopularRoutes } from "../components/PopularRoutes";

export default function HomePage() {
	return (
		<div className="min-h-screen">
			<Hero />
			<Features />
			<PopularRoutes />
		</div>
	);
}