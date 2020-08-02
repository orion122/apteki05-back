package ru.apteki05.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.apteki05.model.Medicine;

@Data
@NoArgsConstructor
public class MedicineOutputModel {
    private Long id;
    private String medicineName;
    private BigDecimal price;
    private Long count;
    private String shopName;
    private String shortShopName;
    private String shopAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yy")
    private LocalDateTime updateDate;

    public MedicineOutputModel(Medicine medicine) {
        this.id = medicine.getId();
        this.shopName = medicine.getPharmacy().getName();
        this.shopAddress = medicine.getPharmacy().getAddress();
        this.shortShopName = medicine.getPharmacy().getShortName();
        this.medicineName = medicine.getName();
        this.count = medicine.getCount();
        this.price = medicine.getPrice();
        this.updateDate = medicine.getUpdatedAt();
    }
}
