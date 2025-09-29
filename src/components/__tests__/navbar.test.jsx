import { render, screen } from "@testing-library/react";
import { Navbar } from "../../components/navbar";

describe("Navbar", () => {
	it("renders brand and links", () => {
		render(<Navbar />);
		expect(screen.getByText(/Lakgamana/i)).toBeInTheDocument();
		expect(screen.getByRole("link", { name: /Home/i })).toBeInTheDocument();
		expect(screen.getByRole("button", { name: /Login/i })).toBeInTheDocument();
	});
});

