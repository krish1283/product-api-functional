package com.krish.apifunctional.repository;

import com.krish.apifunctional.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository  extends ReactiveMongoRepository<Product,String> {
    Mono<Product> findByName(String name);
}
