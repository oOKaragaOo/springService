package com.example.springservice.repo;

import com.example.springservice.entites.ArtistStyleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtistStyleMappingRepository extends JpaRepository<ArtistStyleMapping, Integer> {
    List<ArtistStyleMapping> findAllByUserId(Integer userId);
}
