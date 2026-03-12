const express = require('express');
const Order = require('../models/Order');
const Cart = require('../models/Cart');
const Product = require('../models/Product');
const { auth, adminAuth } = require('../middleware/auth');

const router = express.Router();

// Create order
router.post('/', auth, async (req, res) => {
  try {
    const cart = await Cart.findOne({ userId: req.user.userId }).populate('products.productId');
    if (!cart || cart.products.length === 0) {
      return res.status(400).json({ message: 'Cart is empty' });
    }

    const totalAmount = cart.products.reduce((sum, item) => 
      sum + (item.productId.price * item.quantity), 0
    );

    const order = new Order({
      userId: req.user.userId,
      products: cart.products.map(p => ({ productId: p.productId._id, quantity: p.quantity })),
      totalAmount
    });

    await order.save();
    await Cart.findOneAndDelete({ userId: req.user.userId });

    res.status(201).json(order);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Get user orders
router.get('/', auth, async (req, res) => {
  try {
    const orders = await Order.find({ userId: req.user.userId }).populate('products.productId');
    res.json(orders);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Get all orders (Admin only)
router.get('/all', [auth, adminAuth], async (req, res) => {
  try {
    const orders = await Order.find().populate('userId products.productId');
    res.json(orders);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Update order status (Admin only)
router.put('/:id', [auth, adminAuth], async (req, res) => {
  try {
    const { status } = req.body;
    const order = await Order.findByIdAndUpdate(
      req.params.id,
      { status },
      { new: true }
    );
    if (!order) return res.status(404).json({ message: 'Order not found' });
    res.json(order);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

module.exports = router;
