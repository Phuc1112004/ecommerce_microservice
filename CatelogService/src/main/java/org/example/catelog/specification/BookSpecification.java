package org.example.catelog.specification;



import org.example.catelog.entity.Books;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Books> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Books> hasAuthorName(String authorName) {
        return (root, query, cb) ->
                authorName == null ? null : cb.like(cb.lower(root.get("author").get("authorName")), "%" + authorName.toLowerCase() + "%");
    }

    public static Specification<Books> hasCategoryName(String categoryName) {
        return (root, query, cb) ->
                categoryName == null ? null : cb.like(cb.lower(root.get("category").get("categoryName")), "%" + categoryName.toLowerCase() + "%");
    }

    public static Specification<Books> priceBetween(Long minPrice, Long maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("salePrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("salePrice"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("salePrice"), maxPrice);
            }
            return null;
        };
    }
}

