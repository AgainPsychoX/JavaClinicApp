
DROP TYPE IF EXISTS user_role;
CREATE TYPE user_role AS ENUM (
    'ANONYMOUS',
    'PATIENT',
    'RECEPTION',
    'NURSE',
    'DOCTOR',
    'ADMIN'
)
