package org.example.catalog.service.Impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catalog.dto.BookRequestDTO;
import org.example.catalog.dto.BookResponseDTO;
import org.example.catalog.entity.Author;
import org.example.catalog.entity.Books;
import org.example.catalog.entity.Category;
import org.example.catalog.entity.Publisher;
import org.example.catalog.repository.*;
import org.example.catalog.service.BookService;
import org.example.catalog.service.CloudinaryService;
import org.example.catalog.specification.BookSpecification;
import org.example.common.dto.BookInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
//    private final OrderItemRepository orderItemRepository;
    private final CloudinaryService cloudinaryService;

    private final Executor catalogTaskExecutor;
    private final RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private BookJdbcRepository bookJdbcRepository;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;


    // ---------------- CREATE ----------------
    @Transactional
    @CachePut(value = "bookDetail", key = "#result.bookId")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponseDTO createBook(BookRequestDTO request) {

        boolean isUpdate = request.getBookId() != null;
        Books book;

        // --- Lấy sách cũ nếu update, tạo mới nếu không ---
        if (isUpdate) {
            book = new Books();
        } else {
            book = new Books();
            book.setCreatedAt(LocalDateTime.now());
        }

        // --- Set thông tin chung ---
        book.setTitle(request.getTitle());
        book.setImportPrice(request.getImportPrice());
        book.setMarketPrice(request.getMarketPrice());
        book.setSalePrice(request.getSalePrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setDescription(request.getDescription());

        // --- Xử lý ảnh Cloudinary ---
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(request.getImageFile());
            book.setImageUrl(imageUrl);
        }

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

        // --- Thêm mới sách ---
        if (!isUpdate) {
            bookRepository.save(book); // chỉ save 1 lần, bookNewId tự gán nhờ @PostPersist

            // Async cache
            CompletableFuture.runAsync(() ->
                            redisTemplate.opsForValue().set("book:" + book.getBookId(), book),
                    catalogTaskExecutor
            );

            return convertToDTO(book);
        }

        // --- Cập nhật sách ---
        Books oldBook = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Old book not found"));

        if (!Objects.equals(oldBook.getBookNewId(), oldBook.getBookId())) {
            throw new RuntimeException("Chỉ có thể cập nhật phiên bản mới nhất của sách!");
        }

        // 1️⃣ Tạo bản mới (version mới)
        book.setCreatedAt(LocalDateTime.now());
        book.setBookNewId(book.getBookId()); // gán trước save
        bookRepository.save(book); // save 1 lần

        // 2️⃣ Xác định rootId của chuỗi sách
        Long rootId = (oldBook.getBookNewId() != null) ? oldBook.getBookNewId() : oldBook.getBookId();

        // 3️⃣ Cập nhật tất cả bản cũ về phiên bản mới
        bookRepository.updateBookNewIdForRelated(rootId, book.getBookId()); // dùng bulk update query

        // 4️⃣ Async cache: bản mới + các bản liên quan
        CompletableFuture<Void> cacheNewBook = CompletableFuture.runAsync(() ->
                        redisTemplate.opsForValue().set("book:" + book.getBookId(), book),
                catalogTaskExecutor
        );

        CompletableFuture<Void> cacheRelatedBooks = CompletableFuture.runAsync(() -> {
            // Nếu muốn batch cache các bản liên quan, lấy list từ DB
            List<Books> relatedBooks = bookRepository.findRelatedVersions(book.getBookId());
            relatedBooks.parallelStream().forEach(b ->
                    redisTemplate.opsForValue().set("book:" + b.getBookId(), b)
            );
        }, catalogTaskExecutor);

        CompletableFuture.allOf(cacheNewBook, cacheRelatedBooks)
                .exceptionally(ex -> {
                    log.error("Cache update failed: {}", ex.getMessage());
                    return null;
                });

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
//    @Override
//    @Cacheable(value = "bookDetail", key = "#id")
//    public BookResponseDTO getBookById(Long id) {
//        Books book = bookRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Book not found"));
//
//        BookResponseDTO dto = convertToDTO(book);
//
//        // Lấy sách liên quan (cùng tác giả, không gồm chính sách này)
//        if (book.getAuthorId() != null) {
//            List<BookResponseDTO> relatedBooks = bookRepository.findByAuthorId(book.getAuthorId())
//                    .stream()
//                    .filter(b -> !b.getBookId().equals(id))
//                    .map(this::convertToDTO)
//                    .limit(5) // giới hạn số sách liên quan
//                    .toList();
//            dto.setBookResponseDTOList(relatedBooks);
//        }
//
//        return dto;
//    }


    @Override
    @Cacheable(value = "bookDetail", key = "#id")
    public BookResponseDTO getBookById(Long id) {
        Books book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookResponseDTO dto = new BookResponseDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthorId());
        dto.setImportPrice(book.getImportPrice());
        dto.setMarketPrice(book.getMarketPrice());
        dto.setSalePrice(book.getSalePrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setDescription(book.getDescription());
        dto.setImageUrl(book.getImageUrl());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setBookNewId(book.getBookNewId());

        // ⚙️ Lấy dữ liệu liên quan song song
        CompletableFuture<Author> authorFuture = CompletableFuture.supplyAsync(() ->
                authorRepository.findById(book.getAuthorId())
                        .orElseThrow(() -> new RuntimeException("Author not found")), catalogTaskExecutor);

        CompletableFuture<Publisher> publisherFuture = CompletableFuture.supplyAsync(() ->
                publisherRepository.findById(book.getPublisherId())
                        .orElseThrow(() -> new RuntimeException("Publisher not found")), catalogTaskExecutor);

        CompletableFuture<Category> categoryFuture = CompletableFuture.supplyAsync(() ->
                categoryRepository.findById(book.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found")), catalogTaskExecutor);

        // ⚙️ Đợi tất cả hoàn tất
        CompletableFuture.allOf(authorFuture, publisherFuture, categoryFuture).join();

        Author author = authorFuture.join();
        Publisher publisher = publisherFuture.join();
        Category category = categoryFuture.join();

        dto.setAuthorName(author.getAuthorName());
        dto.setPublisherName(publisher.getPublisherName());
        dto.setCategoryName(category.getCategoryName());

        // ⚙️ Lấy sách liên quan song song
        CompletableFuture<List<BookResponseDTO>> relatedBooksFuture = CompletableFuture.supplyAsync(() ->
                bookRepository.findByAuthorId(book.getAuthorId())
                        .stream()
                        .filter(b -> !b.getBookId().equals(id))
                        .map(this::convertToDTO)
                        .limit(5)
                        .toList(), catalogTaskExecutor);
        dto.setBookResponseDTOList(relatedBooksFuture.join());
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
        log.info("Starting searchBooks with title='{}', authorName='{}', categoryName='{}', minPrice={}, maxPrice={}",
                title, authorName, categoryName, minPrice, maxPrice);

        // --- Build specification
        Specification<Books> spec = BookSpecification.hasTitle(title)
                .and(BookSpecification.hasAuthorName(authorName))
                .and(BookSpecification.hasCategoryName(categoryName))
                .and(BookSpecification.priceBetween(minPrice, maxPrice));

        // --- Query DB
        List<Books> books = bookRepository.findAll(spec);
        log.info("Found {} books. Start parallel DTO conversion...", books.size());

        // --- Convert to DTO in parallel
        List<CompletableFuture<BookResponseDTO>> futures = books.stream()
                .map(book -> CompletableFuture.supplyAsync(() -> {
                    log.info("Thread {} converting book '{}'", Thread.currentThread().getName(), book.getTitle());
                    return convertToDTO(book);
                }, catalogTaskExecutor))
                .toList();

        // --- Wait all threads complete and collect results
        List<BookResponseDTO> result = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        log.info("All DTOs converted. Total: {}", result.size());
        return result;
    }



    @Override
    @Transactional
    @CachePut(value = "bookDetail", key = "#id")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponseDTO updateStockQuantity(Long id, Integer quantity) {
        // 🔒 Dùng khóa ghi để đảm bảo chỉ 1 thread update 1 book tại 1 thời điểm
        Books book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setStockQuantity(book.getStockQuantity() + quantity);
        Books updated = bookRepository.save(book);

        log.info("Thread {} updated stock of book {} to {}",
                Thread.currentThread().getName(), book.getBookId(), updated.getStockQuantity());

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
