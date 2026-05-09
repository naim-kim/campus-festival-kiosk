-- MySQL schema for naimkim_1 menu 주문/웨이팅
-- Run on MySQL (InnoDB). Recommended DB charset: utf8mb4.

-- 0) Optional: create database
-- CREATE DATABASE IF NOT EXISTS naimkim_menu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
-- USE naimkim_menu;

-- 1) Orders (결제 전/후 상태 + 총액)
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT NOT NULL AUTO_INCREMENT,
  status VARCHAR(32) NOT NULL,
  total_price INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_orders_created_at (created_at),
  KEY idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) Order items (메뉴 스냅샷)
CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  menu_code VARCHAR(64) NOT NULL,
  menu_name VARCHAR(128) NOT NULL,
  unit_price INT NOT NULL,
  quantity INT NOT NULL,
  ade_choice VARCHAR(64) NULL,
  PRIMARY KEY (id),
  KEY idx_order_items_order_id (order_id),
  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) Order item toppings (토핑 리스트 스냅샷)
CREATE TABLE IF NOT EXISTS order_item_toppings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL,
  topping_name VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_o_it_toppings_order_item_id (order_item_id),
  CONSTRAINT fk_order_item_toppings_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_items(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) Waiting tickets (대기번호: 날짜별 1..N)
CREATE TABLE IF NOT EXISTS waiting_tickets (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  business_date DATE NOT NULL,
  waiting_number INT NOT NULL,
  status VARCHAR(32) NOT NULL,
  phone_number VARCHAR(20) NULL,
  completed_at TIMESTAMP(6) NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_waiting_date_number (business_date, waiting_number),
  KEY idx_waiting_order_id (order_id),
  KEY idx_waiting_date (business_date),
  CONSTRAINT fk_waiting_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- If you already created waiting_tickets, apply:
-- ALTER TABLE waiting_tickets ADD COLUMN completed_at TIMESTAMP(6) NULL;

-- Helpful queries:
-- 오늘 웨이팅 현황
-- SELECT waiting_number, status, phone_number, created_at
-- FROM waiting_tickets
-- WHERE business_date = CURDATE()
-- ORDER BY waiting_number ASC;

-- 최근 주문 20개
-- SELECT id, status, total_price, created_at
-- FROM orders
-- ORDER BY id DESC
-- LIMIT 20;

