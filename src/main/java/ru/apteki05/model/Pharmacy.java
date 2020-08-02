package ru.apteki05.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

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

    @Column
    private String phone;

    @Column
    private String timetable;

    @Column
    private String url;

    @Column(nullable = false)
    private String token;

    @OneToMany(mappedBy = "pharmacy", fetch = FetchType.LAZY)
    private List<Medicine> medicines;
}
