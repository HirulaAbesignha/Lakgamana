import "./globals.css";
import { Navbar } from "../components/Navbar";
import { Footer } from "../components/Footer";

export const metadata = {
	title: "Lakgamana - Train Reservations",
	description: "Modern, reliable train booking system for Sri Lanka. Book your tickets easily and enjoy comfortable journeys across the country.",
};

export default function RootLayout({ children }) {
	return (
		<html lang="en" suppressHydrationWarning>
			<body className="antialiased">
				<div className="min-h-screen flex flex-col">
					<Navbar />
					<main className="flex-1">{children}</main>
					<Footer />
				</div>
			</body>
		</html>
	);
}