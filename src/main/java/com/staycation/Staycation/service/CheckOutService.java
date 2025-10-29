package com.staycation.Staycation.service;

import com.staycation.Staycation.entity.Booking;

public interface CheckOutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
