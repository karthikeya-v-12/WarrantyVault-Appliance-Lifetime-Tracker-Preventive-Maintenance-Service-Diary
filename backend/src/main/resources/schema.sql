-- =============================================================================
-- WarrantyVault: Household Asset Lifetime & Preventive Maintenance Platform
-- PostgreSQL DDL Schema & Initial Data
-- =============================================================================

-- Clean up existing tables if rebuilding
DROP TABLE IF EXISTS service_logs CASCADE;
DROP TABLE IF EXISTS maintenance_schedules CASCADE;
DROP TABLE IF EXISTS household_assets CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- -----------------------------------------------------------------------------
-- 1. USERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. HOUSEHOLD_ASSETS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE household_assets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    asset_name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('ELECTRONICS', 'APPLIANCE', 'VEHICLE', 'HOME')),
    purchase_date DATE NOT NULL,
    purchase_price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    warranty_months INT NOT NULL CHECK (warranty_months >= 0),
    extended_warranty_months INT NOT NULL DEFAULT 0 CHECK (extended_warranty_months >= 0),
    warranty_expiry_date DATE NOT NULL,
    serial_number VARCHAR(100),
    invoice_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RETIRED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 3. MAINTENANCE_SCHEDULES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE maintenance_schedules (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES household_assets(id) ON DELETE CASCADE,
    task_name VARCHAR(255) NOT NULL,
    interval_months INT NOT NULL CHECK (interval_months > 0),
    last_serviced_date DATE,
    next_due_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 4. SERVICE_LOGS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE service_logs (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES household_assets(id) ON DELETE CASCADE,
    service_date DATE NOT NULL,
    service_center VARCHAR(255),
    technician_contact VARCHAR(100),
    cost NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (cost >= 0),
    work_notes TEXT,
    receipt_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- -----------------------------------------------------------------------------
-- Foreign Key Indexes
CREATE INDEX idx_household_assets_user_id ON household_assets(user_id);
CREATE INDEX idx_maintenance_schedules_asset_id ON maintenance_schedules(asset_id);
CREATE INDEX idx_service_logs_asset_id ON service_logs(asset_id);

-- Date Lookups & Query Filters
CREATE INDEX idx_household_assets_warranty_expiry ON household_assets(warranty_expiry_date);
CREATE INDEX idx_household_assets_category_status ON household_assets(category, status);
CREATE INDEX idx_maintenance_schedules_next_due ON maintenance_schedules(next_due_date);
CREATE INDEX idx_service_logs_service_date ON service_logs(service_date DESC);

-- -----------------------------------------------------------------------------
-- SEED DATA (Default Initial Records)
-- -----------------------------------------------------------------------------
INSERT INTO users (id, name, email, created_at)
VALUES 
    (1, 'Alex Morgan', 'alex.morgan@example.com', CURRENT_TIMESTAMP);

-- Sample Assets
-- Asset 1: MacBook Pro (Active, Expiring in 20 days)
INSERT INTO household_assets (id, user_id, asset_name, category, purchase_date, purchase_price, warranty_months, extended_warranty_months, warranty_expiry_date, serial_number, invoice_url, status)
VALUES 
    (1, 1, 'MacBook Pro 16" M3 Max', 'ELECTRONICS', CURRENT_DATE - INTERVAL '11 months' - INTERVAL '10 days', 3499.00, 12, 0, CURRENT_DATE + INTERVAL '20 days', 'C02G9988MD6T', 'https://invoices.vault.local/inv-1001.pdf', 'ACTIVE');

-- Asset 2: Bosch 800 Series Dishwasher (Active, Expiring in 180 days)
INSERT INTO household_assets (id, user_id, asset_name, category, purchase_date, purchase_price, warranty_months, extended_warranty_months, warranty_expiry_date, serial_number, invoice_url, status)
VALUES 
    (2, 1, 'Bosch 800 Series Dishwasher', 'APPLIANCE', CURRENT_DATE - INTERVAL '6 months', 1299.50, 12, 12, CURRENT_DATE + INTERVAL '18 months', 'BSH-882910-X', 'https://invoices.vault.local/inv-1002.pdf', 'ACTIVE');

-- Asset 3: Carrier Heat Pump HVAC (Active, Expired warranty)
INSERT INTO household_assets (id, user_id, asset_name, category, purchase_date, purchase_price, warranty_months, extended_warranty_months, warranty_expiry_date, serial_number, invoice_url, status)
VALUES 
    (3, 1, 'Carrier Infinity 24 Heat Pump', 'HOME', CURRENT_DATE - INTERVAL '28 months', 8500.00, 24, 0, CURRENT_DATE - INTERVAL '4 months', 'CAR-INF-77312', 'https://invoices.vault.local/inv-1003.pdf', 'ACTIVE');

-- Asset 4: Tesla Model Y (Active, Expiring in 14 days)
INSERT INTO household_assets (id, user_id, asset_name, category, purchase_date, purchase_price, warranty_months, extended_warranty_months, warranty_expiry_date, serial_number, invoice_url, status)
VALUES 
    (4, 1, 'Tesla Model Y Long Range', 'VEHICLE', CURRENT_DATE - INTERVAL '47 months' - INTERVAL '16 days', 52990.00, 48, 0, CURRENT_DATE + INTERVAL '14 days', '5YJ3E1EA8NF123456', 'https://invoices.vault.local/inv-1004.pdf', 'ACTIVE');

-- Reset BIGSERIAL sequence counters
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('household_assets_id_seq', (SELECT MAX(id) FROM household_assets));

-- Sample Maintenance Schedules
INSERT INTO maintenance_schedules (id, asset_id, task_name, interval_months, last_serviced_date, next_due_date)
VALUES
    -- Carrier Heat Pump: Filter Replacement due in 5 days (Upcoming alert!)
    (1, 3, 'High-Efficiency Air Filter Replacement', 3, CURRENT_DATE - INTERVAL '3 months' + INTERVAL '5 days', CURRENT_DATE + INTERVAL '5 days'),
    -- Carrier Heat Pump: Semi-Annual Coil Cleaning & Inspection (Overdue by 2 days)
    (2, 3, 'Condenser Coil Cleaning & Refrigerant Check', 6, CURRENT_DATE - INTERVAL '6 months' - INTERVAL '2 days', CURRENT_DATE - INTERVAL '2 days'),
    -- Dishwasher: Descaling & Filter Scrub due in 25 days
    (3, 2, 'Dishwasher Descale & Deep Filter Scrub', 6, CURRENT_DATE - INTERVAL '5 months' - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days'),
    -- Tesla: Cabin Air Filter & Brake Fluid Moisture Check
    (4, 4, 'Brake Fluid Moisture Inspection & Cabin Filter', 24, CURRENT_DATE - INTERVAL '23 months', CURRENT_DATE + INTERVAL '1 month');

SELECT setval('maintenance_schedules_id_seq', (SELECT MAX(id) FROM maintenance_schedules));

-- Sample Service Logs (Historical Maintenance)
INSERT INTO service_logs (id, asset_id, service_date, service_center, technician_contact, cost, work_notes, receipt_url)
VALUES
    (1, 3, CURRENT_DATE - INTERVAL '6 months' - INTERVAL '2 days', 'Metro Climate Control HVAC Specialists', '+1 (555) 345-6789', 245.00, 'Replaced blower motor capacitor and completed seasonal preventive tune-up.', 'https://receipts.vault.local/rcpt-001.pdf'),
    (2, 3, CURRENT_DATE - INTERVAL '3 months' + INTERVAL '5 days', 'Metro Climate Control HVAC Specialists', '+1 (555) 345-6789', 85.00, 'Replaced MERV-13 whole-house air filter. Checked static duct pressure.', 'https://receipts.vault.local/rcpt-002.pdf'),
    (3, 4, CURRENT_DATE - INTERVAL '12 months', 'Tesla Service Center - Downtown', '+1 (555) 789-0123', 180.00, 'Annual tire rotation, balance, windshield wiper blade replacement.', 'https://receipts.vault.local/rcpt-003.pdf');

SELECT setval('service_logs_id_seq', (SELECT MAX(id) FROM service_logs));
