require('dotenv').config();
const express = require('express');
const cors = require('cors');
const connectDB = require('./src/config/db');

const app = express();

// Connect to MongoDB (for existing routes)
connectDB();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', require('./src/routes/auth'));
app.use('/api/products', require('./src/routes/products'));
app.use('/api/cart', require('./src/routes/cart'));
// Use PostgreSQL orders route
app.use('/api/orders', require('./src/routes/orders-postgres'));

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
