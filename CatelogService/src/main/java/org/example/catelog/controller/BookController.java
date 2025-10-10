package org.example.catelog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.catelog.dto.BookRequestDTO;
import org.example.catelog.dto.BookResponseDTO;
import org.example.catelog.entity.Books;
import org.example.catelog.repository.BookRepository;
import org.example.catelog.service.BookService;
import org.example.common.dto.BookInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catelog/books")
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;

    // ---------------- CREATE ----------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody @Valid BookRequestDTO request) {
        BookResponseDTO createdBook = bookService.createBook(request);
        return ResponseEntity.ok(createdBook);
    }

    // ---------------- READ ----------------
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());

    }

//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
//    public ResponseEntity<List<BookResponseDTO>> getAllBooksJdbc() {
//        try {
//            List<BookResponseDTO> books = bookService.getAllBooksJdbc();
//            return ResponseEntity.ok(books);
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }



    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        if (book == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    @GetMapping("/find/{bookId}")
    public ResponseEntity<BookInfoDTO> findBookById(@PathVariable Long bookId) {
        BookInfoDTO dto = bookService.findBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return ResponseEntity.ok(dto);
    }


    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id,
                                                      @RequestBody @Valid BookRequestDTO request) {
        request.setBookId(id);
        BookResponseDTO updatedBook = bookService.createBook(request);
        if (updatedBook == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedBook);
    }

    // ---------------- DELETE ----------------
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
//        boolean deleted = bookService.deleteBook(id);
//        if (!deleted) return ResponseEntity.notFound().build();
//        return ResponseEntity.noContent().build(); // 204
//    }


    // search dùng specification
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice
    ) {
        List<BookResponseDTO> books = bookService.searchBooks(title, authorName, categoryName, minPrice, maxPrice);
        return ResponseEntity.ok(books);
    }

//    @GetMapping("/book/{purchaseId}")
//    public Long getBookByPurchaseId(@PathVariable("purchaseId") Long purchaseId) {
//        return bookRepository.getBooksByPurchaseId(purchaseId);
//    }

    @GetMapping("/book-title/{bookId}")
    public String getTitleByBookId(@PathVariable("bookId") Long bookId) {
        return bookRepository.getTitleByBookId(bookId);
    }

//    @PutMapping("/api/books/{id}/update-stock")
//    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Integer stockQuantity) {
//        bookService.updateStock(id, stockQuantity);
//        return ResponseEntity.ok().build();
//    }
    @PutMapping("/{id}/update-stock")
    public void updateStockQuantity(@PathVariable("id") Long bookId,
                                    @RequestParam("quantity") Integer quantity){
        bookService.updateStockQuantity(bookId, quantity);
    }

    @GetMapping("/{bookId}/info")
    public ResponseEntity<BookInfoDTO> getBookInfo(@PathVariable Long bookId) {
        BookInfoDTO bookInfo = bookRepository.getBookInfo(bookId);
        return ResponseEntity.ok(bookInfo);
    }
}

