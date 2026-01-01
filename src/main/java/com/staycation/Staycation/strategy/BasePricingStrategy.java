package com.staycation.Staycation.strategy;

import com.staycation.Staycation.entity.Inventory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;

@Service
public class BasePricingStrategy  implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
