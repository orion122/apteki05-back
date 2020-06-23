package ru.apteki05.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(
        name = "pharmacies",
        indexes = {@Index(columnList = "token", unique = true)}
)
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String shortName;

    @Column
    private String city;

    @Column
    private String address;

    @Column(nullable = false)
    private String token;

    @OneToMany(mappedBy = "pharmacy", fetch = FetchType.LAZY)
    private List<Medicine> medicines;
}
