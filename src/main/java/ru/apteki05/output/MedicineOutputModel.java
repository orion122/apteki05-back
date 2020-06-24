package ru.apteki05.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.apteki05.model.Medicine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MedicineOutputModel {
    private Long id;
    private String medicineName;
    private BigDecimal price;
    private Long count;
    private String shopName;
    private String shopAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yy")
    private LocalDateTime updateDate;

    public MedicineOutputModel(Medicine medicine) {
        this.id = medicine.getId();
        this.shopName = medicine.getPharmacy().getName();
        this.shopAddress = medicine.getPharmacy().getAddress();
        this.medicineName = medicine.getName();
        this.count = medicine.getCount();
        this.price = medicine.getPrice();
        this.updateDate = medicine.getUpdatedAt();
    }
}
