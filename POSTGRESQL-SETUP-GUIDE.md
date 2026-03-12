# Spring Boot PostgreSQL Integration - Complete Guide

## ✅ Changes Completed

### 1. Database Configuration
- **Updated**: `pom.xml` - Added PostgreSQL driver dependency
- **Updated**: `application.properties` - Configured PostgreSQL connection to Render

### 2. Entity Classes (Already Exist)
- ✅ **Users** - User authentication and profile
- ✅ **Product** - Product catalog with pricing and inventory
- ✅ **CartItem** - Shopping cart items
- ✅ **Order** - Order with shipping details
- ✅ **OrderItem** - Individual items in an order

### 3. Repositories Updated
- **CartItemRepository** - Added `findByUser()` method
- **OrderRepository** - Added query methods for order retrieval
- **UserRepository** - Changed ID type from Integer to Long

### 4. REST Controllers Created/Updated
- ✅ **UserController** - Updated to `/api/register` and `/api/login`
- ✅ **ProductController** - Already exists with `/api/products` endpoints
- ✅ **CartController** - NEW - Shopping cart management
- ✅ **OrderController** - NEW - Order processing

### 5. CORS Configuration
- ✅ **CorsConfig** - Global CORS configuration allowing all origins

---

## 📋 API Endpoints

### Authentication Endpoints

#### POST /api/register
Register a new user.

**Request:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "department": "Sales"
}
```

**Response (201 Created):**
```json
"Registration successful"
```

#### POST /api/login
Authenticate a user.

**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
"Login successful"
```

---

### Product Endpoints

#### GET /api/products
Get all products.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "category": "Electronics",
    "image": "laptop.jpg",
    "stock": 50,
    "createdAt": "2026-03-12T10:00:00"
  }
]
```

#### GET /api/products/{id}
Get a single product by ID.

#### POST /api/products
Create a new product (admin).

#### PUT /api/products/{id}
Update a product (admin).

#### DELETE /api/products/{id}
Delete a product (admin).

---

### Cart Endpoints

#### POST /api/cart
Add item to cart.

**Request:**
```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

**Response (201 Created):**
```json
{
  "message": "Item added to cart successfully",
  "cartItem": {
    "id": 1,
    "product": {...},
    "quantity": 2
  }
}
```

#### GET /api/cart?userId=1
Get cart items for a user.

**Response (200 OK):**
```json
{
  "cartItems": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "Laptop",
        "price": 999.99
      },
      "quantity": 2
    }
  ],
  "total": 1999.98
}
```

#### PUT /api/cart/{id}
Update cart item quantity.

**Request:**
```json
{
  "quantity": 3
}
```

#### DELETE /api/cart/{id}
Remove item from cart.

#### DELETE /api/cart/clear?userId=1
Clear entire cart for a user.

---

### Order Endpoints

#### POST /api/orders
Create a new order.

**Request:**
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "555-1234",
  "address": "123 Main St",
  "city": "Springfield",
  "zipCode": "12345",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**Note:** If `items` is not provided, the system will use items from the user's cart.

**Response (201 Created):**
```json
{
  "message": "Order placed successfully",
  "order": {
    "id": 1,
    "user": {...},
    "orderItems": [...],
    "totalAmount": 1999.98,
    "status": "Pending",
    "address": "123 Main St",
    "city": "Springfield",
    "zipCode": "12345",
    "createdAt": "2026-03-12T10:30:00"
  }
}
```

#### GET /api/orders?userId=1
Get all orders for a user.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "totalAmount": 1999.98,
    "status": "Pending",
    "address": "123 Main St",
    "createdAt": "2026-03-12T10:30:00",
    "orderItems": [...]
  }
]
```

#### GET /api/orders
Get all orders (without userId parameter - for admin).

#### GET /api/orders/{id}
Get a single order by ID.

#### PUT /api/orders/{id}/status
Update order status.

**Request:**
```json
{
  "status": "Shipped"
}
```

---

## 🚀 Setup Instructions

### 1. Install Dependencies
Make sure you have Maven installed, then run:
```bash
cd /Users/saiteja/Desktop/mini_p/firstproject
mvn clean install
```

### 2. Database Setup on Render

Your PostgreSQL database is already configured with these credentials:
- **Host**: dpg-d6g15adm5p6s7393jh0g-a.oregon-postgres.render.com
- **Database**: myapp_1vxx
- **Username**: myapp_1vxx_user
- **Password**: BTUEi0O5iR7litF5365MvJV9USj0V0LC

The tables will be created automatically when the application starts (using `spring.jpa.hibernate.ddl-auto=update`).

### 3. Run the Application

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using Java**
```bash
mvn clean package
java -jar target/firstproject-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 4. Test the APIs

**Using cURL:**

```bash
# Register a user
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"test123"}'

# Login
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'

# Get products
curl http://localhost:8080/api/products

# Add to cart (use actual user ID and product ID)
curl -X POST http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":2}'

# Get cart
curl "http://localhost:8080/api/cart?userId=1"

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "Test User",
    "email": "test@example.com",
    "phone": "555-1234",
    "address": "123 Main St",
    "city": "Springfield",
    "zipCode": "12345"
  }'

# Get orders
curl "http://localhost:8080/api/orders?userId=1"
```

---

## 🔧 Configuration Details

### application.properties
```properties
spring.application.name=firstproject

# PostgreSQL Database Configuration (Render)
spring.datasource.url=jdbc:postgresql://dpg-d6g15adm5p6s7393jh0g-a.oregon-postgres.render.com:5432/myapp_1vxx
spring.datasource.username=myapp_1vxx_user
spring.datasource.password=BTUEi0O5iR7litF5365MvJV9USj0V0LC
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### CORS Configuration
All endpoints are configured to allow cross-origin requests from any origin (`*`). This can be restricted in production.

---

## 📊 Database Schema

The following tables will be created automatically:

### users
- id (BIGSERIAL PRIMARY KEY)
- username (VARCHAR)
- email (VARCHAR)
- password (VARCHAR)
- department (VARCHAR)
- reset_token (VARCHAR)
- token_expiry (TIMESTAMP)

### products
- id (BIGSERIAL PRIMARY KEY)
- name (VARCHAR)
- description (VARCHAR)
- price (DOUBLE PRECISION)
- category (VARCHAR)
- image (VARCHAR)
- stock (INTEGER)
- created_at (TIMESTAMP)

### cart_items
- id (BIGSERIAL PRIMARY KEY)
- user_id (BIGINT) → users(id)
- product_id (BIGINT) → products(id)
- quantity (INTEGER)

### orders
- id (BIGSERIAL PRIMARY KEY)
- user_id (BIGINT) → users(id)
- total_amount (DOUBLE PRECISION)
- status (VARCHAR)
- name (VARCHAR)
- email (VARCHAR)
- phone (VARCHAR)
- address (VARCHAR)
- city (VARCHAR)
- zip_code (VARCHAR)
- created_at (TIMESTAMP)

### order_items
- id (BIGSERIAL PRIMARY KEY)
- order_id (BIGINT) → orders(id)
- product_id (BIGINT) → products(id)
- quantity (INTEGER)
- price (DOUBLE PRECISION)

---

## 🎯 Testing with React Frontend

Your React frontend can now connect to these APIs. Make sure to:

1. Update your API base URL to `http://localhost:8080/api`
2. Use the correct endpoint paths
3. Include proper headers (Content-Type: application/json)

Example with Axios:
```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Register
axios.post(`${API_BASE_URL}/register`, userData);

// Login
axios.post(`${API_BASE_URL}/login`, credentials);

// Get products
axios.get(`${API_BASE_URL}/products`);

// Add to cart
axios.post(`${API_BASE_URL}/cart`, cartData);

// Create order
axios.post(`${API_BASE_URL}/orders`, orderData);
```

---

## 🐛 Troubleshooting

### Connection Issues
1. Check if PostgreSQL database on Render is active
2. Verify database credentials in application.properties
3. Check network connectivity

### Application Not Starting
1. Ensure port 8080 is not in use: `lsof -i :8080`
2. Check Maven dependencies are installed: `mvn dependency:resolve`
3. Look at error logs for specific issues

### CORS Errors
- CORS is enabled globally for all origins
- If issues persist, check browser console for specific error messages

### Database Errors
- Check that tables are created: Use Render's SQL console
- Verify column names match entity fields
- Check PostgreSQL logs in Render dashboard

---

## 📝 Notes

- All passwords are stored in plain text. **Add encryption (BCrypt) before production!**
- JWT authentication should be implemented for secure API access
- Input validation should be enhanced
- Error handling can be improved with custom exception handlers
- Consider adding pagination for large datasets
- Add logging for better debugging

---

## ✨ Summary

Your Spring Boot application is now fully configured to:
- ✅ Connect to PostgreSQL on Render
- ✅ Handle user registration and login at `/api/register` and `/api/login`
- ✅ Manage products at `/api/products`
- ✅ Handle shopping cart operations at `/api/cart`
- ✅ Process orders at `/api/orders`
- ✅ Support CORS for React frontend integration
- ✅ Use Spring Data JPA repositories for database operations

All endpoints are ready for testing and integration with your React frontend!
