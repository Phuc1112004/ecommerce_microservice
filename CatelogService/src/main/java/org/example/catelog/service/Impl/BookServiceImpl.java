package org.example.catelog.service.Impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.catelog.dto.BookRequestDTO;
import org.example.catelog.dto.BookResponseDTO;
import org.example.catelog.entity.Author;
import org.example.catelog.entity.Books;
import org.example.catelog.entity.Category;
import org.example.catelog.entity.Publisher;
import org.example.catelog.repository.*;
import org.example.catelog.service.BookService;
import org.example.catelog.specification.BookSpecification;
import org.example.common.dto.BookInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
//    private final OrderItemRepository orderItemRepository;

    @Autowired
    private BookJdbcRepository bookJdbcRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    // ---------------- CREATE ----------------
    @Transactional
    @CachePut(value = "bookDetail", key = "#result.bookId")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponseDTO createBook(BookRequestDTO request) {
        Books book = new Books();
        book.setTitle(request.getTitle());
        book.setImportPrice(request.getImportPrice());
        book.setMarketPrice(request.getMarketPrice());
        book.setSalePrice(request.getSalePrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setDescription(request.getDescription());
        book.setImageUrl(request.getImageUrl());
        book.setCreatedAt(LocalDateTime.now());

        // --- Liên kết khóa ngoại ---
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        book.setAuthorId(author.getAuthorId());
        book.setPublisherId(publisher.getPublisherId());
        book.setCategoryId(category.getCategoryId());

        // --- Nếu là thêm mới ---
        if (request.getBookId() == null) {
            // Lưu trước để có bookId
            bookRepository.save(book);
            // Gán bookNewId = bookId
            book.setBookNewId(book.getBookId());
            bookRepository.save(book);

            // lưu vào cache
            redisTemplate.opsForValue().set("book:" + book.getBookId(), book);
            return convertToDTO(book);
        }

        // --- Nếu là cập nhật ---
        Books oldBook = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Old book not found"));

        // Chỉ cho phép cập nhật nếu bản gốc (bookNewId == bookId)
        if (!Objects.equals(oldBook.getBookNewId(), oldBook.getBookId())) {
            throw new RuntimeException("Chỉ có thể cập nhật phiên bản mới nhất của sách!");
        }

        // 1️⃣ Lưu bản mới
        bookRepository.save(book);
        book.setBookNewId(book.getBookId());
        bookRepository.save(book);

        // 2️⃣ Xác định “chuỗi gốc” (root) của bản cũ
        Long rootId = (oldBook.getBookNewId() != null)
                ? oldBook.getBookNewId()
                : oldBook.getBookId();

        // 3️⃣ Lấy tất cả bản thuộc cùng chuỗi đó
        List<Books> relatedBooks = bookRepository.findRelatedVersions(rootId);

        // 4️⃣ Cập nhật bookNewId của toàn bộ bản cũ trỏ về bản mới nhất
        for (Books b : relatedBooks) {
            b.setBookNewId(book.getBookId());
        }
        bookRepository.saveAll(relatedBooks);

        // 5️⃣ Cập nhật cache
        redisTemplate.opsForValue().set("book:" + book.getBookId(), book);
        for (Books b : relatedBooks) {
            redisTemplate.opsForValue().set("book:" + b.getBookId(), b);
        }

        return convertToDTO(book);
    }


    // ---------------- READ ----------------
    @Cacheable(value = "books")
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // chi tiết sách có kèm theo gọi ý những sách liên quan có chung tác giả
    // chi tiết sách + sách liên quan
    @Override
    @Cacheable(value = "bookDetail", key = "#id")
    public BookResponseDTO getBookById(Long id) {
        Books book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookResponseDTO dto = convertToDTO(book);

        // Lấy sách liên quan (cùng tác giả, không gồm chính sách này)
        if (book.getAuthorId() != null) {
            List<BookResponseDTO> relatedBooks = bookRepository.findByAuthorId(book.getAuthorId())
                    .stream()
                    .filter(b -> !b.getBookId().equals(id))
                    .map(this::convertToDTO)
                    .limit(5) // giới hạn số sách liên quan
                    .toList();
            dto.setBookResponseDTOList(relatedBooks);
        }

        return dto;
    }

    // ---------------- UPDATE ----------------
//    @CachePut(value = "book", key = "#id")
//    @CacheEvict(value = "books", allEntries = true)
//    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
//        return bookRepository.findById(id)
//                .map(book -> {
//                    book.setTitle(request.getTitle());
//                    book.setImportPrice(request.getImportPrice());
//                    book.setMarketPrice(request.getMarketPrice());
//                    book.setSalePrice(request.getSalePrice());
//                    book.setStockQuantity(request.getStockQuantity());
//                    book.setDescription(request.getDescription());
//                    book.setImageUrl(request.getImageUrl());
//
//                    Author author = authorRepository.findById(request.getAuthorId())
//                            .orElseThrow(() -> new RuntimeException("Author not found"));
//                    Publisher publisher = publisherRepository.findById(request.getPublisherId())
//                            .orElseThrow(() -> new RuntimeException("Publisher not found"));
//                    Category category = categoryRepository.findById(request.getCategoryId())
//                            .orElseThrow(() -> new RuntimeException("Category not found"));
//
//                    book.setAuthorId(author.getAuthorId());
//                    book.setPublisherId(publisher.getPublisherId());
//                    book.setCategoryId(category.getCategoryId());
//
//                    Books updated = bookRepository.save(book);
//                    return convertToDTO(updated);
//                })
//                .orElse(null);
//    }

    // ---------------- DELETE ----------------
//    public boolean deleteBook(Long id) {
//        List<Books> books = bookRepository.findByBookNewIdOrBookId(id, id);
//        List<OrderItem> items = orderItemRepository.findAllByBooksIn(books);
//        if (items.isEmpty()) {
//            bookRepository.deleteById(id);
//            return true;
//        }else
//            return false;
//    }




    // ---------------- CONVERT ----------------
    private BookResponseDTO convertToDTO(Books book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        Author author = authorRepository.findById(book.getAuthorId()).orElseThrow(() -> new RuntimeException("Author not found"));
        Publisher publisher = publisherRepository.findById(book.getPublisherId()).orElseThrow(() -> new RuntimeException("Publisher not found"));
        Category category = categoryRepository.findById(book.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));


        // Kiểm tra null trước khi lấy tên
        dto.setAuthorName(book.getAuthorId() != null ? author.getAuthorName() : null);
        dto.setAuthorId(book.getAuthorId() != null ? author.getAuthorId() : null);
        dto.setPublisherName(book.getPublisherId() != null ? publisher.getPublisherName() : null);
        dto.setCategoryName(book.getCategoryId() != null ? category.getCategoryName() : null);

        dto.setImportPrice(book.getImportPrice());
        dto.setMarketPrice(book.getMarketPrice());
        dto.setSalePrice(book.getSalePrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setDescription(book.getDescription());
        dto.setImageUrl(book.getImageUrl());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setBookNewId(book.getBookNewId());

        return dto;
    }

    // Lấy tất cả sách (JdbcTemplate)
    public List<BookResponseDTO> getAllBooksJdbc() {
        return bookJdbcRepository.getAllBoosJdbc();
    }

    // Dùng Specification để tìm kiếm Book
    @Cacheable(value = "searchBooks", key = "#title + '-' + #authorName + '-' + #categoryName + '-' + #minPrice + '-' + #maxPrice")
    public List<BookResponseDTO> searchBooks(String title, String authorName, String categoryName, Long minPrice, Long maxPrice) {
        Specification<Books> spec = BookSpecification.hasTitle(title)
                .and(BookSpecification.hasAuthorName(authorName))
                .and(BookSpecification.hasCategoryName(categoryName))
                .and(BookSpecification.priceBetween(minPrice, maxPrice));

        List<Books> books = bookRepository.findAll(spec);

        return books.stream()
                .map(this::convertToDTO)
                .toList();
    }


    @Override
    @CachePut(value = "bookDetail", key = "#id")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponseDTO updateStockQuantity(Long id, Integer quantity) {
        Books book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setStockQuantity(book.getStockQuantity() + quantity);
        Books updated = bookRepository.save(book);

        // trả về DTO để cache cập nhật đúng giá trị
        return convertToDTO(updated);
    }

    @Override
    public Optional<BookInfoDTO> findBookById(Long id) {
        return bookRepository.findById(id)
                .map(book -> new BookInfoDTO(
                        book.getBookId(),
                        book.getTitle(),
                        book.getSalePrice(),
                        book.getStockQuantity()
                ));
    }

}
