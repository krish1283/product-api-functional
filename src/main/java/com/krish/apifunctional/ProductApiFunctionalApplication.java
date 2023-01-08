package com.krish.apifunctional;

import com.krish.apifunctional.model.Product;
import com.krish.apifunctional.repository.ProductRepository;
import com.krish.apifunctional.handler.ProductHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@SpringBootApplication
public class ProductApiFunctionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiFunctionalApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository repository){
		return args -> {
			Flux<Product> productFlux =
					Flux.just(
							new Product(null,"Big Latte",2.99),
							new Product(null,"Capuchino",2.49),
							new Product(null,"Green Tea",1.99)
					).flatMap(p->repository.save(p));

			productFlux.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler productHandler){
		return RouterFunctions.route(GET("/products")
						.and(accept(MediaType.APPLICATION_JSON))
						,productHandler::getProducts)
				.andRoute(POST("/products")
								.and(accept(MediaType.APPLICATION_JSON))
						,productHandler::saveProduct)
				.andRoute(GET("/products/events")
								.and(accept(MediaType.TEXT_EVENT_STREAM))
						,productHandler::getProductEvents);
	}

}
