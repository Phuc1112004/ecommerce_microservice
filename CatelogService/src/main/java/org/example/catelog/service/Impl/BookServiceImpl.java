package org.example.catelog.service.Impl;


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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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


    // ---------------- CREATE ----------------
    @CacheEvict(value = { "books", "book" }, allEntries = true)
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
        book.setBookNewId(request.getBookId());

        // set author, publisher, category
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        book.setAuthorId(author.getAuthorId());
        book.setPublisherId(publisher.getPublisherId());
        book.setCategoryId(category.getCategoryId());

        bookRepository.save(book);
        book.setBookNewId(book.getBookId());
        if (request.getBookId() != null) {
            // Tìm sách cũ theo bookId
            Books oldBook = bookRepository.findById(request.getBookId()).orElse(null);
            // Tìm tất cả sách liên quan (bao gồm các phiên bản trước)
            List<Books> booksList = new ArrayList<>();
            if (oldBook != null) {
                // Thêm sách cũ
                booksList.add(oldBook);
                // Tìm các sách có bookNewId bằng bookId của bất kỳ phiên bản nào
                List<Books> relatedBooks = bookRepository.findByBookNewId(oldBook.getBookNewId());
                booksList.addAll(relatedBooks);
            }
            // Cập nhật bookNewId cho tất cả sách trong danh sách
            for (Books item : booksList) {
                item.setBookNewId(book.getBookId());
            }
            bookRepository.saveAll(booksList);
        }
        bookRepository.save(book);

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
    @Cacheable(value = "book", key = "#id")
    public BookResponseDTO getBookById(Long id) {
        BookResponseDTO responseDTO = convertToDTO(bookRepository.findById(id).get());
        if (responseDTO.getAuthorId()!= null){
            Optional<Author> author = authorRepository.findById(responseDTO.getAuthorId());
            if(author.isPresent()){
                List<BookResponseDTO> listAuthor = bookRepository.findByAuthorId(responseDTO.getAuthorId()).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
                responseDTO.setBookResponseDTOList(listAuthor);
            }
        }
        return responseDTO;
    }

    // ---------------- UPDATE ----------------
    @CachePut(value = "book", key = "#id")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(request.getTitle());
                    book.setImportPrice(request.getImportPrice());
                    book.setMarketPrice(request.getMarketPrice());
                    book.setSalePrice(request.getSalePrice());
                    book.setStockQuantity(request.getStockQuantity());
                    book.setDescription(request.getDescription());
                    book.setImageUrl(request.getImageUrl());

                    Author author = authorRepository.findById(request.getAuthorId())
                            .orElseThrow(() -> new RuntimeException("Author not found"));
                    Publisher publisher = publisherRepository.findById(request.getPublisherId())
                            .orElseThrow(() -> new RuntimeException("Publisher not found"));
                    Category category = categoryRepository.findById(request.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    book.setAuthorId(author.getAuthorId());
                    book.setPublisherId(publisher.getPublisherId());
                    book.setCategoryId(category.getCategoryId());

                    Books updated = bookRepository.save(book);
                    return convertToDTO(updated);
                })
                .orElse(null);
    }

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
    @CacheEvict(value = { "books", "book" }, allEntries = true)
    public void updateStockQuantity(Long id, Integer quantity) {
        Books book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setStockQuantity(book.getStockQuantity() + quantity);
        bookRepository.save(book);
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
