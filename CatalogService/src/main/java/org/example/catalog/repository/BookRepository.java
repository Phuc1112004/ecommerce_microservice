package org.example.catalog.repository;


import jakarta.persistence.LockModeType;
import org.example.catalog.entity.Books;
import org.example.common.dto.BookInfoDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT b FROM Books b WHERE b.bookNewId = :rootId OR b.bookId = :rootId")
    List<Books> findRelatedVersions(@Param("rootId") Long rootId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Books b WHERE b.bookId = :id")
    Optional<Books> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Books b SET b.bookNewId = :newId WHERE b.bookNewId = :rootId OR b.bookId = :rootId")
    void updateBookNewIdForRelated(@Param("rootId") Long rootId, @Param("newId") Long newId);

}
