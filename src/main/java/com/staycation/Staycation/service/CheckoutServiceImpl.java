package com.staycation.Staycation.service;
import com.staycation.Staycation.entity.Booking;
import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.repository.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckOutService {

    private final BookingRepository bookingRepository;

    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("Creating session for booking with ID: {}", booking.getId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            Customer customer = Customer.create(customerParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() +" : "+ booking.getRoom().getType())
                                                                    .setDescription("Booking ID: "+booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            log.info("Session created successfully for booking with ID: {}", booking.getId());
            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }
}
//package com.staycation.Staycation.service;
//import com.staycation.Staycation.entity.Booking;
//import com.staycation.Staycation.entity.User;
//import com.staycation.Staycation.repository.BookingRepository;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Customer;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.CustomerCreateParams;
//import com.stripe.param.checkout.SessionCreateParams;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CheckoutServiceImpl implements CheckOutService {
//
//    private static final BigDecimal MIN_PAYMENT_INR = BigDecimal.valueOf(50);
//
//    private final BookingRepository bookingRepository;
//
//    @Override
//    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {
//
//        validateMinimumAmount(booking);
//
//        User user = getCurrentUser();
//        log.info("Creating checkout session | bookingId={} | userId={}",
//                booking.getId(), user.getId());
//
//        try {
//            Customer customer = createStripeCustomer(user);
//
//            SessionCreateParams sessionParams =
//                    buildCheckoutSessionParams(booking, customer, successUrl, failureUrl);
//
//            Session session = Session.create(sessionParams);
//
//            booking.setPaymentSessionId(session.getId());
//            bookingRepository.save(booking);
//
//            log.info("Stripe session created | sessionId={}", session.getId());
//            return session.getUrl();
//
//        } catch (StripeException e) {
//            log.error("Stripe checkout session creation failed", e);
//            throw new RuntimeException("Payment gateway error. Please try again later.");
//        }
//    }
//
//    /* ---------------- PRIVATE HELPERS ---------------- */
//
//private void validateMinimumAmount(Booking booking) {
//    if (booking.getAmount().compareTo(MIN_PAYMENT_INR) < 0) {
//        throw new IllegalStateException(
//                "Minimum booking amount must be at least ₹" + MIN_PAYMENT_INR
//        );
//    }
//}
//
//private Customer createStripeCustomer(User user) throws StripeException {
//    CustomerCreateParams params = CustomerCreateParams.builder()
//            .setName(user.getName())
//            .setEmail(user.getEmail())
//            .build();
//
//    return Customer.create(params);
//}
//
//private SessionCreateParams buildCheckoutSessionParams(
//        Booking booking,
//        Customer customer,
//        String successUrl,
//        String failureUrl
//) {
//
//    long amountInPaise = booking.getAmount()
//            .multiply(BigDecimal.valueOf(100))
//            .longValueExact();
//
//    return SessionCreateParams.builder()
//            .setMode(SessionCreateParams.Mode.PAYMENT)
//            .setCustomer(customer.getId())
//            .setBillingAddressCollection(
//                    SessionCreateParams.BillingAddressCollection.REQUIRED
//            )
//            .setSuccessUrl(successUrl)
//            .setCancelUrl(failureUrl)
//            .addLineItem(
//                    SessionCreateParams.LineItem.builder()
//                            .setQuantity(1L)
//                            .setPriceData(
//                                    SessionCreateParams.LineItem.PriceData.builder()
//                                            .setCurrency("inr")
//                                            .setUnitAmount(amountInPaise)
//                                            .setProductData(
//                                                    SessionCreateParams
//                                                            .LineItem
//                                                            .PriceData
//                                                            .ProductData
//                                                            .builder()
//                                                            .setName(
//                                                                    booking.getHotel().getName()
//                                                                            + " : "
//                                                                            + booking.getRoom().getType()
//                                                            )
//                                                            .setDescription(
//                                                                    "Booking ID: " + booking.getId()
//                                                            )
//                                                            .build()
//                                            )
//                                            .build()
//                            )
//                            .build()
//            )
//            .build();
//}
//
//private User getCurrentUser() {
//    return (User) SecurityContextHolder
//            .getContext()
//            .getAuthentication()
//            .getPrincipal();
//}
//}
