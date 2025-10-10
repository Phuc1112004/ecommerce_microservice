package org.example.catelog.repository;


import org.example.catelog.entity.Books;
import org.example.common.dto.BookInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Books, Long>, JpaSpecificationExecutor {
    List<Books> findByTitleContaining(String title_keyword);
    List<Books> findByBookNewId(Long title_keyword);
    List<Books> findByBookNewIdOrBookId(Long title_keyword, Long id);
    List<Books> findByAuthorId(Long authorId);

//    Long getBooksByPurchaseId(Long purchaseId);
    @Query("SELECT b.title FROM Books b WHERE b.bookId = :bookId")
    String getTitleByBookId(@Param("bookId") Long bookId);

    @Query("SELECT new org.example.common.dto.BookInfoDTO(b.bookId, b.title, b.salePrice, b.stockQuantity) " +
            "FROM Books b WHERE b.bookId = :bookId")
    BookInfoDTO getBookInfo(@Param("bookId") Long bookId);

}
