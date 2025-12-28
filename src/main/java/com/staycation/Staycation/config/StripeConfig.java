package com.staycation.Staycation.config;

import com.stripe.Stripe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${stripe.secret.key}") String stripeSecretKey){
        Stripe.apiKey=stripeSecretKey;
        log.info("Stripe key loaded: {}", stripeSecretKey.startsWith("sk_"));
    }

}
