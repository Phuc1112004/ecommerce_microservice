package org.example.catalog.specification;



import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.catalog.entity.Author;
import org.example.catalog.entity.Books;
import org.example.catalog.entity.Category;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Books> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Books> hasAuthorName(String authorName) {
        return (root, query, cb) -> {
            if (authorName == null || authorName.isEmpty()) return cb.conjunction();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Author> authorRoot = subquery.from(Author.class);
            subquery.select(authorRoot.get("authorId"))
                    .where(cb.like(cb.lower(authorRoot.get("authorName")), "%" + authorName.toLowerCase() + "%"));

            return root.get("authorId").in(subquery);
        };
    }

    public static Specification<Books> hasCategoryName(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null || categoryName.isEmpty()) return cb.conjunction();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Category> categoryRoot = subquery.from(Category.class);
            subquery.select(categoryRoot.get("categoryId"))
                    .where(cb.like(cb.lower(categoryRoot.get("categoryName")), "%" + categoryName.toLowerCase() + "%"));

            return root.get("categoryId").in(subquery);
        };
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

