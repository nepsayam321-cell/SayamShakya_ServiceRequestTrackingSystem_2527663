CREATE DATABASE srts_db;
USE srts_db;
-- USERS TABLE
-- Stores all users: Customers, Staff and Admin
CREATE TABLE users (
    userID             INT PRIMARY KEY AUTO_INCREMENT,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    password           VARCHAR(50)  NOT NULL,
    fullName           VARCHAR(100) NOT NULL,
    role               VARCHAR(20)  NOT NULL,
    contactNumber      VARCHAR(20),
    address            VARCHAR(200),
    specialization     VARCHAR(50),
    availabilityStatus BOOLEAN DEFAULT TRUE,
    adminLevel         INT DEFAULT 1
);

-- SERVICE TYPES TABLE 
-- Stores types of services e.g. IT Support, Laptop Servicing
CREATE TABLE service_types (
    serviceTypeID   INT PRIMARY KEY AUTO_INCREMENT,
    serviceTypeName VARCHAR(50) NOT NULL
);

-- SERVICE CATEGORIES TABLE
-- Stores categories e.g. Hardware, Software, Maintenance
CREATE TABLE service_categories (
    categoryID   INT PRIMARY KEY AUTO_INCREMENT,
    categoryName VARCHAR(50) NOT NULL
);

-- PRIORITY LEVELS TABLE
-- Stores priority levels: Rush and Standard
CREATE TABLE priority_levels (
    priorityID   INT PRIMARY KEY AUTO_INCREMENT,
    priorityName VARCHAR(20)  NOT NULL,
    description  VARCHAR(100)
);

-- SERVICE REQUESTS TABLE
-- Main table storing all service requests submitted by customers
CREATE TABLE service_requests (
    requestID        INT PRIMARY KEY AUTO_INCREMENT,
    categoryID       INT NOT NULL,
    serviceTypeID    INT NOT NULL,
    priorityID       INT NOT NULL,
    status           VARCHAR(30)  NOT NULL DEFAULT 'Pending',
    description      VARCHAR(500) NOT NULL,
    completionRemark VARCHAR(500) DEFAULT '',
    submissionDate   VARCHAR(20)  NOT NULL,
    customerID       INT NOT NULL,
    staffID          INT,
    FOREIGN KEY (customerID)    REFERENCES users(userID),
    FOREIGN KEY (staffID)       REFERENCES users(userID),
    FOREIGN KEY (categoryID)    REFERENCES service_categories(categoryID),
    FOREIGN KEY (serviceTypeID) REFERENCES service_types(serviceTypeID),
    FOREIGN KEY (priorityID)    REFERENCES priority_levels(priorityID)
);

-- INSERT SAMPLE USERS
-- Column order: userID, username, password, fullName, role, contactNumber, address, specialization, availabilityStatus, adminLevel
INSERT INTO users VALUES
(1,  'Diken',   'Diken123',   'Diken Shakya',   'Customer', '9803078644', 'Nagbahal', NULL,       TRUE, 1),
(2,  'Shrawan', 'Shrawan123', 'Shrawan Shakya',  'Customer', '9863523455', 'Daubahal', NULL,       TRUE, 1),
(10, 'Pujan',   'Pujan123',   'Pujan Shakya',    'Staff',    NULL,          NULL,      'Hardware', TRUE, 1),
(11, 'Rinesh',  'Rinesh123',  'Rinesh Shakya',   'Staff',    NULL,          NULL,      'Software', TRUE, 1),
(20, 'Sayam',   'Sayam123',   'Sayam Shakya',    'Admin',    NULL,          NULL,       NULL,      TRUE, 1);

-- Fix AUTO_INCREMENT so new users registered via the app
-- get IDs starting from 21 and do not conflict with sample data
ALTER TABLE users AUTO_INCREMENT = 21;

-- INSERT SERVICE TYPES
INSERT INTO service_types (serviceTypeName) VALUES
('IT Support'),
('Laptop Servicing');

-- ── INSERT SERVICE CATEGORIES
INSERT INTO service_categories (categoryName) VALUES
('Hardware'),
('Software'),
('Maintenance');

-- INSERT PRIORITY LEVELS
INSERT INTO priority_levels (priorityName, description) VALUES
('Rush',     'Urgent - handled as soon as possible'),
('Standard', 'Normal priority - handled in order received');

-- INSERT SAMPLE SERVICE REQUEST
INSERT INTO service_requests
(categoryID, serviceTypeID, priorityID, status, description,
 completionRemark, submissionDate, customerID, staffID)
VALUES
(1, 2, 1, 'Pending',
 'Laptop screen is cracked and needs replacing.',
 '', '2025-03-01', 1, NULL);

-- Fix AUTO_INCREMENT for service_requests too
ALTER TABLE service_requests AUTO_INCREMENT = 2;

-- VERIFY DATA
USE srts_db;
SELECT * FROM users;
SELECT * FROM service_types;
SELECT * FROM service_categories;
SELECT * FROM priority_levels;
SELECT * FROM service_requests;
SELECT * FROM users ORDER BY role;