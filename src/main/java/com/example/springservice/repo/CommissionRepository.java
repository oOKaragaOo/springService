package com.example.springservice.repo;

import com.example.springservice.entites.enmap.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
    List<Commission> findAllByCustomer_UserId(Integer customerId);
    List<Commission> findAllByArtist_UserId(Integer artistId);
}

