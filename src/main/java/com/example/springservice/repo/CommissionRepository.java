package com.example.springservice.repo;

import com.example.springservice.entites.User;
import com.example.springservice.entites.enmap.Commission;
import com.example.springservice.entites.enmap.CommissionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 📁 CommissionRepository.java
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
    List<Commission> findAllByCustomerOrderByCreatedAtDesc(User customer);
    List<Commission> findAllByArtistOrderByCreatedAtDesc(User artist);
}


