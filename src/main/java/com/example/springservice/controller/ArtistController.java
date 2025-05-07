package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.CommissionCardCreateDTO;
import com.example.springservice.dto.CommissionCardUpdateDTO;
import com.example.springservice.entites.User;
import com.example.springservice.entites.UserFollows;
import com.example.springservice.repo.UserFollowsRepository;
import com.example.springservice.repo.UserRepository;
import com.example.springservice.service.CommissionCardService;
import com.example.springservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/artist")
public class ArtistController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CommissionCardService cardService;

    @Autowired
    private NotificationService notiService;

    @Autowired
    UserFollowsRepository userFollowsRepo;

    //======================================= 🛠️ C 🛠️ ======================================================//
    @PostMapping("/commission-cards")
    public ResponseEntity<?> createCard(@RequestBody CommissionCardCreateDTO dto, HttpServletRequest request) {
        System.out.println("--> POST /artist/commission-cards");
        User artist = SessionUtil.requireSessionUser(userRepo, request);

        notiService.sendToAllFollowers(artist, artist, "NEW_COMMISSION",  artist.getName() + " : กำลังเปิดรับงานอยู่ตอนนี้ ไปดูกันเลย 📢 ");

        return ResponseEntity.ok(cardService.createCard(artist, dto));
    }

    //======================================= 🛠️ R 🛠️ ======================================================//

    @GetMapping("/public-cards")
    public ResponseEntity<?> getPublicCards() {
        System.out.println("getPublicCards");
        return ResponseEntity.ok(cardService.getPublicCards());
    }

    @GetMapping("/commission-cards")
    public ResponseEntity<?> getMyCards(HttpServletRequest request) {
        System.out.println("--> GET /artist/commission-cards");
        User artist = SessionUtil.requireSessionUser(userRepo, request);
        return ResponseEntity.ok(cardService.getMyCards(artist));
    }

    //======================================= 🛠️ U 🛠️ ======================================================//
    @PutMapping("/commission-cards/{id}")
    public ResponseEntity<?> updateCard(@PathVariable Integer id,
                                        @RequestBody CommissionCardUpdateDTO dto,
                                        HttpServletRequest request) {
        User artist = SessionUtil.requireSessionUser(userRepo, request);
        return cardService.updateCard(artist, id, dto);
    }

    @PutMapping("/commission-cards/{id}/toggle-status")
    public ResponseEntity<?> toggleCommissionStatus(@PathVariable Integer id, HttpServletRequest request) {
        System.out.println("--> ⚡ PUT /artist/commission-cards/{id}/toggle-status");
        User artist = SessionUtil.requireSessionUser(userRepo, request);
        return cardService.toggleCommissionStatus(id, artist);
    }

    //======================================= 🛠️ D 🛠️ ======================================================//
    @DeleteMapping("/commission-cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Integer id, HttpServletRequest request) {
        System.out.println("--> \uD83D\uDD25 DELETE /artist/commission-cards/{id}");
        User artist = SessionUtil.requireSessionUser(userRepo, request);

        return cardService.deleteCard(artist, id);
    }


}

