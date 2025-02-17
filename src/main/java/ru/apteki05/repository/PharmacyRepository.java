package ru.apteki05.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.apteki05.model.Pharmacy;

import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

//    @Query("SELECT name FROM Pharmacy WHERE token = :token")
//    String findNameByToken(String token);

    Optional<Pharmacy> findByToken(String token);
}
