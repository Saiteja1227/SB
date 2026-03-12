package com.example.firstproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (productRepository.count() == 0) {
            seedProducts();
        }
    }

    private void seedProducts() {
        // Electronics
        productRepository.save(new Product(
            "Wireless Bluetooth Headphones",
            "Premium noise-cancelling headphones with 30-hour battery life",
            149.99,
            "Electronics",
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format",
            50
        ));

        productRepository.save(new Product(
            "Smart Watch Pro",
            "Fitness tracker with heart rate monitor and GPS",
            299.99,
            "Electronics",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&auto=format",
            30
        ));

        productRepository.save(new Product(
            "Mechanical Keyboard",
            "RGB backlit gaming keyboard with blue switches",
            89.99,
            "Electronics",
            "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&auto=format",
            40
        ));

        productRepository.save(new Product(
            "Wireless Mouse",
            "Ergonomic wireless mouse with adjustable DPI",
            34.99,
            "Electronics",
            "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&auto=format",
            60
        ));

        productRepository.save(new Product(
            "Portable SSD 1TB",
            "High-speed external SSD with USB 3.2",
            129.99,
            "Electronics",
            "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=500&auto=format",
            25
        ));

        productRepository.save(new Product(
            "Webcam HD 1080p",
            "Full HD webcam with built-in microphone",
            69.99,
            "Electronics",
            "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=500&auto=format",
            35
        ));

        productRepository.save(new Product(
            "Bluetooth Speaker",
            "Waterproof portable speaker with 12-hour battery",
            59.99,
            "Electronics",
            "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500&auto=format",
            45
        ));

        // Accessories
        productRepository.save(new Product(
            "Laptop Backpack",
            "Water-resistant backpack with USB charging port",
            49.99,
            "Accessories",
            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&auto=format",
            55
        ));

        productRepository.save(new Product(
            "USB-C Hub",
            "7-in-1 USB-C hub with HDMI and SD card reader",
            39.99,
            "Accessories",
            "https://images.unsplash.com/photo-1625948515291-69613efd103f?w=500&auto=format",
            70
        ));

        productRepository.save(new Product(
            "Phone Stand",
            "Adjustable aluminum phone and tablet stand",
            19.99,
            "Accessories",
            "https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=500&auto=format",
            80
        ));

        // Home & Garden
        productRepository.save(new Product(
            "LED Desk Lamp",
            "Smart LED lamp with touch control and USB port",
            44.99,
            "Home & Garden",
            "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500&auto=format",
            40
        ));

        // Sports
        productRepository.save(new Product(
            "Fitness Yoga Mat",
            "Non-slip exercise mat with carrying strap",
            29.99,
            "Sports",
            "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&auto=format",
            65
        ));

        // Additional products for better variety
        productRepository.save(new Product(
            "Smartphone Case",
            "Premium leather case with card holder",
            24.99,
            "Accessories",
            "https://images.unsplash.com/photo-1601593346740-925612772716?w=500&auto=format",
            100
        ));

        productRepository.save(new Product(
            "Gaming Monitor 27\"",
            "4K Ultra HD monitor with 144Hz refresh rate",
            449.99,
            "Electronics",
            "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&auto=format",
            15
        ));

        productRepository.save(new Product(
            "Wireless Earbuds",
            "True wireless earbuds with active noise cancellation",
            79.99,
            "Electronics",
            "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&auto=format",
            50
        ));

        productRepository.save(new Product(
            "Travel Mug",
            "Insulated stainless steel travel mug 16oz",
            19.99,
            "Home & Garden",
            "https://images.unsplash.com/photo-1534067783941-51c9c23ecefd?w=500&auto=format",
            90
        ));

        System.out.println("✅ Sample products seeded successfully!");
    }
}
