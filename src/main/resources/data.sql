-- Insert System Settings
INSERT IGNORE INTO system_settings (allow_registrations, maintenance_mode, patient_portal_active, doctor_portal_active, pharmacist_portal_active)
VALUES (true, false, true, true, true);

-- Insert Users (Passwords are BCrypt hashed "123456")
INSERT IGNORE INTO users (name, email, password, role, created_at) VALUES 
('System Admin', 'admin@admin.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_ADMIN', NOW()),
('Dr. Rahul Mehta', 'rahul@doctor.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_DOCTOR', NOW()),
('Dr. Sarah Chen', 'sarah@doctor.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_DOCTOR', NOW()),
('HealthPlus Pharmacy', 'healthplus@pharmacy.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PHARMACIST', NOW()),
('John Doe', 'john@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW()),
('Alice Morgan', 'alice@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW()),
('Bob Williams', 'bob@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW());

-- Insert Role specific profiles using email lookups to prevent hardcoded ID conflicts
INSERT IGNORE INTO doctors (id, specialization, department, license, online, status)
VALUES
((SELECT id FROM users WHERE email='rahul@doctor.com'), 'General Medicine', 'General Medicine', 'MD-10001', false, 'VERIFIED'),
((SELECT id FROM users WHERE email='sarah@doctor.com'), 'Cardiology', 'Cardiology', 'MD-10002', true, 'VERIFIED');

INSERT IGNORE INTO patients (id, age, gender, contact, allergies, medical_history, blood_type, height, weight, registration_date, status)
VALUES 
((SELECT id FROM users WHERE email='john@patient.com'), 35, 'MALE', '(555) 123-4567', 'Penicillin', 'Mild hypertension', 'O+', '180cm', '80kg', CURDATE(), 'ACTIVE'),
((SELECT id FROM users WHERE email='alice@patient.com'), 42, 'FEMALE', '(555) 201-0001', 'None', 'Seasonal allergies', 'A+', '165cm', '62kg', CURDATE(), 'ACTIVE'),
((SELECT id FROM users WHERE email='bob@patient.com'), 58, 'MALE', '(555) 201-0002', 'Sulfa drugs', 'Type 2 diabetes', 'B+', '178cm', '88kg', CURDATE(), 'ACTIVE');

INSERT IGNORE INTO pharmacies (id, pharmacy_name, license_number, phone, address, operating_hours, online, status)
VALUES 
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'HealthPlus Pharmacy', 'PH-98765', '(555) 987-6543', '123 Health Ave, Medical District', 'Mon-Sat: 8AM-9PM', true, 'ACTIVE');

-- Insert Initial Inventory for Pharmacy
INSERT IGNORE INTO inventory_items (pharmacy_id, name, category, stock, updated_at) VALUES
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'Amoxicillin 500mg', 'ANTIBIOTIC', 150, NOW()),
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'Lisinopril 10mg', 'BLOOD_PRESSURE', 200, NOW()),
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'Atorvastatin 20mg', 'CHOLESTEROL', 180, NOW()),
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'Metformin 500mg', 'DIABETES', 300, NOW()),
((SELECT id FROM users WHERE email='healthplus@pharmacy.com'), 'Ibuprofen 400mg', 'PAIN_RELIEF', 40, NOW()); 

-- Past visits so these patients appear in doctor portal lists
INSERT IGNORE INTO appointments (patient_id, doctor_id, date, time, type, duration, status, created_at) VALUES
((SELECT id FROM users WHERE email='john@patient.com'), (SELECT id FROM users WHERE email='rahul@doctor.com'), DATE_SUB(CURDATE(), INTERVAL 14 DAY), '09:00', 'IN_PERSON', 30, 'COMPLETED', NOW()),
((SELECT id FROM users WHERE email='alice@patient.com'), (SELECT id FROM users WHERE email='rahul@doctor.com'), DATE_SUB(CURDATE(), INTERVAL 7 DAY), '10:30', 'VIRTUAL', 30, 'COMPLETED', NOW()),
((SELECT id FROM users WHERE email='bob@patient.com'), (SELECT id FROM users WHERE email='sarah@doctor.com'), DATE_SUB(CURDATE(), INTERVAL 3 DAY), '14:00', 'IN_PERSON', 30, 'COMPLETED', NOW());

-- Insert Sample Activity Logs
INSERT IGNORE INTO activity_logs (actor, action, timestamp) VALUES
('System', 'System initialized with seed data', NOW()),
('System Admin', 'Created initial doctor profiles', NOW()),
('System Admin', 'Seeded dashboard users (3 patients, 2 doctors, 1 admin, 1 pharmacy)', NOW());
