package com.example.firstproject;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Add demo products
        productRepository.save(createProduct(
            "Samsung Galaxy S24",
            "Latest flagship smartphone with AI features and 200MP camera",
            799.99,
            "Electronics",
            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9",
            50
        ));
        
        productRepository.save(createProduct(
            "Sony WH-1000XM5",
            "Premium noise-cancelling wireless headphones",
            349.99,
            "Electronics",
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
            30
        ));
        
        productRepository.save(createProduct(
            "Apple MacBook Pro",
            "14-inch M3 Pro with 18GB RAM and 512GB SSD",
            1999.99,
            "Electronics",
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8",
            20
        ));
        
        productRepository.save(createProduct(
            "Nike Air Max 270",
            "Comfortable running shoes with Air cushioning",
            129.99,
            "Sports",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff",
            100
        ));
        
        productRepository.save(createProduct(
            "Levi's 501 Original Jeans",
            "Classic straight-fit denim jeans",
            69.99,
            "Fashion",
            "https://images.unsplash.com/photo-1542272604-787c3835535d",
            75
        ));
        
        productRepository.save(createProduct(
            "Instant Pot Duo",
            "7-in-1 electric pressure cooker",
            89.99,
            "Home",
            "https://images.unsplash.com/photo-1585515320310-259814833e62",
            40
        ));
        
        productRepository.save(createProduct(
            "The Great Gatsby",
            "Classic novel by F. Scott Fitzgerald",
            14.99,
            "Books",
            "https://images.unsplash.com/photo-1543002588-bfa74002ed7e",
            200
        ));
        
        productRepository.save(createProduct(
            "Dell UltraSharp Monitor",
            "27-inch 4K USB-C monitor",
            449.99,
            "Electronics",
            "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf",
            25
        ));
        
        productRepository.save(createProduct(
            "Yoga Mat Premium",
            "Non-slip exercise mat with carrying strap",
            29.99,
            "Sports",
            "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f",
            150
        ));
        
        productRepository.save(createProduct(
            "Cashmere Sweater",
            "Luxury soft cashmere crew neck sweater",
            159.99,
            "Fashion",
            "https://images.unsplash.com/photo-1576566588028-4147f3842f27",
            35
        ));
        
        productRepository.save(createProduct(
            "Coffee Maker Deluxe",
            "Programmable drip coffee maker with thermal carafe",
            79.99,
            "Home",
            "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6",
            60
        ));
        
        productRepository.save(createProduct(
            "Atomic Habits",
            "Bestselling book on building good habits",
            16.99,
            "Books",
            "https://images.unsplash.com/photo-1512820790803-83ca734da794",
            180
        ));
        
        System.out.println("✅ Initialized database with " + productRepository.count() + " products");
    }
    
    private Product createProduct(String name, String description, Double price, String category, String image, Integer stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImage(image);
        product.setStock(stock);
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }
}
