package org.example.catelog.service;

import org.example.catelog.dto.BookRequestDTO;
import org.example.catelog.dto.BookResponseDTO;
import org.example.common.dto.BookInfoDTO;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BookResponseDTO createBook(BookRequestDTO request);
//    BookResponseDTO deleteBook(BookRequestDTO request);
    List<BookResponseDTO> getAllBooks();
    BookResponseDTO getBookById(Long id);
//    BookResponseDTO updateBook(Long id, BookRequestDTO request);
    List<BookResponseDTO> getAllBooksJdbc();
    List<BookResponseDTO> searchBooks(String title, String authorName, String categoryName, Long minPrice, Long maxPrice);
    BookResponseDTO updateStockQuantity(Long id, Integer quantity);
    Optional<BookInfoDTO> findBookById(Long id);

}
