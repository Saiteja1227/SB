package com.example.firstproject;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // Add item to cart
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // Check if item already exists in cart
            List<CartItem> existingItems = cartItemRepository.findByUser(user);
            CartItem existingItem = existingItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);
            
            if (existingItem != null) {
                // Update quantity
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemRepository.save(existingItem);
                return ResponseEntity.ok(Map.of(
                    "message", "Cart updated successfully",
                    "cartItem", existingItem
                ));
            } else {
                // Create new cart item
                CartItem cartItem = new CartItem(user, product, quantity);
                CartItem savedItem = cartItemRepository.save(cartItem);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Item added to cart successfully",
                    "cartItem", savedItem
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error adding item to cart: " + e.getMessage()));
        }
    }
    
    // Get cart items for user
    @GetMapping
    public ResponseEntity<?> getCartItems(@RequestParam Long userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<CartItem> cartItems = cartItemRepository.findByUser(user);
            
            double total = cartItems.stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();
            
            return ResponseEntity.ok(Map.of(
                "cartItems", cartItems,
                "total", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error fetching cart: " + e.getMessage()));
        }
    }
    
    // Update cart item quantity
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            
            CartItem cartItem = cartItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cart item not found"));
            
            if (quantity <= 0) {
                cartItemRepository.delete(cartItem);
                return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
            }
            
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            
            return ResponseEntity.ok(Map.of(
                "message", "Cart item updated successfully",
                "cartItem", cartItem
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error updating cart item: " + e.getMessage()));
        }
    }
    
    // Remove item from cart
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id) {
        try {
            CartItem cartItem = cartItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cart item not found"));
            
            cartItemRepository.delete(cartItem);
            
            return ResponseEntity.ok(Map.of("message", "Item removed from cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error removing item: " + e.getMessage()));
        }
    }
    
    // Clear cart for user
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<CartItem> cartItems = cartItemRepository.findByUser(user);
            cartItemRepository.deleteAll(cartItems);
            
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error clearing cart: " + e.getMessage()));
        }
    }
}
