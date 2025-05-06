package com.example.springservice.service;

import com.example.springservice.entites.enmap.Commission;
import lombok.RequiredArgsConstructor;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommissionResponseDTO requestCommission(CommissionCreateDTO dto, User customer) {
        User artist = userRepository.findById(dto.artistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));

        Commission commission = new Commission();
        commission.setCustomer(customer);
        commission.setArtist(artist);
        commission.setTitle(dto.title);
        commission.setDescription(dto.description);
        commission.setPrice(dto.price);
        commission.setDeadline(dto.deadline);
        commission.setStatus(Commission.Status.REQUESTED);
        commissionRepository.save(commission);

        notificationService.sendNotiTo(artist, "NEW_COMMISSION", "📥 มีคำขอใหม่จาก " + customer.getName());

        return new CommissionResponseDTO(commission);
    }

    public CommissionResponseDTO respondToRequest(Integer id, Commission.Status status, User artist) {
        Commission commission = findCommissionById(id);

        if (!commission.getArtist().getUserId().equals(artist.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the artist");
        }

        if (commission.getStatus() != Commission.Status.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status flow");
        }

        commission.setStatus(status);
        commissionRepository.save(commission);

        notificationService.sendNotiTo(commission.getCustomer(), "COMMISSION_RESPONSE",
                "🎨 นักวาดตอบกลับแล้ว: " + status.name());

        return new CommissionResponseDTO(commission);
    }

    public CommissionResponseDTO updateStatus(Integer id, Commission.Status newStatus, User user) {
        Commission commission = findCommissionById(id);

        boolean isArtist = commission.getArtist().getUserId().equals(user.getUserId());
        boolean isCustomer = commission.getCustomer().getUserId().equals(user.getUserId());

        if (newStatus == Commission.Status.IN_PROGRESS || newStatus == Commission.Status.DELIVERED) {
            if (!isArtist) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only artist can update to this status");
        }

        if (newStatus == Commission.Status.COMPLETED) {
            if (!isCustomer) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customer can complete");
        }

        commission.setStatus(newStatus);
        commissionRepository.save(commission);

        // ส่ง noti
        User receiver = isArtist ? commission.getCustomer() : commission.getArtist();
        notificationService.sendNotiTo(receiver, "STATUS_UPDATE",
                "📌 สถานะคอมมิชชันอัปเดต: " + newStatus.name());

        return new CommissionResponseDTO(commission);
    }

    public CommissionResponseDTO getDetail(Integer id, User user) {
        Commission c = findCommissionById(id);
        if (!c.getCustomer().getUserId().equals(user.getUserId()) &&
                !c.getArtist().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access");
        }
        return new CommissionResponseDTO(c);
    }

    public List<CommissionResponseDTO> getMyCommissions(User user) {
        List<Commission> list = commissionRepository.findByCustomerOrArtist(user, user);
        return list.stream().map(CommissionResponseDTO::new).toList();
    }

    private Commission findCommissionById(Integer id) {
        return commissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
    }
}

