package org.example.catelog.repository;



import org.example.catelog.dto.BookResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // JDBC template
    public List<BookResponseDTO> getAllBoosJdbc() {
        String sql = """
            SELECT 
                b.book_id, b.title, b.import_price, b.market_price, b.sale_price,
                b.stock_quantity, b.description, b.image_url, b.created_at,
                a.author_name AS author_name,
                p.publisher_name AS publisher_name,
                c.category_name AS category_name
            FROM books b
            LEFT JOIN author a ON b.author_id = a.author_id
            LEFT JOIN publisher p ON b.publisher_id = p.publisher_id
            LEFT JOIN category c ON b.category_id = c.category_id
            ORDER BY b.created_at DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BookResponseDTO dto = new BookResponseDTO();
            dto.setBookId(rs.getLong("book_id"));
            dto.setTitle(rs.getString("title"));
            dto.setImportPrice(rs.getLong("import_price"));
            dto.setMarketPrice(rs.getLong("market_price"));
            dto.setSalePrice(rs.getLong("sale_price"));
            dto.setStockQuantity(rs.getInt("stock_quantity"));
            dto.setDescription(rs.getString("description"));
            dto.setImageUrl(rs.getString("image_url"));
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            dto.setAuthorName(rs.getString("author_name"));
            dto.setPublisherName(rs.getString("publisher_name"));
            dto.setCategoryName(rs.getString("category_name"));
            return dto;
        });
    }
}

