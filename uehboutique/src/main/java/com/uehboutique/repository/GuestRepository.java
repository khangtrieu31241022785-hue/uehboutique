package com.uehboutique.repository;

import com.uehboutique.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    Optional<Guest> findByIdCard(String idCard);

    Optional<Guest> findByPhone(String phone);
}
