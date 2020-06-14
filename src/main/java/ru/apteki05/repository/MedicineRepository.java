package ru.apteki05.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.apteki05.model.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
