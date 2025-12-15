package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateCountDTO {
    private int count;
    private LocalDate date;
}
