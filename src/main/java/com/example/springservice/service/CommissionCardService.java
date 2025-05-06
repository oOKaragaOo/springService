package com.example.springservice.service;

import com.example.springservice.dto.CommissionCardCreateDTO;
import com.example.springservice.dto.CommissionCardResponseDTO;
import com.example.springservice.dto.CommissionCardUpdateDTO;
import com.example.springservice.entites.CommissionCard;
import com.example.springservice.entites.User;
import com.example.springservice.repo.CommissionCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommissionCardService {

    @Autowired
    private CommissionCardRepository cardRepo;

    public CommissionCardResponseDTO createCard(User artist, CommissionCardCreateDTO dto) {
        CommissionCard card = new CommissionCard();
        card.setArtist(artist);
        card.setTitle(dto.title);
        card.setDescription(dto.description);
        card.setPrice(dto.price);
        card.setEstimatedDuration(dto.estimatedDuration);
        card.setSampleImageUrl(dto.sampleImageUrl);
        card.setOpen(true);

        return new CommissionCardResponseDTO(cardRepo.save(card));  // <-- น่าจะพังตรงนี้
    }


    public List<CommissionCardResponseDTO> getMyCards(User artist) {
        return cardRepo.findByArtist(artist).stream()
                .map(CommissionCardResponseDTO::new)
                .toList();
    }

    public List<CommissionCardResponseDTO> getPublicCards() {
        return cardRepo.findByOpenTrue().stream()
                .map(CommissionCardResponseDTO::new)
                .toList();
    }

    public ResponseEntity<?> updateCard(User artist, Integer id, CommissionCardUpdateDTO dto) {
        ResponseEntity<?> response = validateCard(artist, id);
        if (!response.getStatusCode().is2xxSuccessful()) return response;

        CommissionCard card = (CommissionCard) response.getBody();
        assert card != null;
        card.setTitle(dto.title);
        card.setDescription(dto.description);
        card.setPrice(dto.price);
        card.setEstimatedDuration(dto.estimatedDuration);
        card.setSampleImageUrl(dto.sampleImageUrl);
        card.setOpen(dto.open);

        cardRepo.save(card);
        return ResponseEntity.ok(Map.of("message", "Commission card updated"));
    }

    public ResponseEntity<?> deleteCard(User artist, Integer id) {
        ResponseEntity<?> response = validateCard(artist, id);
        if (!response.getStatusCode().is2xxSuccessful()) return response;

        assert response.getBody() != null;
        cardRepo.delete((CommissionCard) response.getBody());
        return ResponseEntity.ok(Map.of("message", "Commission card deleted"));
    }

    private ResponseEntity<?> validateCard(User artist, Integer cardId) {
        Optional<CommissionCard> cardOpt = cardRepo.findById(cardId);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Card not found"));
        }

        CommissionCard card = cardOpt.get();
        if (!card.getArtist().getUserId().equals(artist.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Not your card"));
        }

        return ResponseEntity.ok(card); // ถ้า validate ผ่าน return card ใน body
    }

}


