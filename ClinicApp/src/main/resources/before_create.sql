
DROP TYPE IF EXISTS user_role;
CREATE TYPE user_role AS ENUM (
    'ANONYMOUS',
    'PATIENT',
    'RECEPTION',
    'NURSE',
    'DOCTOR',
    'ADMIN'
);

DROP TYPE IF EXISTS schedule_simple_entry_type;
CREATE TYPE schedule_simple_entry_type AS ENUM (
    'NONE',
    'OPEN',
    'CLOSED',
    'VACATION',
    'HOLIDAYS',
    'SICK_LEAVE',
    'EMERGENCY_LEAVE',
    'APPOINTMENT',
    'EXTRA',
    'OTHER'
);
