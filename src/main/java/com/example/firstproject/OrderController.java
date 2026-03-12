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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.firstproject.dto.OrderResponseDTO;
import com.example.firstproject.service.OrderService;

@RestController
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
    
    @Autowired
    private OrderService orderService;
    
    // Create order
    @PostMapping({"/orders", "/api/orders"})
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== DEBUG: Creating order ===");
            System.out.println("DEBUG: Request userId: " + request.get("userId"));
            System.out.println("DEBUG: Request email: " + request.get("email"));
            
            // Handle both numeric userId and Firebase UID
            String userIdStr = request.get("userId").toString();
            Users user = null;
            
            // Try to parse as Long first (for backward compatibility)
            try {
                Long userId = Long.valueOf(userIdStr);
                user = userRepository.findById(userId).orElse(null);
                System.out.println("DEBUG: Found user by numeric ID: " + (user != null));
            } catch (NumberFormatException e) {
                // Not a number, might be a Firebase UID - look up by email
                System.out.println("DEBUG: userId is not numeric, treating as Firebase UID: " + userIdStr);
                String email = request.get("email") != null ? request.get("email").toString() : null;
                if (email != null) {
                    user = userRepository.findByEmail(email);
                    System.out.println("DEBUG: Found user by email '" + email + "': " + (user != null));
                    
                    // If user still not found, create a new user record with Firebase UID as username
                    if (user == null) {
                        System.out.println("DEBUG: Creating new user with email: " + email + ", username (Firebase UID): " + userIdStr);
                        user = new Users();
                        user.setEmail(email);
                        user.setUsername(userIdStr); // Store Firebase UID as username!
                        user.setPassword(""); // No password for Firebase users
                        user = userRepository.save(user);
                        System.out.println("DEBUG: Created new user with ID: " + user.getId());
                    }
                }
            }
            
            // If user is still null after all attempts, use or create guest user
            if (user == null) {
                String guestEmail = request.get("email") != null ? request.get("email").toString() : "guest@smartcart.com";
                
                // Check if guest email already exists
                user = userRepository.findByEmail(guestEmail);
                
                // Only create if doesn't exist
                if (user == null) {
                    user = new Users();
                    user.setEmail(guestEmail);
                    user.setUsername(request.get("name") != null ? request.get("name").toString() : "Guest");
                    user.setPassword(""); // Guest user
                    user = userRepository.save(user);
                }
            }
            
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
                // Get items from cart (if user exists and is not newly created)
                if (user != null && user.getId() != null) {
                    List<CartItem> cartItems = cartItemRepository.findByUser(user);
                    if (!cartItems.isEmpty()) {
                        // Convert cart items to order items
                        items = new ArrayList<>();
                        for (CartItem cartItem : cartItems) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("productId", cartItem.getProduct().getId());
                            item.put("quantity", cartItem.getQuantity());
                            items.add(item);
                        }
                    }
                }
                
                // If still no items, return error
                if (items == null || items.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "No items provided in the order"));
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
    @GetMapping({"/orders", "/api/orders"})
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
    @GetMapping({"/orders/{id}", "/api/orders/{id}"})
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
    @PutMapping({"/orders/{id}/status", "/api/orders/{id}/status"})
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
    
    // Get orders by user ID
    @GetMapping({"/api/orders/user/{userId}", "/orders/user/{userId}"})
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId) {
        try {
            System.out.println("=== DEBUG: Fetching orders for userId: " + userId + " ===");
            Users user = null;
            
            // Try to parse as Long first (for numeric user IDs)
            try {
                Long userIdLong = Long.valueOf(userId);
                user = userRepository.findById(userIdLong).orElse(null);
                System.out.println("DEBUG: Tried numeric ID lookup, user found: " + (user != null));
            } catch (NumberFormatException e) {
                // Not a number, treat as Firebase UID - find by username or email
                System.out.println("DEBUG: Not a numeric ID, treating as Firebase UID");
                user = userRepository.findByUsername(userId);
                System.out.println("DEBUG: Username lookup result: " + (user != null));
                if (user == null) {
                    user = userRepository.findByEmail(userId);
                    System.out.println("DEBUG: Email lookup result: " + (user != null));
                }
            }
            
            if (user == null) {
                System.out.println("DEBUG: No user found for userId: " + userId);
                // Check if this userId matches any order's email directly
                List<Order> ordersByEmail = orderRepository.findAll().stream()
                    .filter(o -> userId.equals(o.getEmail()))
                    .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                    .toList();
                
                if (!ordersByEmail.isEmpty()) {
                    System.out.println("DEBUG: Found " + ordersByEmail.size() + " orders by email match");
                    List<OrderResponseDTO> orderDTOs = orderService.convertToDTOList(ordersByEmail);
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "count", orderDTOs.size(),
                        "orders", orderDTOs
                    ));
                }
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", 0,
                    "orders", new ArrayList<>()
                ));
            }
            
            System.out.println("DEBUG: User found - ID: " + user.getId() + ", Email: " + user.getEmail());
            List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
            System.out.println("DEBUG: Found " + orders.size() + " orders for user");
            List<OrderResponseDTO> orderDTOs = orderService.convertToDTOList(orders);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", orderDTOs.size(),
                "orders", orderDTOs
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Error fetching orders: " + e.getMessage()));
        }
    }
    
    // Get orders by email
    @GetMapping({"/api/orders/user/email/{email}", "/orders/user/email/{email}"})
    public ResponseEntity<?> getOrdersByEmail(@PathVariable String email) {
        try {
            Users user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", 0,
                    "orders", new ArrayList<>()
                ));
            }
            
            List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
            List<OrderResponseDTO> orderDTOs = orderService.convertToDTOList(orders);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", orderDTOs.size(),
                "orders", orderDTOs
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching orders: " + e.getMessage()));
        }
    }
    
    // Debug: Get all users (for troubleshooting)
    @GetMapping({"/api/debug/users", "/debug/users"})
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Users> users = userRepository.findAll();
            List<Map<String, Object>> userList = users.stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername() != null ? u.getUsername() : "null");
                    map.put("email", u.getEmail() != null ? u.getEmail() : "null");
                    return map;
                })
                .toList();
            return ResponseEntity.ok(Map.of("success", true, "count", users.size(), "users", userList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    // Debug: Get all orders (for troubleshooting)
    @GetMapping({"/api/debug/orders", "/debug/orders"})
    public ResponseEntity<?> getAllOrdersDebug() {
        try {
            List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
            List<Map<String, Object>> orderList = orders.stream()
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("orderId", o.getId());
                    map.put("userId", o.getUser() != null ? o.getUser().getId() : null);
                    map.put("customerEmail", o.getEmail());
                    map.put("customerName", o.getName());
                    map.put("totalAmount", o.getTotalAmount());
                    map.put("status", o.getStatus());
                    map.put("createdAt", o.getCreatedAt().toString());
                    return map;
                })
                .toList();
            return ResponseEntity.ok(Map.of("success", true, "count", orders.size(), "orders", orderList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
