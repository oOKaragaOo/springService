package com.example.springservice.dto;

import com.example.springservice.entites.CommissionCard;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommissionCardResponseDTO {
    public Integer cardId;
    public String title;
    public String description;
    public String price;;
    public Integer estimatedDuration;
    public String sampleImageUrl;
    public Boolean open;

    public CommissionCardResponseDTO(CommissionCard card) {
        this.cardId = card.getCardId();
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.price = card.getPrice();
        this.estimatedDuration = card.getEstimatedDuration();
        this.sampleImageUrl = card.getSampleImageUrl();
        this.open = card.getOpen();
    }
}


