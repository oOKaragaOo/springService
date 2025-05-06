package com.example.springservice.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public class CommissionCardCreateDTO {
    public String title;
    public String description;
    public BigDecimal price;
    public Integer estimatedDuration;
    public String sampleImageUrl;

    // ต้องมี constructor ว่าง
    public CommissionCardCreateDTO() {}
}



