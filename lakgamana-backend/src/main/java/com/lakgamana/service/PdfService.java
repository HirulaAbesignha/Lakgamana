package com.lakgamana.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.Passenger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PdfService.class);

    public byte[] generateBookingPdf(Booking booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            Paragraph title = new Paragraph("TRAIN BOOKING TICKET")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();
            document.add(title);

            document.add(new Paragraph("\n"));

            // Booking Details Section
            Paragraph bookingHeader = new Paragraph("BOOKING DETAILS")
                    .setBold()
                    .setFontSize(14);
            document.add(bookingHeader);

            Table bookingTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            bookingTable.addCell(new Cell().add(new Paragraph("Booking ID:").setBold()));
            bookingTable.addCell(new Cell().add(new Paragraph(booking.getBookingId())));
            
            bookingTable.addCell(new Cell().add(new Paragraph("Booking Date:").setBold()));
            bookingTable.addCell(new Cell().add(new Paragraph(booking.getBookingDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))));
            
            bookingTable.addCell(new Cell().add(new Paragraph("Status:").setBold()));
            bookingTable.addCell(new Cell().add(new Paragraph(booking.getStatus().toString())));

            document.add(bookingTable);
            document.add(new Paragraph("\n"));

            // Train Details Section
            Paragraph trainHeader = new Paragraph("TRAIN DETAILS")
                    .setBold()
                    .setFontSize(14);
            document.add(trainHeader);

            Table trainTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            trainTable.addCell(new Cell().add(new Paragraph("Train Name:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getTrain().getName())));
            
            trainTable.addCell(new Cell().add(new Paragraph("Route:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getTrain().getRoute())));
            
            trainTable.addCell(new Cell().add(new Paragraph("Departure Date:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getDepartureDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
            
            trainTable.addCell(new Cell().add(new Paragraph("Departure Time:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getDepartureTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")))));
            
            trainTable.addCell(new Cell().add(new Paragraph("Arrival Time:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getArrivalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")))));
            
            trainTable.addCell(new Cell().add(new Paragraph("Seat Class:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getSeatClass())));
            
            trainTable.addCell(new Cell().add(new Paragraph("Seat Number:").setBold()));
            trainTable.addCell(new Cell().add(new Paragraph(booking.getSeatNumber())));

            document.add(trainTable);
            document.add(new Paragraph("\n"));

            // Passenger Details Section
            Paragraph passengerHeader = new Paragraph("PASSENGER DETAILS")
                    .setBold()
                    .setFontSize(14);
            document.add(passengerHeader);

            Table passengerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Headers
            passengerTable.addCell(new Cell().add(new Paragraph("Name").setBold()));
            passengerTable.addCell(new Cell().add(new Paragraph("Age").setBold()));
            passengerTable.addCell(new Cell().add(new Paragraph("Gender").setBold()));
            passengerTable.addCell(new Cell().add(new Paragraph("ID Type").setBold()));
            passengerTable.addCell(new Cell().add(new Paragraph("ID Number").setBold()));

            // Passenger data
            for (Passenger passenger : booking.getPassengers()) {
                passengerTable.addCell(new Cell().add(new Paragraph(passenger.getName())));
                passengerTable.addCell(new Cell().add(new Paragraph(passenger.getAge().toString())));
                passengerTable.addCell(new Cell().add(new Paragraph(passenger.getGender().toString())));
                passengerTable.addCell(new Cell().add(new Paragraph(passenger.getIdType().toString())));
                passengerTable.addCell(new Cell().add(new Paragraph(passenger.getIdNumber())));
            }

            document.add(passengerTable);
            document.add(new Paragraph("\n"));

            // Payment Details Section
            Paragraph paymentHeader = new Paragraph("PAYMENT DETAILS")
                    .setBold()
                    .setFontSize(14);
            document.add(paymentHeader);

            Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            paymentTable.addCell(new Cell().add(new Paragraph("Total Amount:").setBold()));
            paymentTable.addCell(new Cell().add(new Paragraph("LKR " + booking.getTotalAmount())));
            
            paymentTable.addCell(new Cell().add(new Paragraph("Passengers:").setBold()));
            paymentTable.addCell(new Cell().add(new Paragraph(String.valueOf(booking.getPassengers().size()))));

            document.add(paymentTable);
            document.add(new Paragraph("\n"));

            // Footer
            Paragraph footer = new Paragraph("Thank you for choosing Lakgamana Train Services!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .setFontSize(12);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF for booking: {}", booking.getBookingId(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }
}
