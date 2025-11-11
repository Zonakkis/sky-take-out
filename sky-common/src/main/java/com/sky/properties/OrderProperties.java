package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties("sky.order")
@Data
public class OrderProperties {
    private BigDecimal packagingFeeCoefficient;
    private BigDecimal deliveryFee;

}
