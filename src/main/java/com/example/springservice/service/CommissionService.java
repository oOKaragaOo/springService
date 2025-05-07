package com.example.springservice.service;

import com.example.springservice.entites.enmap.Commission;
import lombok.RequiredArgsConstructor;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionService {


    private final CommissionRepository commissionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    private CommissionRepository commissionRepo;
    @Autowired
    private CommissionCardRepository cardRepo;
    @Autowired
    CommissionBriefRepository commissionBriefRepo;


    public CommissionResponseDTO requestCommission(CommissionCreateDTO dto, User customer) {
        User artist = userRepository.findById(dto.artistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));

        Commission commission = new Commission();
        commission.setCustomer(customer);
        commission.setArtist(artist);
        commission.setTitle(dto.title);
        commission.setDescription(dto.description);
        commission.setPrice(String.valueOf(dto.price));
        commission.setDeadline(dto.deadline);
        commission.setStatus(Commission.Status.REQUESTED);
        commissionRepository.save(commission);

        notificationService.sendNotiTo(artist, "NEW_COMMISSION", "📥 มีคำขอใหม่จาก " + customer.getName());

        return new CommissionResponseDTO(commission);
    }
    // 🟢 ศิลปินดูคอมมิชชันที่ได้รับ
    public List<CommissionResponseDTO> getMyCommissionsAsArtist(User artist) {
        List<Commission> list = commissionRepo.findByArtist(artist);
        return list.stream().map(CommissionResponseDTO::new).toList();
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

    // 🔍 ลูกค้าและศิลปินดูงานตัวเอง
    public CommissionResponseDTO getDetail(Integer id, User user) {
        Commission c = getCommissionOrThrow(id);
        if (!c.getCustomer().getUserId().equals(user.getUserId()) &&
                !c.getArtist().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
        return new CommissionResponseDTO(c);
    }

    private Commission getCommissionOrThrow(Integer id) {
        return commissionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
    }

    public List<CommissionResponseDTO> getMyCommissions(User user) {
        List<Commission> list = commissionRepo.findByCustomerOrArtist(user, user);
        return list.stream().map(CommissionResponseDTO::new).toList();
    }

    public CommissionBriefDTO createCommissionRequest(CommissionBriefRequestDTO dto, User customer) {
        CommissionCard card = cardRepo.findById(dto.cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        if (card.getArtist().getUserId().equals(customer.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot request your own commission card");
        }

        Commission com = new Commission();
        com.setCustomer(customer);
        com.setArtist(card.getArtist());
        com.setTitle(card.getTitle());
        com.setDescription(card.getDescription());
        com.setPrice(card.getPrice());
        com.setStatus(Commission.Status.REQUESTED);
        commissionRepo.save(com);

        CommissionBrief brief = new CommissionBrief();
        brief.setCommission(com);
        brief.setCustomer(customer);
        brief.setFileUrl(dto.fileUrl);
        brief.setFileType(dto.fileType);
        brief.setDescription(dto.description);
        commissionBriefRepo.save(brief);

        return CommissionBriefDTO.fromEntity(brief);
    }

    private Commission findCommissionById(Integer id) {
        return commissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
    }
}

