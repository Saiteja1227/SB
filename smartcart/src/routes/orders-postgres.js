const express = require('express');
const pool = require('../config/postgres');

const router = express.Router();

// POST /api/orders - Place a new order
router.post('/', async (req, res) => {
  console.log('POST /api/orders - Received request:', req.body);
  
  try {
    const { userId, items, totalPrice, address } = req.body;

    // Validation
    if (!userId) {
      console.log('Validation failed: userId missing');
      return res.status(400).json({ message: 'Invalid order data' });
    }

    if (!items || !Array.isArray(items) || items.length === 0) {
      console.log('Validation failed: items array is empty or invalid');
      return res.status(400).json({ message: 'Invalid order data' });
    }

    if (typeof totalPrice !== 'number' || totalPrice <= 0) {
      console.log('Validation failed: totalPrice is not a valid number');
      return res.status(400).json({ message: 'Invalid order data' });
    }

    if (!address || typeof address !== 'string' || address.trim() === '') {
      console.log('Validation failed: address is missing or invalid');
      return res.status(400).json({ message: 'Invalid order data' });
    }

    // Check if user exists
    console.log('Checking if user exists with userId:', userId);
    const userCheck = await pool.query(
      'SELECT id FROM users WHERE id = $1',
      [userId]
    );

    if (userCheck.rows.length === 0) {
      console.log('Validation failed: userId does not exist');
      return res.status(400).json({ message: 'Invalid order data' });
    }

    // Insert order into PostgreSQL
    console.log('Inserting order into database...');
    const result = await pool.query(
      `INSERT INTO orders (user_id, items, total_price, address, status) 
       VALUES ($1, $2, $3, $4, $5) 
       RETURNING *`,
      [userId, JSON.stringify(items), totalPrice, address, 'pending']
    );

    console.log('Order created successfully:', result.rows[0]);

    res.status(201).json({
      message: 'Order placed successfully',
      order: result.rows[0]
    });

  } catch (error) {
    console.error('Error placing order:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});

// GET /api/orders - Get all orders (optional helper endpoint)
router.get('/', async (req, res) => {
  console.log('GET /api/orders - Fetching all orders');
  
  try {
    const result = await pool.query(
      'SELECT * FROM orders ORDER BY id DESC'
    );
    
    console.log(`Found ${result.rows.length} orders`);
    res.json(result.rows);
  } catch (error) {
    console.error('Error fetching orders:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});

// GET /api/orders/:userId - Get orders by user ID (optional helper endpoint)
router.get('/user/:userId', async (req, res) => {
  const { userId } = req.params;
  console.log(`GET /api/orders/user/${userId} - Fetching orders for user`);
  
  try {
    const result = await pool.query(
      'SELECT * FROM orders WHERE user_id = $1 ORDER BY id DESC',
      [userId]
    );
    
    console.log(`Found ${result.rows.length} orders for user ${userId}`);
    res.json(result.rows);
  } catch (error) {
    console.error('Error fetching user orders:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});

module.exports = router;
