package org.example.common.client;

import org.example.common.dto.BookInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalog-service", url = "http://localhost:8002/api/catalog")
public interface BookClient {
//    @GetMapping("/book/{purchaseId}")
//    Long getBookByPurchaseId(@PathVariable("purchaseId") Long purchaseId);

    @GetMapping("/books/book-title/{bookId}")
    String getTitleByBookId(@PathVariable("bookId") Long bookId);

    @PutMapping("/books/{id}/update-stock")
    void updateStockQuantity(@PathVariable("id") Long bookId,
                             @RequestParam("quantity") Integer quantity);

    @GetMapping("/{bookId}/info")
    BookInfoDTO getBookInfo(@PathVariable("bookId") Long bookId);

    @GetMapping("/books/{bookId}")
    BookInfoDTO getBookById(@PathVariable("bookId") Long bookId);

    @GetMapping("/books/find/{bookId}")
    BookInfoDTO findBookById(@PathVariable("bookId") Long bookId);

}
