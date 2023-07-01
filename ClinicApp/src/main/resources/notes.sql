RAISE EXCEPTION 'This file is meant for notes taking, not for execution';
--------------------------------------------------------------------------------

-- Force recreate schema
DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- Remove all roles (also users) starting with 'u_'
DO $$
DECLARE
    role_name TEXT;
BEGIN
    FOR role_name IN (SELECT rolname FROM pg_catalog.pg_roles WHERE rolname LIKE 'u\_%' ESCAPE '\')
    LOOP
        DROP OWNED BY anonymous;
		EXECUTE 'DROP ROLE ' || quote_ident(role_name);
    END LOOP;
END $$;
