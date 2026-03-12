package com.example.firstproject.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String phone;
    private String address;
    private String city;
    private String zipCode;
    private Double totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
    
    // Nested DTO for order items
    public static class OrderItemDTO {
        private Long itemId;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private String category;
        private Double price;
        private Integer quantity;
        private Double subtotal;
        
        // Constructors
        public OrderItemDTO() {}
        
        public OrderItemDTO(Long itemId, Long productId, String productName, String productDescription, 
                           String productImage, String category, Double price, Integer quantity) {
            this.itemId = itemId;
            this.productId = productId;
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = price * quantity;
        }
        
        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getProductDescription() { return productDescription; }
        public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
        
        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
    
    // Constructors
    public OrderResponseDTO() {}
    
    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
