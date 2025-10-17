package org.example.catelog.service;

import org.example.common.dto.BookInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class BookServiceConcurrencyTest {

    @Autowired
    private BookService bookService;

    @Test
    void testRaceConditionOnUpdateStock() throws InterruptedException {
        Long bookId = 1L; // đảm bảo sách này tồn tại
        int threads = 20;
        int quantityPerThread = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        System.out.println("Start concurrent updates...");

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    updateStockSafely(bookId, quantityPerThread);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Chờ tất cả thread kết thúc
        latch.await();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Lấy stock cuối cùng
        BookInfoDTO info = bookService.findBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found!"));
        System.out.println("Stock after concurrent updates: " + info.getStockQuantity());
    }

    /**
     * Wrapper gọi updateStockQuantity trong transaction mới để commit ngay
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStockSafely(Long bookId, int quantity) {
        bookService.updateStockQuantity(bookId, quantity);
        System.out.println("Updated " + quantity + " on thread " + Thread.currentThread().getName());
    }
}
