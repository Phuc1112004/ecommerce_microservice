package org.example.catelog.config;

import org.example.catelog.dto.BookResponseDTO;
import org.example.catelog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CacheRefresher {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    // Mỗi 30 phút refresh cache cả danh sách sách và sách chi tiết
    @Value("${cache.refresh.books.cron}")
    public void refreshAllBookCaches() {
        // 1. Xóa cache cũ
        cacheManager.getCache("books").clear();
        cacheManager.getCache("bookDetail").clear();

        // 2. Lấy danh sách tất cả sách từ DB
        List<BookResponseDTO> allBooks = bookService.getAllBooks();

        // 3. Put danh sách sách vào cache "books"
        cacheManager.getCache("books").put("allBooks", allBooks);

        // 4. Trigger getBookById để cache chi tiết từng sách (có sách liên quan)
        for (BookResponseDTO book : allBooks) {
            bookService.getBookById(book.getBookId()); // @Cacheable sẽ tự lưu cache
        }

        System.out.println("Đã refresh cache sách và sách chi tiết lúc " + LocalDateTime.now());
    }
}
