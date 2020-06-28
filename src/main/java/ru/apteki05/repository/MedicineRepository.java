package ru.apteki05.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findAllByNameContainingIgnoreCase(String word);

    @Query("SELECT MAX(id) FROM Medicine")
    Long findMaxId();

    @Transactional
    Long deleteByPharmacy(Pharmacy pharmacy);
}
