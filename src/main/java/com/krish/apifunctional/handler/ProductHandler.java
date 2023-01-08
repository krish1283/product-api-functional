package com.krish.apifunctional.handler;

import com.krish.apifunctional.model.Product;
import com.krish.apifunctional.model.ProductEvent;
import com.krish.apifunctional.repository.ProductRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
/**
 * A hadler class to define all the handlerFunctions for products
 */
public class ProductHandler {
    ProductRepository productRepository;

    public ProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //defining a handlerfunction which excepts a ServerReqest and return a Mono of
    //ServerResponse
    public Mono<ServerResponse> getProducts(ServerRequest request){
        Flux<Product> products = productRepository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(products,Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<Product> productMono = productRepository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono.flatMap(product -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(product,Product.class)).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request){
        Mono<Product> productMono = request.bodyToMono(Product.class);
        return productMono.flatMap(product->
              ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(productRepository.save(product),Product.class));
    }

    public Mono<ServerResponse> getProductEvents(ServerRequest request){
        Flux<ProductEvent> eventsFlux = Flux.interval(Duration.ofSeconds(1))
                .map(second -> new ProductEvent(second,"Server Event"));

        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventsFlux,ProductEvent.class);

    }

}
