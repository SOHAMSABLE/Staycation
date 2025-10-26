package com.staycation.Staycation.strategy;

import com.staycation.Staycation.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing (Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //Apply the aditional Strategy

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupanyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStratgey(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }
}
