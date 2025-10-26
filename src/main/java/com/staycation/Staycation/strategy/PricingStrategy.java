package com.staycation.Staycation.strategy;

import com.staycation.Staycation.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service


public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
