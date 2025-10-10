package org.example.catelog.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Table(name = "publisher")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherId;

    private String publisherName;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String country;
    private LocalDate foundedYear;
    private String description;
    private LocalDateTime createdAt;

}
