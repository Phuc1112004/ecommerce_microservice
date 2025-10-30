package org.example.catalog.repository;


import org.example.catalog.entity.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PublishersRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private RowMapper<Publisher> publisherRowMapper = (rs, rowNum) -> {
        Publisher p = new Publisher();
        p.setPublisherId(rs.getLong("publisher_id"));
        p.setPublisherName(rs.getString("publisher_name"));
        p.setPublisherImage(rs.getString("publisher_image"));
        p.setAddress(rs.getString("address"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setWebsite(rs.getString("website"));
        p.setCountry(rs.getString("country"));
        p.setFoundedYear(rs.getObject("founded_year") != null ? rs.getInt("founded_year") : null);
        p.setDescription(rs.getString("description"));
        p.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return p;
    };

    // CREATE
    public Publisher save(Publisher publisher) {
        if (publisher.getCreatedAt() == null) {
            publisher.setCreatedAt(LocalDateTime.now());
        }

        String sql = """
        INSERT INTO publisher 
        (publisher_name, publisher_image, address, phone, email, website, country, founded_year, description, created_at)
        VALUES (:publisherName, :publisherImage, :address, :phone, :email, :website, :country, :foundedYear, :description, :createdAt)
    """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(publisher);

        namedJdbcTemplate.update(sql, params, keyHolder, new String[]{"publisher_id"});
        Number key = keyHolder.getKey();
        if (key != null) {
            publisher.setPublisherId(key.longValue());
        }

        return publisher;
    }


    // READ ALL
    public List<Publisher> findAll() {
        String sql = "SELECT publisher_id, publisher_name, publisher_image, address, phone, email, website, country, founded_year, description, created_at FROM publisher";
        return namedJdbcTemplate.query(sql, publisherRowMapper);
    }

    // READ BY ID
    public Optional<Publisher> findById(Long id) {
        String sql = "SELECT publisher_id, publisher_name, publisher_image, address, phone, email, website, country, founded_year, description, created_at FROM publisher WHERE publisher_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Publisher> result = namedJdbcTemplate.query(sql, params, publisherRowMapper);
        return result.stream().findFirst();
    }

    // UPDATE
    public int update(Publisher publisher) {
        if (publisher.getCreatedAt() == null) {
            publisher.setCreatedAt(LocalDateTime.now());
        }
        String sql = """
            UPDATE publisher 
            SET publisher_name = :publisherName,
                publisher_image = :publisherImage,
                address = :address,
                phone = :phone,
                email = :email,
                website = :website,
                country = :country,
                founded_year = :foundedYear,
                description = :description,
                created_at = :createdAt
            WHERE publisher_id = :publisherId
        """;
        SqlParameterSource params = new BeanPropertySqlParameterSource(publisher);
        return namedJdbcTemplate.update(sql, params);
    }

    // DELETE
    public int delete(Long id) {
        String sql = "DELETE FROM publisher WHERE publisher_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return namedJdbcTemplate.update(sql, params);
    }
}
