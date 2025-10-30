package org.example.catalog.repository;


import org.example.catalog.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    @Procedure(procedureName = "GetPublisherByName")
    List<Publisher> getPublisherByName(@Param("p_name") String name);

    @Procedure(procedureName = "AddPublisher")
    void addPublisher(
            @Param("p_name") String name,
            @Param("p_address") String address,
            @Param("p_phone") String phone,
            @Param("p_email") String email,
            @Param("p_website") String website,
            @Param("p_country") String country,
            @Param("p_foundedYear") Integer foundedYear,
            @Param("p_description") String description,
            @Param("p_publisherImage") String publiserImage
    );
}
