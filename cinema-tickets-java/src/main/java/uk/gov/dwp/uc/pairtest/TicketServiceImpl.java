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
    public void purchaseTickets(Long accountId, TicketTypeRequest...ticketTypeRequests) throws InvalidPurchaseException {
        int infantCount = 0;
        int childCount = 0;
        int adultCount = 0;

        // Calculate the counts based on the ticket type
        for (TicketTypeRequest request : ticketTypeRequests) {
            int noOfTickets = request.getNoOfTickets();
            TicketTypeRequest.Type ticketType = request.getTicketType();

            if (ticketType == TicketTypeRequest.Type.INFANT) {
                infantCount += noOfTickets;
            } else if (ticketType == TicketTypeRequest.Type.CHILD) {
                childCount += noOfTickets;
            } else if (ticketType == TicketTypeRequest.Type.ADULT) {
                adultCount += noOfTickets;
            }
        }

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

    public interface TicketPaymentService {
    void makePayment(Long accountId, int amount);
}

public interface SeatReservationService {
    void reserveSeats(Long accountId, int numberOfSeats);
}

public class TicketPaymentServiceImpl implements TicketPaymentService {
    @Override
    public void makePayment(Long accountId, int amount) {
        // Assume the payment is successful without any defects
        // Implement the logic to make the payment using an external payment provider
        // For the purpose of this example, let's just print a message
        System.out.println("Payment of amount £" + amount + " made from account ID: " + accountId);
    }
 }

public class SeatReservationServiceImpl implements SeatReservationService {
    @Override
    public void reserveSeats(Long accountId, int numberOfSeats) {
        // Assume the seat reservation is successful without any defects
        // Implement the logic to reserve seats using an external seat reservation system
        // For the purpose of this example, let's just print a message
        System.out.println("Reserved " + numberOfSeats + " seats for account ID: " + accountId);
    }
 }
    
}
