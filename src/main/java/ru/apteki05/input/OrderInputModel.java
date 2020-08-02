package ru.apteki05.input;

import java.util.Comparator;

import ru.apteki05.output.MedicineOutputModel;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

public enum OrderInputModel {
    DEFAULT((x, y) -> 1),
    PRICE(comparing(MedicineOutputModel::getPrice, nullsLast(naturalOrder())));

    final Comparator<MedicineOutputModel> comparator;

    OrderInputModel(Comparator<MedicineOutputModel> comparator) {
        this.comparator = comparator;
    }

    public Comparator<MedicineOutputModel> getComparator() {
        return comparator;
    }
}
