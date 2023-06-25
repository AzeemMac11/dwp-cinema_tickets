package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Calculate the amount and reserve seats based on ticket type and quantity
        int infantCount = ticketTypeRequest.getInfantCount();
        int childCount = ticketTypeRequest.getChildCount();
        int adultCount = ticketTypeRequest.getAdultCount();

        // Validate the ticket purchase request
        if (isValidPurchaseRequest(infantCount, childCount, adultCount)) {
            // Calculate the total amount
            int totalAmount = calculateTotalAmount(childCount, adultCount);

            // Reserve seats
            int numberOfSeats = calculateNumberOfSeats(infantCount, childCount, adultCount);

            // Make a payment request to the TicketPaymentService
            paymentService.makePayment(accountId, totalAmount);

            // Make a seat reservation request to the SeatReservationService
            reservationService.reserveSeats(accountId, numberOfSeats);
        } else {
            throw new IllegalArgumentException("Invalid ticket purchase request.");
        }
    }

    private boolean isValidPurchaseRequest(int infantCount, int childCount, int adultCount) {
        // Check if the total ticket count exceeds the maximum limit
        int totalTicketCount = infantCount + childCount + adultCount;
        if (totalTicketCount > 20) {
            return false;
        }

        // Check if child or infant tickets are purchased without an adult ticket
        if ((childCount > 0 || infantCount > 0) && adultCount == 0) {
            return false;
        }

        return true;
    }

    private int calculateTotalAmount(int childCount, int adultCount) {
        int childTicketPrice = 10;
        int adultTicketPrice = 20;

        return (childTicketPrice * childCount) + (adultTicketPrice * adultCount);
    }

    private int calculateNumberOfSeats(int infantCount, int childCount, int adultCount) {
        // Reserve one seat for each adult and child ticket
        int reservedSeats = childCount + adultCount;

        // Infants do not require a seat reservation
        return reservedSeats;
    }

}
