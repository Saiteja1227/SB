package com.example.firstproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    // Create order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get shipping details
            String name = request.get("name") != null ? request.get("name").toString() : user.getUsername();
            String email = request.get("email") != null ? request.get("email").toString() : user.getEmail();
            String phone = request.get("phone") != null ? request.get("phone").toString() : "";
            String address = request.get("address").toString();
            String city = request.get("city") != null ? request.get("city").toString() : "";
            String zipCode = request.get("zipCode") != null ? request.get("zipCode").toString() : "";
            
            // Get order items from request or cart
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            
            if (items == null || items.isEmpty()) {
                // Get items from cart
                List<CartItem> cartItems = cartItemRepository.findByUser(user);
                if (cartItems.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "No items provided and cart is empty"));
                }
                
                // Convert cart items to order items
                items = new ArrayList<>();
                for (CartItem cartItem : cartItems) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", cartItem.getProduct().getId());
                    item.put("quantity", cartItem.getQuantity());
                    items.add(item);
                }
            }
            
            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setName(name);
            order.setEmail(email);
            order.setPhone(phone);
            order.setAddress(address);
            order.setCity(city);
            order.setZipCode(zipCode);
            order.setStatus("Pending");
            
            double totalAmount = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            
            // Create order items
            for (Map<String, Object> itemData : items) {
                Long productId = Long.valueOf(itemData.get("productId").toString());
                Integer quantity = Integer.valueOf(itemData.get("quantity").toString());
                
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
                
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());
                orderItem.setOrder(order);
                
                orderItems.add(orderItem);
                totalAmount += product.getPrice() * quantity;
            }
            
            order.setOrderItems(orderItems);
            order.setTotalAmount(totalAmount);
            
            // Save order
            Order savedOrder = orderRepository.save(order);
            
            // Clear cart after successful order
            List<CartItem> cartItems = cartItemRepository.findByUser(user);
            cartItemRepository.deleteAll(cartItems);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Order placed successfully",
                "order", savedOrder
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating order: " + e.getMessage()));
        }
    }
    
    // Get all orders for a user
    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) Long userId) {
        try {
            List<Order> orders;
            
            if (userId != null) {
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
            } else {
                // Get all orders (for admin)
                orders = orderRepository.findAllByOrderByCreatedAtDesc();
            }
            
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error fetching orders: " + e.getMessage()));
        }
    }
    
    // Get single order by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            return ResponseEntity.ok(order);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Order not found: " + e.getMessage()));
        }
    }
    
    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            order.setStatus(status);
            orderRepository.save(order);
            
            return ResponseEntity.ok(Map.of(
                "message", "Order status updated successfully",
                "order", order
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error updating order status: " + e.getMessage()));
        }
    }
}
