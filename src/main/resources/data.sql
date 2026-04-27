-- Insert System Settings
INSERT INTO system_settings (allow_registrations, maintenance_mode, patient_portal_active, doctor_portal_active, pharmacist_portal_active)
VALUES (true, false, true, true, true);

-- Insert Users (Passwords are BCrypt hashed "123456")
INSERT INTO users (id, name, email, password, role, created_at) VALUES 
(1, 'System Admin', 'admin@admin.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_ADMIN', NOW()),
(2, 'Dr. Rahul Mehta', 'rahul@doctor.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_DOCTOR', NOW()),
(3, 'Dr. Sarah Chen', 'sarah@doctor.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_DOCTOR', NOW()),
(4, 'HealthPlus Pharmacy', 'healthplus@pharmacy.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PHARMACIST', NOW()),
(5, 'John Doe', 'john@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW()),
(6, 'Alice Morgan', 'alice@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW()),
(7, 'Bob Williams', 'bob@patient.com', '$2a$12$QgB2lyTRa3SjQz2mKwmeTucLlHFuwIRnJOqBi0gqmGQrhLOQtK0gW', 'ROLE_PATIENT', NOW());

-- Insert Role specific profiles
-- 1 admin, 2 doctors (2,3), 3 patients (5,6,7), 1 pharmacy (4)

INSERT INTO doctors (id, specialization, department, license, online, status)
VALUES
(2, 'General Medicine', 'General Medicine', 'MD-10001', false, 'VERIFIED'),
(3, 'Cardiology', 'Cardiology', 'MD-10002', true, 'VERIFIED');

INSERT INTO patients (id, age, gender, contact, allergies, medical_history, blood_type, height, weight, registration_date, status)
VALUES (5, 35, 'MALE', '(555) 123-4567', 'Penicillin', 'Mild hypertension', 'O+', '180cm', '80kg', CURDATE(), 'ACTIVE'),
(6, 42, 'FEMALE', '(555) 201-0001', 'None', 'Seasonal allergies', 'A+', '165cm', '62kg', CURDATE(), 'ACTIVE'),
(7, 58, 'MALE', '(555) 201-0002', 'Sulfa drugs', 'Type 2 diabetes', 'B+', '178cm', '88kg', CURDATE(), 'ACTIVE');

INSERT INTO pharmacies (id, pharmacy_name, license_number, phone, address, operating_hours, online, status)
VALUES (4, 'HealthPlus Pharmacy', 'PH-98765', '(555) 987-6543', '123 Health Ave, Medical District', 'Mon-Sat: 8AM-9PM', true, 'ACTIVE');

-- Insert Initial Inventory for Pharmacy
INSERT INTO inventory_items (pharmacy_id, name, category, stock, updated_at) VALUES
(4, 'Amoxicillin 500mg', 'ANTIBIOTIC', 150, NOW()),
(4, 'Lisinopril 10mg', 'BLOOD_PRESSURE', 200, NOW()),
(4, 'Atorvastatin 20mg', 'CHOLESTEROL', 180, NOW()),
(4, 'Metformin 500mg', 'DIABETES', 300, NOW()),
(4, 'Ibuprofen 400mg', 'PAIN_RELIEF', 40, NOW()); -- Low stock

-- Past visits so these patients appear in doctor portal lists
INSERT INTO appointments (patient_id, doctor_id, date, time, type, duration, status, created_at) VALUES
(5, 2, DATE_SUB(CURDATE(), INTERVAL 14 DAY), '09:00', 'IN_PERSON', 30, 'COMPLETED', NOW()),
(6, 2, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '10:30', 'VIRTUAL', 30, 'COMPLETED', NOW()),
(7, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '14:00', 'IN_PERSON', 30, 'COMPLETED', NOW());

-- Insert Sample Activity Logs
INSERT INTO activity_logs (actor, action, timestamp) VALUES
('System', 'System initialized with seed data', NOW()),
('System Admin', 'Created initial doctor profiles', NOW()),
('System Admin', 'Seeded dashboard users (3 patients, 2 doctors, 1 admin, 1 pharmacy)', NOW());
