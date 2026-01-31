-- Database: cssc

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    avatar VARCHAR(255),
    phone VARCHAR(20),
    is_rider TINYINT(1) DEFAULT 0,
    rider_status ENUM('not_applied', 'pending', 'approved', 'rejected') DEFAULT 'not_applied',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Rider applications table
CREATE TABLE IF NOT EXISTS rider_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    id_card VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    id_photo VARCHAR(255) NOT NULL,
    id_photo_back VARCHAR(255) NOT NULL,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Address table
CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(64) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    building INT NOT NULL,
    floor INT NOT NULL,
    detail VARCHAR(255) NOT NULL,
    is_default TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(128) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    image VARCHAR(255),
    description TEXT,
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    rider_id INT,
    address_id INT NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0: pending, 1: delivering, 2: completed, 3: cancelled',
    total_amount DECIMAL(10, 2) NOT NULL,
    delivery_fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (rider_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Earnings table
CREATE TABLE IF NOT EXISTS earnings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rider_id INT NOT NULL,
    order_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(32) DEFAULT 'delivery_fee',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rider_id) REFERENCES users(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Riders table
CREATE TABLE IF NOT EXISTS riders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    real_name VARCHAR(64) NOT NULL,
    id_card VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    level ENUM('normal', 'silver', 'gold') DEFAULT 'normal',
    balance DECIMAL(10, 2) DEFAULT 0.00,
    total_orders INT DEFAULT 0,
    total_earnings DECIMAL(10, 2) DEFAULT 0.00,
    payment_code VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_openid ON users(openid);
CREATE INDEX idx_users_is_rider ON users(is_rider);
CREATE INDEX idx_rider_applications_user_id ON rider_applications(user_id);
CREATE INDEX idx_address_user_id ON address(user_id);
CREATE INDEX idx_categories_sort_order ON categories(sort_order);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_rider_id ON orders(rider_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_deleted ON orders(deleted);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_earnings_rider_id ON earnings(rider_id);
CREATE INDEX idx_riders_user_id ON riders(user_id);
CREATE INDEX idx_riders_status ON riders(status);
