# Orders API - PostgreSQL Integration

## Setup Instructions

### 1. Install Dependencies
```bash
cd smartcart
npm install
```

### 2. Configure Environment Variables

Add the following to your `.env` file in the `smartcart` folder:

```env
# PostgreSQL Connection (from Render)
DATABASE_URL=postgresql://username:password@host:5432/database_name

# Other existing variables
MONGODB_URI=your_mongodb_uri
PORT=5000
JWT_SECRET=your_jwt_secret
NODE_ENV=production
```

### 3. Setup PostgreSQL Database on Render

1. Go to your Render dashboard
2. Navigate to your PostgreSQL database
3. Copy the **External Database URL**
4. Use this as your `DATABASE_URL` in the `.env` file

### 4. Run Database Setup Script

Connect to your Render PostgreSQL database and run the SQL commands from `database-setup.sql`:

```bash
# Using psql command line
psql "postgresql://username:password@host:5432/database_name" -f database-setup.sql
```

Or copy and paste the SQL commands directly in the Render dashboard SQL console.

### 5. Start the Server

```bash
npm run dev
# or
npm start
```

## API Endpoint

### POST /api/orders

Place a new order in the PostgreSQL database.

**Request:**
```json
POST http://localhost:5000/api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "productName": "Product 1",
      "quantity": 2,
      "price": 29.99
    },
    {
      "productId": 2,
      "productName": "Product 2",
      "quantity": 1,
      "price": 49.99
    }
  ],
  "totalPrice": 109.97,
  "address": "123 Main St, City, State 12345"
}
```

**Success Response (201):**
```json
{
  "message": "Order placed successfully",
  "order": {
    "id": 1,
    "user_id": 1,
    "items": [...],
    "total_price": "109.97",
    "address": "123 Main St, City, State 12345",
    "status": "pending",
    "created_at": "2026-03-12T10:30:00.000Z"
  }
}
```

**Validation Error Response (400):**
```json
{
  "message": "Invalid order data"
}
```

**Server Error Response (500):**
```json
{
  "message": "Internal server error"
}
```

## Validation Rules

The endpoint validates:
- ✅ `userId` must exist and be provided
- ✅ `userId` must exist in the users table
- ✅ `items` array must not be empty
- ✅ `totalPrice` must be a number greater than 0
- ✅ `address` must be a non-empty string

## Testing with cURL

```bash
# Test placing an order
curl -X POST http://localhost:5000/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "productName": "Laptop",
        "quantity": 1,
        "price": 999.99
      }
    ],
    "totalPrice": 999.99,
    "address": "456 Oak Ave, Springfield, IL 62701"
  }'

# Get all orders
curl http://localhost:5000/api/orders

# Get orders by user ID
curl http://localhost:5000/api/orders/user/1
```

## Additional Helper Endpoints

- `GET /api/orders` - Retrieve all orders
- `GET /api/orders/user/:userId` - Get orders for a specific user

## Features Implemented

✅ **CORS enabled** - Already configured in server.js  
✅ **JSON middleware** - Already configured in server.js  
✅ **Request validation** - userId, items, totalPrice, address  
✅ **Database validation** - Checks if userId exists in users table  
✅ **Error handling** - Try/catch with proper status codes  
✅ **Console logging** - Detailed logs for debugging  
✅ **PostgreSQL integration** - Uses connection pool for performance  
✅ **JSONB storage** - Items stored as JSONB for flexible querying  

## Database Schema

```sql
orders (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  items JSONB NOT NULL,
  total_price NUMERIC(10, 2) NOT NULL,
  address TEXT NOT NULL,
  status TEXT DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
)
```

## Troubleshooting

### Connection Issues
- Verify `DATABASE_URL` is correct in `.env`
- Check if Render PostgreSQL database is running
- Ensure IP whitelist is configured on Render (usually allows all IPs)

### Validation Errors
- Ensure user exists in the database before placing orders
- Check that all required fields are included in the request
- Verify `totalPrice` is a number, not a string

### Console Logs
Check server console for detailed logs:
- Request received
- Validation results
- Database query results
- Error messages
