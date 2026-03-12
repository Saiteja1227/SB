package com.example.firstproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>SmartCart API</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        max-width: 1000px;
                        margin: 50px auto;
                        padding: 20px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: #333;
                    }
                    .container {
                        background: white;
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                    }
                    h1 { 
                        color: #667eea; 
                        border-bottom: 3px solid #667eea;
                        padding-bottom: 10px;
                    }
                    h2 { 
                        color: #764ba2; 
                        margin-top: 30px;
                    }
                    .endpoint {
                        background: #f8f9fa;
                        padding: 15px;
                        margin: 10px 0;
                        border-left: 4px solid #667eea;
                        border-radius: 4px;
                    }
                    .method {
                        display: inline-block;
                        padding: 3px 8px;
                        border-radius: 3px;
                        font-weight: bold;
                        font-size: 12px;
                        margin-right: 10px;
                    }
                    .get { background: #61affe; color: white; }
                    .post { background: #49cc90; color: white; }
                    .delete { background: #f93e3e; color: white; }
                    .put { background: #fca130; color: white; }
                    code {
                        background: #e9ecef;
                        padding: 2px 6px;
                        border-radius: 3px;
                        font-family: 'Courier New', monospace;
                    }
                    .status {
                        display: inline-block;
                        background: #28a745;
                        color: white;
                        padding: 5px 15px;
                        border-radius: 20px;
                        font-size: 14px;
                        margin-bottom: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🛒 SmartCart API Server</h1>
                    <div class="status">✓ Server Running on Port 8080</div>
                    
                    <h2>🔐 Authentication Endpoints</h2>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/register</code> - Register new user
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/login</code> - User login (with reCAPTCHA)
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/forgot-password</code> - Request password reset token
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/reset-password</code> - Reset password with token
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/verify-reset-token</code> - Verify reset token
                    </div>
                    
                    <h2>� User Management</h2>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/auth/users</code> - Get all registered users with login details
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/auth/profile</code> - Get user profile (requires JWT)
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/auth/health</code> - Health check
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/update</code> - Update user password
                    </div>
                    
                    <h2>�🛍️ Product Endpoints</h2>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/products</code> - Get all products
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/products/{id}</code> - Get product by ID
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/products/category/{category}</code> - Get products by category
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/products/search?query={text}</code> - Search products
                    </div>
                    
                    <h2>🛒 Cart Endpoints</h2>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/cart</code> - Get cart items
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/cart/add</code> - Add item to cart
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/cart/update</code> - Update cart item quantity
                    </div>
                    <div class="endpoint">
                        <span class="method delete">DELETE</span>
                        <code>/api/cart/remove/{id}</code> - Remove item from cart
                    </div>
                    
                    <h2>📦 Order Endpoints</h2>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/orders</code> - Get user orders
                    </div>
                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/api/orders/{id}</code> - Get order by ID
                    </div>
                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/api/orders</code> - Create new order
                    </div>
                    
                    <h2>� Security Features</h2>
                    <p>✓ Google reCAPTCHA Enterprise (v3) on login</p>
                    <p>✓ JWT Token Authentication</p>
                    <p>✓ Password Reset with Time-Limited Tokens</p>
                    <p>✓ CORS enabled for cross-origin requests</p>
                    <p>✓ Firebase Phone Authentication</p>
                    
                    <h2>📝 Test Credentials</h2>
                    <div class="endpoint">
                        <strong>Email:</strong> <code>test@example.com</code><br>
                        <strong>Password:</strong> <code>password123</code>
                    </div>
                    <div class="endpoint">
                        <strong>Email:</strong> <code>teja@gmail.com</code><br>
                        <strong>Password:</strong> <code>password123</code>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
