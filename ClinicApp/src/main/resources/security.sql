
SET client_min_messages = warning; -- prevent notices about things not existing and being skipped when dropping

--------------------------------------------------------------------------------
-- Login
--------------------------------------------------------------------------------

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
        result VARCHAR := NULL;
    BEGIN
        SELECT users.internal_name INTO result
            FROM public.users LEFT JOIN public.patients p ON users.id = p.id
            WHERE users.email LIKE email_or_pesel OR p.pesel LIKE email_or_pesel;
        IF result IS NULL THEN
            RETURN CONCAT('u_', LOWER(MD5(email_or_pesel))); -- if not found return anything to prevent scanning
        END IF;
        RETURN result;
    END;
$$;

GRANT EXECUTE ON FUNCTION public.get_user_internal_name TO anonymous;

-- Function that creates a database user
CREATE OR REPLACE FUNCTION public.create_database_user(uname varchar, passwrd varchar) RETURNS void
    LANGUAGE plpgsql
    SECURITY DEFINER
AS $$
BEGIN
    EXECUTE FORMAT('CREATE USER %I LOGIN ENCRYPTED PASSWORD %L IN ROLE gp_patients', uname, passwrd);
END;
$$;

REVOKE EXECUTE ON FUNCTION public.create_database_user FROM gp_receptionists, gp_doctors, gp_admins;



--------------------------------------------------------------------------------
-- Roles
--------------------------------------------------------------------------------

GRANT USAGE ON schema public TO PUBLIC;

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

GRANT EXECUTE ON FUNCTION public.create_database_user TO gp_receptionists, gp_doctors, gp_admins;

-- Note for admin users:
-- > The role attributes `LOGIN`, `SUPERUSER`, `CREATEDB`, and `CREATEROLE` can be thought of as special privileges,
-- > but they are never inherited as ordinary privileges on database objects are.
-- >
-- > &mdash; <cite>https://www.postgresql.org/docs/current/role-membership.html</cite>
-- so those need to be explicitly added.



--------------------------------------------------------------------------------
-- Utils functions
--------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION minute_of_day(p_timestamp TIMESTAMP WITH TIME ZONE)
    RETURNS INTEGER
    LANGUAGE sql
AS $$
    SELECT (EXTRACT(MINUTE FROM p_timestamp) + EXTRACT(HOUR FROM p_timestamp) * 60)::int;
$$;



--------------------------------------------------------------------------------
-- Schedule logic
--------------------------------------------------------------------------------

-- View to provide info about schedule entries of users with schedules (doctors) without leaking details.
CREATE OR REPLACE VIEW schedule_busy_view AS
    SELECT user_id, "begin", "end", type FROM schedule_simple_entries WHERE type NOT IN ('OPEN', 'EXTRA')
    UNION
        SELECT
            patient_id AS user_id,
            date AS "begin",
            (date + duration * INTERVAL '1 minute') AS "end",
            'APPOINTMENT'::schedule_simple_entry_type AS type
        FROM appointments
    UNION
        SELECT
            doctor_id AS user_id,
            date AS "begin",
            (date + duration * INTERVAL '1 minute') AS "end",
            'APPOINTMENT'::schedule_simple_entry_type AS type
        FROM appointments
    ORDER BY user_id, "begin"
;

GRANT SELECT ON schedule_busy_view TO PUBLIC;

-- Function to get effective timetable for given user for give timestamp
CREATE OR REPLACE FUNCTION public.get_effective_timetable(p_user_id INTEGER, p_date TIMESTAMP WITH TIME ZONE)
    RETURNS RECORD
    LANGUAGE plpgsql
AS $$
	DECLARE
		v_result RECORD;
    BEGIN
		SELECT * FROM timetables INTO v_result
			WHERE user_id = p_user_id AND effective_date <= p_date
			ORDER BY effective_date DESC
			LIMIT 1;
		RETURN v_result;
    END;
$$;

GRANT EXECUTE ON FUNCTION public.get_effective_timetable TO PUBLIC;

-- Function to validate new appointment addition
CREATE OR REPLACE FUNCTION public.validate_new_appointment(p_patient_id INTEGER, p_doctor_id INTEGER, p_begin TIMESTAMP, p_duration INTEGER)
    RETURNS INTEGER
    LANGUAGE plpgsql
    SECURITY DEFINER
AS $$
    DECLARE
        v_end TIMESTAMP;
        v_doctor_timetable RECORD;
        v_start_minute INTEGER;
        v_end_minute INTEGER;
        v_max_days_in_advance INTEGER;
    BEGIN
        IF p < 5 OR 12 * 60 < p THEN
            RETURN 1; -- Duration must be at least 5 minutes and max 12 hours
        END IF;

        v_end := p_begin + p_duration * INTERVAL '1 minute';
        SELECT max_days_in_advance INTO v_max_days_in_advance FROM doctors WHERE id = p_doctor_id;
        IF CURRENT_TIMESTAMP + (v_max_days_in_advance * INTERVAL '1 day') < v_end THEN
            RETURN 2; -- Cannot add appointments beyond max days in advance specified by the doctor
        END IF;

        v_doctor_timetable := public.get_effective_timetable(p_doctor_id, p_begin);
        IF v_doctor_timetable != public.get_effective_timetable(p_doctor_id, v_end) THEN
            RETURN 3; -- Appointment cannot span over multiple timetables
        END IF;

        v_start_minute := minute_of_day(p_from);
        v_end_minute := minute_of_day(p_to);
        IF (
            NOT EXISTS (
                -- We assume timetables_entries are consolidated concatenated if they touch,
                -- and that appointments cannot span on multiple days (like thought mid-night).
                SELECT 1 FROM timetable_entries
                WHERE timetable_id = v_doctor_timetable.id
                    AND start_minute <= v_start_minute AND v_end_minute <= end_minute
            )
            AND NOT EXISTS (
                SELECT 1 FROM schedule_simple_entries
                WHERE user_id = p_doctor_id
                    AND "begin" <= p_begin AND p_begin <= "end"
                    AND type = 'EXTRA'
            )
        ) THEN
            RETURN 4; -- Doctor doesn't work in specified range (or at least, not fully)
        END IF;

        IF EXISTS (
            SELECT 1 FROM schedule_busy_view
            WHERE user_id = p_doctor_id AND ("begin", "end") OVERLAPS (p_begin, v_end)
        ) THEN
            RETURN 5; -- Doctor is busy already
        END IF;

        RETURN 0;
    END;
$$;

GRANT EXECUTE ON FUNCTION public.validate_new_appointment TO PUBLIC;



--------------------------------------------------------------------------------
-- Row Level Security
--------------------------------------------------------------------------------

-- Make sure current user (superuser creating database) has RLS bypass enabled
ALTER ROLE CURRENT_USER WITH BYPASSRLS;


--------------------------------------------------------------------------------
-- `appointments`

ALTER TABLE public.appointments ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.appointments TO gp_admins, gp_patients, gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS admin ON public.appointments;
CREATE POLICY admin ON public.appointments FOR ALL TO gp_admins, gp_receptionists
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.appointments TO gp_patients, gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS insert_own_as_patient ON public.appointments;
CREATE POLICY insert_own_as_patient ON public.appointments FOR INSERT TO gp_patients
    WITH CHECK (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS insert_as_reception ON public.appointments;
CREATE POLICY insert_as_reception ON public.appointments FOR INSERT TO gp_receptionists;

DROP POLICY IF EXISTS insert_own_as_doctor ON public.appointments;
CREATE POLICY insert_own_as_doctor ON public.appointments FOR INSERT TO gp_doctors
    WITH CHECK (doctor_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.appointments TO gp_patients, gp_receptionists, gp_doctors, gp_admins;

DROP POLICY IF EXISTS select_own_as_patient ON public.appointments;
CREATE POLICY select_own_as_patient ON public.appointments FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS select_as_staff ON public.appointments;
CREATE POLICY select_as_staff ON public.appointments FOR SELECT TO gp_receptionists, gp_nurses, gp_doctors
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.appointments TO gp_patients, gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS update_own_as_patient ON public.appointments;
CREATE POLICY update_own_as_patient ON public.appointments FOR UPDATE TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER))
    WITH CHECK (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS update_as_reception ON public.appointments;
CREATE POLICY update_as_reception ON public.appointments FOR UPDATE TO gp_receptionists
    USING (TRUE);

DROP POLICY IF EXISTS update_own_as_doctor ON public.appointments;
CREATE POLICY update_own_as_doctor ON public.appointments FOR UPDATE TO gp_doctors
    USING (doctor_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER))
    WITH CHECK (doctor_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

----------------------------------------
-- DELETE

DROP POLICY IF EXISTS delete_own_as_patient ON public.appointments;
CREATE POLICY delete_own_as_patient ON public.appointments FOR DELETE TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS delete_as_reception ON public.appointments;
CREATE POLICY delete_as_reception ON public.appointments FOR DELETE TO gp_receptionists
    USING (TRUE);

DROP POLICY IF EXISTS delete_own_as_doctor ON public.appointments;
CREATE POLICY delete_own_as_doctor ON public.appointments FOR DELETE TO gp_doctors
    USING (doctor_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rules to validate update/inserts (dates, etc.)
-- TODO: policy to allow patients to add appointments only if they fit in schedule properly
-- TODO: trigger to generate notifications (if configured) on insert/update/delete

--------------------------------------------------------------------------------
-- `doctors`

ALTER TABLE public.doctors ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.doctors TO gp_admins;

DROP POLICY IF EXISTS admin ON public.doctors;
CREATE POLICY admin ON public.doctors FOR ALL TO gp_admins
    USING (TRUE);

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.doctors TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS select_as_anyone ON public.doctors;
CREATE POLICY select_as_anyone ON public.doctors FOR SELECT TO PUBLIC
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.doctors TO gp_doctors;

DROP POLICY IF EXISTS update_own_as_doctor ON public.doctors;
CREATE POLICY update_own_as_doctor ON public.doctors FOR UPDATE TO gp_doctors
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER))
    WITH CHECK (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: make sure names are the same as related user (yes, redundant by design)
-- TODO: test if setter in Java is coordinated (first user, then doctors table

--------------------------------------------------------------------------------
-- `notifications`

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.notifications TO gp_admins;

DROP POLICY IF EXISTS admin ON public.notifications;
CREATE POLICY admin ON public.notifications FOR ALL TO gp_admins
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.notifications TO PUBLIC;

DROP POLICY IF EXISTS insert_as_source ON public.notifications;
CREATE POLICY insert_as_source ON public.notifications FOR INSERT TO PUBLIC
    WITH CHECK (source_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: check if names are the same as related user (yes, redundant by design)

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.notifications TO PUBLIC;

DROP POLICY IF EXISTS select_as_source ON public.notifications;
CREATE POLICY select_as_source ON public.notifications FOR SELECT TO PUBLIC
    USING (source_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS select_as_destination ON public.notifications;
CREATE POLICY select_as_destination ON public.notifications FOR SELECT TO PUBLIC
    USING (destination_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.notifications TO PUBLIC;

DROP POLICY IF EXISTS update_on_read ON public.notifications;
CREATE POLICY update_on_read ON public.notifications FOR UPDATE TO PUBLIC
    USING (destination_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rule (or check?) to validate dates on insert

--------------------------------------------------------------------------------
-- `patients`

ALTER TABLE public.patients ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.patients TO gp_admins, gp_patients, gp_receptionists, gp_doctors, gp_nurses;

DROP POLICY IF EXISTS admin ON public.patients;
CREATE POLICY admin ON public.patients FOR ALL TO gp_admins, gp_patients, gp_receptionists, gp_doctors, gp_nurses
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.patients TO gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS insert_asdf ON public.patients;
CREATE POLICY insert_asdf ON public.patients FOR INSERT TO gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS insert_auth ON public.patients;
CREATE POLICY insert_auth ON public.patients FOR INSERT TO gp_receptionists, gp_doctors
    WITH CHECK (true);

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.patients TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS select_own_as_patient ON public.patients;
CREATE POLICY select_own_as_patient ON public.patients FOR SELECT TO gp_patients
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS select_as_staff ON public.patients;
CREATE POLICY select_as_staff ON public.patients FOR SELECT TO gp_receptionists, gp_nurses, gp_doctors
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.patients TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS update_own_as_patient ON public.patients;
CREATE POLICY update_own_as_patient ON public.patients FOR UPDATE TO gp_patients
    USING (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER))
    WITH CHECK (id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS update_as_receptionists ON public.patients;
CREATE POLICY update_as_receptionists ON public.patients FOR UPDATE TO gp_receptionists
    USING (TRUE);
-- TODO: rules for receptionists to be able to change only non-health details

DROP POLICY IF EXISTS update_as_nurse ON public.patients;
CREATE POLICY update_as_nurse ON public.patients FOR UPDATE TO gp_nurses
    USING (TRUE);
-- TODO: rules for nurses to be able to change only health details

DROP POLICY IF EXISTS update_as_doctor ON public.patients;
CREATE POLICY update_as_doctor ON public.patients FOR UPDATE TO gp_doctors
    USING (TRUE);

-- TODO: rules to validate update/inserts

--------------------------------------------------------------------------------
-- `prescriptions`

ALTER TABLE public.prescriptions ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.prescriptions TO gp_admins, gp_patients, gp_receptionists, gp_doctors, gp_nurses;

DROP POLICY IF EXISTS admin ON public.prescriptions;
CREATE POLICY admin ON public.prescriptions FOR ALL TO gp_admins, gp_patients, gp_receptionists, gp_doctors, gp_nurses
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.prescriptions TO gp_doctors;

DROP POLICY IF EXISTS insert_own_as_doctor ON public.prescriptions;
CREATE POLICY insert_own_as_doctor ON public.prescriptions FOR INSERT TO gp_doctors
    WITH CHECK (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.prescriptions TO gp_patients, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS select_own_as_patient ON public.prescriptions;
CREATE POLICY select_own_as_patient ON public.prescriptions FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS select_as_doctor ON public.prescriptions;
CREATE POLICY select_as_doctor ON public.prescriptions FOR SELECT TO gp_doctors
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.prescriptions TO gp_doctors;

DROP POLICY IF EXISTS update_own_as_doctor ON public.prescriptions;
CREATE POLICY update_own_as_doctor ON public.prescriptions FOR UPDATE TO gp_doctors
    USING (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

-- TODO: rules to validate update/inserts
-- TODO: should doctors be able to delete?

--------------------------------------------------------------------------------
-- `referrals`

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.referrals TO gp_admins;

DROP POLICY IF EXISTS admin ON public.referrals;
CREATE POLICY admin ON public.referrals FOR ALL TO gp_admins, gp_patients, gp_receptionists, gp_doctors, gp_nurses
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.referrals TO gp_doctors;

DROP POLICY IF EXISTS insert_own_as_doctor ON public.referrals;
CREATE POLICY insert_own_as_doctor ON public.referrals FOR INSERT TO gp_doctors
    WITH CHECK (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.referrals TO gp_patients, gp_nurses, gp_doctors, gp_receptionists;

DROP POLICY IF EXISTS select_own_as_patient ON public.referrals;
CREATE POLICY select_own_as_patient ON public.referrals FOR SELECT TO gp_patients
    USING (patient_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS select_as_personel ON public.referrals;
CREATE POLICY select_as_personel ON public.referrals FOR SELECT TO gp_doctors, gp_nurses, gp_receptionists
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.referrals TO gp_doctors, gp_nurses;

DROP POLICY IF EXISTS update_own_as_doctor ON public.referrals;
CREATE POLICY update_own_as_doctor ON public.referrals FOR UPDATE TO gp_doctors
    USING (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));

DROP POLICY IF EXISTS update_as_nurse ON public.referrals;
CREATE POLICY update_as_nurse ON public.referrals FOR UPDATE TO  gp_nurses
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT DELETE ON TABLE public.referrals TO gp_doctors;

DROP POLICY IF EXISTS delete_own_as_doctor ON public.referrals;
CREATE POLICY delete_own_as_doctor ON public.referrals FOR DELETE TO gp_doctors
    USING (added_by_user_id = (SELECT id FROM public.users WHERE internal_name = CURRENT_USER));


-- TODO: rules to validate update/inserts

--------------------------------------------------------------------------------
-- `schedule_simple_entries`

--ALTER TABLE public.schedule_simple_entries ENABLE ROW LEVEL SECURITY;
GRANT ALL ON TABLE public.schedule_simple_entries TO gp_patients, gp_receptionists, gp_nurses, gp_doctors, gp_admins;

--------------------------------------------------------------------------------
-- `timetable`

--ALTER TABLE public.timetables ENABLE ROW LEVEL SECURITY;
GRANT ALL ON TABLE public.timetables TO gp_patients, gp_receptionists, gp_nurses, gp_doctors, gp_admins;

--------------------------------------------------------------------------------
-- `timetable_entries`

--ALTER TABLE public.timetable_entries ENABLE ROW LEVEL SECURITY;
GRANT ALL ON TABLE public.timetable_entries TO gp_patients, gp_receptionists, gp_nurses, gp_doctors, gp_admins;

-- TODO: fix & test permissions, make all those rules easier to read/maintain

--------------------------------------------------------------------------------
-- `users`

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.users TO gp_admins;

DROP POLICY IF EXISTS admin ON public.users;
CREATE POLICY admin ON public.users FOR ALL TO gp_admins
    USING (TRUE);

----------------------------------------
-- INSERT

GRANT INSERT ON TABLE public.users TO gp_receptionists, gp_doctors;

DROP POLICY IF EXISTS insert_asdf ON public.users;
CREATE POLICY insert_asdf ON public.users FOR INSERT TO gp_receptionists, gp_doctors
    WITH CHECK (role = 'PATIENT' AND LENGTH(internal_name) = 0);

DROP POLICY IF EXISTS insert_auth ON public.users;
CREATE POLICY insert_auth ON public.users FOR INSERT TO gp_receptionists, gp_doctors
    WITH CHECK (true);

----------------------------------------
-- SELECT

GRANT SELECT ON TABLE public.users TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS select_own ON public.users;
CREATE POLICY select_own ON public.users FOR SELECT TO PUBLIC
    USING (internal_name = CURRENT_USER);

CREATE POLICY select_doctors_as_anyone ON public.users FOR SELECT TO PUBLIC
    USING (EXISTS (SELECT 1 FROM doctors WHERE doctors.id = users.id));
-- TODO: how to protect doctors email/phone? actually do we need to protect it? 

DROP POLICY IF EXISTS select_as_staff ON public.users;
CREATE POLICY select_as_staff ON public.users FOR SELECT TO gp_receptionists, gp_nurses, gp_doctors
    USING (TRUE);

----------------------------------------
-- UPDATE

GRANT UPDATE ON TABLE public.users TO gp_patients, gp_receptionists, gp_nurses, gp_doctors;

DROP POLICY IF EXISTS update_own ON public.users;
CREATE POLICY update_own ON public.users FOR UPDATE TO PUBLIC
    USING (internal_name = CURRENT_USER);

DROP POLICY IF EXISTS select_asdf ON public.users;
CREATE POLICY select_asdf ON public.users FOR UPDATE TO gp_receptionists, gp_doctors
    USING (role = 'PATIENT')
    WITH CHECK (role = 'PATIENT');

DROP RULE IF EXISTS validate ON public.users;
CREATE RULE validate AS ON UPDATE TO public.users
    WHERE NEW.id <> OLD.id OR NEW.internal_name <> OLD.internal_name
    DO INSTEAD NOTHING;

-- TODO: rules to validate update/inserts
-- TODO: use trigger to create database account (and fill internal_name)
-- TODO: test receptionist/doctor being unable change role of patient
-- TODO: think how receptionist/doctor could give new patient password. default = pesel + current date?



--------------------------------------------------------------------------------
-- Audit
--------------------------------------------------------------------------------

-- TODO: think about audit/log tables, maintained by triggers?
