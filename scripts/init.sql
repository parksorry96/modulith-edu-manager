-- Education Manager Database Initialization Script
-- This script creates the basic database structure for the education management system

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schemas for different modules (Spring Modulith approach)
CREATE SCHEMA IF NOT EXISTS user_module;
CREATE SCHEMA IF NOT EXISTS student_module;
CREATE SCHEMA IF NOT EXISTS course_module;

-- Set default search path
SET search_path TO public, user_module, student_module, course_module;

-- Create audit columns function for automatic timestamping
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO edumanager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_module TO edumanager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA student_module TO edumanager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA course_module TO edumanager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO edumanager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_module TO edumanager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA student_module TO edumanager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA course_module TO edumanager;