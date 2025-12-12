package com.sky.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DateSumDTO {
    private BigDecimal total;
    private LocalDate date;
}
