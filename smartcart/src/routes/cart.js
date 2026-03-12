const express = require('express');
const Cart = require('../models/Cart');
const { auth } = require('../middleware/auth');

const router = express.Router();

// Get cart
router.get('/', auth, async (req, res) => {
  try {
    const cart = await Cart.findOne({ userId: req.user.userId }).populate('products.productId');
    res.json(cart || { products: [] });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Add to cart
router.post('/add', auth, async (req, res) => {
  try {
    const { productId, quantity } = req.body;
    let cart = await Cart.findOne({ userId: req.user.userId });

    if (!cart) {
      cart = new Cart({ userId: req.user.userId, products: [{ productId, quantity }] });
    } else {
      const itemIndex = cart.products.findIndex(p => p.productId.toString() === productId);
      if (itemIndex > -1) {
        cart.products[itemIndex].quantity += quantity;
      } else {
        cart.products.push({ productId, quantity });
      }
    }

    await cart.save();
    res.json(cart);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Update cart
router.put('/update', auth, async (req, res) => {
  try {
    const { productId, quantity } = req.body;
    const cart = await Cart.findOne({ userId: req.user.userId });
    if (!cart) return res.status(404).json({ message: 'Cart not found' });

    const itemIndex = cart.products.findIndex(p => p.productId.toString() === productId);
    if (itemIndex > -1) {
      cart.products[itemIndex].quantity = quantity;
      await cart.save();
    }

    res.json(cart);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Remove from cart
router.delete('/remove', auth, async (req, res) => {
  try {
    const { productId } = req.body;
    const cart = await Cart.findOne({ userId: req.user.userId });
    if (!cart) return res.status(404).json({ message: 'Cart not found' });

    cart.products = cart.products.filter(p => p.productId.toString() !== productId);
    await cart.save();
    res.json(cart);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

module.exports = router;
