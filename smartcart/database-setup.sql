-- PostgreSQL Database Setup for Orders API

-- Create users table (if not exists)
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  items JSONB NOT NULL,
  total_price NUMERIC(10, 2) NOT NULL,
  address TEXT NOT NULL,
  status TEXT DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- Insert sample user for testing (optional)
INSERT INTO users (name, email, password) 
VALUES ('Test User', 'test@example.com', 'hashed_password_here')
ON CONFLICT (email) DO NOTHING;

-- Verify tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('users', 'orders');
