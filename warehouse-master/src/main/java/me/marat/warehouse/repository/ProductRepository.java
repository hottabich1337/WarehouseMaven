package me.marat.warehouse.repository;

import me.marat.warehouse.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getProductByArticle(Long article);
    List<Product> getAllByNameIn(List<String> names);
}
