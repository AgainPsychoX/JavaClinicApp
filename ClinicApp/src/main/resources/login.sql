
-- Anonymous user
DO $$
    BEGIN
        IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'anonymous')
        THEN
            DROP OWNED BY anonymous;
        END IF;
    END
$$;

DROP USER IF EXISTS anonymous;
CREATE USER anonymous WITH PASSWORD 'anonymous';

REVOKE ALL ON DATABASE clinic FROM anonymous;


-- Function to get user internal name (username to login to database) for given email or PESEL (for patients)
CREATE OR REPLACE FUNCTION public.get_user_internal_name(email_or_pesel VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
    SECURITY DEFINER
AS $$
    DECLARE
        result VARCHAR := CONCAT('u_', LOWER(MD5(email_or_pesel))); -- if not found return anything to prevent scanning
    BEGIN
        SELECT users.internal_name INTO result
            FROM public.users LEFT JOIN public.patients p ON users.id = p.id
            WHERE users.email LIKE email_or_pesel OR p.pesel LIKE email_or_pesel;
        RETURN result;
    END;
$$;

GRANT EXECUTE ON FUNCTION public.get_user_internal_name TO anonymous;
