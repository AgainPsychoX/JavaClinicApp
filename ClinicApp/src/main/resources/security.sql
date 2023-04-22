
SET client_min_messages = warning; -- prevent notices about things not existing and being skipped when dropping

--------------------------------------------------------------------------------
-- Login

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

--------------------------------------------------------------------------------
-- Roles

DROP ROLE IF EXISTS gp_patients;
DROP ROLE IF EXISTS gp_receptionists;
DROP ROLE IF EXISTS gp_nurses;
DROP ROLE IF EXISTS gp_doctors;
DROP ROLE IF EXISTS gp_admins;

CREATE ROLE gp_patients;
CREATE ROLE gp_receptionists;
CREATE ROLE gp_nurses;
CREATE ROLE gp_doctors;
CREATE ROLE gp_admins SUPERUSER CREATEDB CREATEROLE REPLICATION BYPASSRLS;

-- Note for admin users:
-- > The role attributes `LOGIN`, `SUPERUSER`, `CREATEDB`, and `CREATEROLE` can be thought of as special privileges,
-- > but they are never inherited as ordinary privileges on database objects are.
-- >
-- > &mdash; <cite>https://www.postgresql.org/docs/current/role-membership.html</cite>
-- so those need to be explicitly added.

--------------------------------------------------------------------------------
-- Row Level Security

-- Make sure current user (superuser creating database) has RLS bypass enabled
ALTER ROLE CURRENT_USER WITH BYPASSRLS;

-- Enable RLS for tables
ALTER TABLE public.appointments         ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.doctor_specialities  ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.doctors              ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications        ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.patients             ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.prescriptions        ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.referrals            ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.schedule_entries     ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.users                ENABLE ROW LEVEL SECURITY;

----------------------------------------
-- `appointments` table policies and rules

DROP POLICY IF EXISTS allow_select_own ON public.appointments;
CREATE POLICY allow_select_own ON public.appointments FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

GRANT SELECT ON TABLE public.appointments TO gp_nurses;
GRANT SELECT, UPDATE, INSERT ON TABLE public.appointments TO gp_receptionists, gp_doctors;

----------------------------------------
-- `doctor_specialities` table policies and rules

-- TODO: rethink if we want to keep it separate or move to doctors table

GRANT SELECT ON TABLE public.doctor_specialities TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

----------------------------------------
-- `doctors` table policies and rules

DROP POLICY IF EXISTS allow_update_own ON public.doctors;
CREATE POLICY allow_update_own ON public.doctors FOR UPDATE TO gp_doctors
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

GRANT SELECT ON TABLE public.doctors TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

----------------------------------------
-- `notifications` table policies and rules

DROP POLICY IF EXISTS allow_select_sent ON public.notifications;
CREATE POLICY allow_select_sent ON public.notifications FOR SELECT TO PUBLIC
    USING (source_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS allow_select_received ON public.notifications;
CREATE POLICY allow_select_received ON public.notifications FOR SELECT TO PUBLIC
    USING (destination_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rule to protect from sending as someone else

GRANT SELECT ON TABLE public.notifications TO gp_receptionists;
GRANT INSERT ON TABLE public.notifications TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

----------------------------------------
-- `patients` table policies and rules

DROP POLICY IF EXISTS allow_select_own ON public.patients;
CREATE POLICY allow_select_own ON public.patients FOR SELECT TO PUBLIC
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS allow_update_own ON public.patients;
CREATE POLICY allow_update_own ON public.patients FOR UPDATE TO PUBLIC
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rules to validate update/inserts

GRANT SELECT, UPDATE ON TABLE public.patients TO gp_nurses;
GRANT SELECT, UPDATE, INSERT ON TABLE public.patients TO gp_receptionists, gp_doctors;

----------------------------------------
-- `prescriptions` table policies and rules

DROP POLICY IF EXISTS allow_select_own ON public.prescriptions;
CREATE POLICY allow_select_own ON public.prescriptions FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS allow_update_added ON public.prescriptions;
CREATE POLICY allow_update_added ON public.prescriptions FOR UPDATE TO gp_doctors
    USING (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rules to prevent doctors adding prescriptions as other doctor
-- TODO: rules to validate update/inserts

GRANT SELECT ON TABLE public.prescriptions TO gp_receptionists, gp_nurses;
GRANT SELECT, INSERT ON TABLE public.prescriptions TO gp_doctors;

----------------------------------------
-- `referrals` table policies and rules

DROP POLICY IF EXISTS allow_select_own ON public.referrals;
CREATE POLICY allow_select_own ON public.referrals FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS allow_update_added ON public.referrals;
CREATE POLICY allow_update_added ON public.referrals FOR UPDATE TO gp_doctors
    USING (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS allow_update_by_nurses ON public.referrals;
CREATE POLICY allow_update_by_nurses ON public.referrals FOR UPDATE TO gp_nurses
    USING (point_of_interest = '^^^INTERNAL NURSES^^^');

-- TODO: rules to prevent doctors adding referrals as other doctor
-- TODO: rules to validate update/inserts

GRANT SELECT ON TABLE public.referrals TO gp_receptionists, gp_nurses;
GRANT SELECT, INSERT ON TABLE public.referrals TO gp_doctors;

----------------------------------------
-- `schedule_entries` table policies and rules

-- TODO: rethink whole schedule/timetable systems

----------------------------------------
-- `users` table policies and rules

DROP POLICY IF EXISTS allow_select_own ON public.users;
CREATE POLICY allow_select_own ON public.users FOR SELECT TO PUBLIC
    USING (internal_name = CURRENT_USER);

DROP POLICY IF EXISTS allow_update_own ON public.users;
CREATE POLICY allow_update_own ON public.users FOR UPDATE TO PUBLIC
    USING (internal_name = CURRENT_USER);

DROP RULE IF EXISTS validate ON public.users;
CREATE RULE validate AS ON UPDATE TO public.users
    WHERE NEW.id <> OLD.id OR NEW.internal_name <> OLD.internal_name
    DO INSTEAD NOTHING;

GRANT SELECT ON TABLE public.users TO gp_nurses;
GRANT SELECT, UPDATE, INSERT ON TABLE public.users TO gp_receptionists, gp_doctors;

--------------------------------------------------------------------------------
-- Audit

-- TODO: think about audit/log tables, maintained by triggers?
