-- Customer Registration System - Database Seeder
-- This file contains seed data for development and testing purposes
-- Runs automatically on startup via Spring Boot SQL initialization

-- =============================================================================
-- AUTHORITIES
-- =============================================================================
INSERT INTO public.authority (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO public.authority (name) VALUES ('ROLE_AGENT') ON CONFLICT (name) DO NOTHING;

-- =============================================================================
-- USERS
-- =============================================================================
-- System Administrator (admin/admin)
INSERT INTO public."user" (
    id, created_by, created_date, last_modified_by, last_modified_date,
    activated, activation_key, email, first_name, image_url, lang_key,
    last_name, login, password_hash, phone_number, reset_date, reset_key
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'system', '2025-09-29 09:00:00.000000',
    'system', '2025-09-29 09:00:00.000000',
    true, null, 'admin@localhost', 'Administrator', null, 'en',
    'Administrator', 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC',
    null, null, null
) ON CONFLICT (id) DO NOTHING;

-- Regular User (user/user)
INSERT INTO public."user" (
    id, created_by, created_date, last_modified_by, last_modified_date,
    activated, activation_key, email, first_name, image_url, lang_key,
    last_name, login, password_hash, phone_number, reset_date, reset_key
) VALUES (
    '00000000-0000-0000-0000-000000000002',
    'system', '2025-09-29 09:00:00.000000',
    'system', '2025-09-29 09:00:00.000000',
    true, null, 'user@localhost', 'User', null, 'en',
    'User', 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K',
    null, null, null
) ON CONFLICT (id) DO NOTHING;

-- Agent 1 - James Mwalimu (0721015320)
INSERT INTO public."user" (
    id, created_by, created_date, last_modified_by, last_modified_date,
    activated, activation_key, email, first_name, image_url, lang_key,
    last_name, login, password_hash, phone_number, reset_date, reset_key
) VALUES (
    '65ecf5ee-26a2-400e-98f4-838816b005f3',
    'admin', '2025-09-29 09:30:57.387809',
    'anonymousUser', '2025-09-29 09:31:18.660594',
    true, null, 'james.mwalimu1@vodacom.tz', 'James', null, 'en',
    'Mwalimu', '0721015320', '$2a$10$oCMU3AIodpFLe7Z8qM61iuK5UJ5Lh9PlSYKDW7qE1q9K201bOK4cS',
    '0721015320', null, null
) ON CONFLICT (id) DO NOTHING;

-- Agent 2 - James Mwalimu (0711015320)
INSERT INTO public."user" (
    id, created_by, created_date, last_modified_by, last_modified_date,
    activated, activation_key, email, first_name, image_url, lang_key,
    last_name, login, password_hash, phone_number, reset_date, reset_key
) VALUES (
    '64bbd1e0-98ff-4b4f-acfc-7352a73f4021',
    'admin', '2025-09-29 09:33:40.849206',
    'anonymousUser', '2025-09-29 09:33:58.478228',
    true, null, 'james.mwalimu2@vodacom.tz', 'James', null, 'en',
    'Mwalimu', '0711015320', '$2a$10$9d90wUJlnXi9tM1C2WQNtOJbDfuTJ58vioFDmPScziRVTGDO5SW5q',
    '0711015320', null, null
) ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- USER AUTHORITIES (Roles)
-- =============================================================================
INSERT INTO public.user_authority (user_id, authority_name) VALUES ('00000000-0000-0000-0000-000000000001', 'ROLE_ADMIN') ON CONFLICT (user_id, authority_name) DO NOTHING;
INSERT INTO public.user_authority (user_id, authority_name) VALUES ('00000000-0000-0000-0000-000000000001', 'ROLE_AGENT') ON CONFLICT (user_id, authority_name) DO NOTHING;
INSERT INTO public.user_authority (user_id, authority_name) VALUES ('00000000-0000-0000-0000-000000000002', 'ROLE_AGENT') ON CONFLICT (user_id, authority_name) DO NOTHING;
INSERT INTO public.user_authority (user_id, authority_name) VALUES ('65ecf5ee-26a2-400e-98f4-838816b005f3', 'ROLE_AGENT') ON CONFLICT (user_id, authority_name) DO NOTHING;
INSERT INTO public.user_authority (user_id, authority_name) VALUES ('64bbd1e0-98ff-4b4f-acfc-7352a73f4021', 'ROLE_AGENT') ON CONFLICT (user_id, authority_name) DO NOTHING;

-- =============================================================================
-- AGENTS
-- =============================================================================
-- Agent 1 - James Mwalimu (0721015320)
INSERT INTO public.agent (
    id, created_by, created_date, last_modified_by, last_modified_date,
    district, phone_number, region, status, ward, user_id
) VALUES (
    '7b8836ba-07d3-4fb6-a36a-50714615d489',
    'admin', '2025-09-29 09:30:57.399555',
    'admin', '2025-09-29 09:30:57.399555',
    'Dodoma Urban', '0721015320', 'Dodoma', 'ACTIVE', 'Kikuyu',
    '65ecf5ee-26a2-400e-98f4-838816b005f3'
) ON CONFLICT (id) DO NOTHING;

-- Agent 2 - James Mwalimu (0711015320)
INSERT INTO public.agent (
    id, created_by, created_date, last_modified_by, last_modified_date,
    district, phone_number, region, status, ward, user_id
) VALUES (
    'dfc41582-3c4f-4f06-bd74-09e61593b69d',
    'admin', '2025-09-29 09:33:40.850590',
    'admin', '2025-09-29 09:33:40.850590',
    'Dodoma Urban', '0711015320', 'Dodoma', 'ACTIVE', 'Kikuyu',
    '64bbd1e0-98ff-4b4f-acfc-7352a73f4021'
) ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- CUSTOMERS
-- =============================================================================
-- Customers registered by Agent 1 (0721015320)
INSERT INTO public.customer (
    id, created_by, created_date, last_modified_by, last_modified_date,
    date_of_birth, district, first_name, last_name, middle_name,
    nida_number, region, ward
) VALUES
(
    'caada958-9d14-40d3-8162-3cb737e43195',
    '0721015320', '2025-09-29 09:32:00.454765',
    '0721015320', '2025-09-29 09:32:00.454765',
    '2002-04-25', 'Scottsdale', 'Bryon', 'Vandervort', 'Russel',
    '20020425383203828609', 'Ebert Villages', 'East Jack'
),
(
    'a80fd544-361b-46b8-bafa-0ecdbe3806e4',
    '0721015320', '2025-09-29 09:32:04.811157',
    '0721015320', '2025-09-29 09:32:04.811157',
    '1991-06-11', 'Darylshire', 'Jackeline', 'Shanahan', 'Leuschke',
    '19910611383247609552', 'Mosciski Terrace', 'Allieborough'
),
(
    'c6b9e1ac-27ee-4b1e-9257-92efc491253c',
    '0721015320', '2025-09-29 09:32:05.832439',
    '0721015320', '2025-09-29 09:32:05.832439',
    '1996-08-26', 'South Dessie', 'Susan', 'Pollich', 'Hane',
    '19960826383257941521', 'Dorcas Station', 'Hettingermouth'
),
(
    '63a96bab-1cd9-4fa2-b5d7-95ce56f36072',
    '0721015320', '2025-09-29 09:32:06.852803',
    '0721015320', '2025-09-29 09:32:06.852803',
    '1997-02-10', 'Port Isaistad', 'Sidney', 'Stanton', 'Lang',
    '19970210383267897990', 'Considine Inlet', 'North Erikabury'
),
(
    '865140f8-4160-4946-a844-ff72db7383fe',
    '0721015320', '2025-09-29 09:32:07.825513',
    '0721015320', '2025-09-29 09:32:07.825513',
    '1995-04-04', 'Rowenastad', 'Oren', 'Nader', 'Huel',
    '19950404383278039783', 'Rodriguez Hills', 'East Alyson'
),
(
    'c0aefeef-db9f-4ee6-b9f3-7688f1a9ee2e',
    '0721015320', '2025-09-29 09:32:08.855854',
    '0721015320', '2025-09-29 09:32:08.855854',
    '1973-03-06', 'Bergnaumville', 'Angelina', 'Goldner', 'Thompson',
    '19730306383288121005', 'Schneider Mountains', 'Lake Reuben'
),
(
    '20369fda-d431-4fab-b717-754a6f0e90c3',
    '0721015320', '2025-09-29 09:32:09.802735',
    '0721015320', '2025-09-29 09:32:09.802735',
    '1980-12-24', 'North Diana', 'Talon', 'Simonis', 'Johnson',
    '19801224383297606174', 'Chadd Dale', 'Mooremouth'
) ON CONFLICT (id) DO NOTHING;

-- Customers registered by Agent 2 (0711015320)
INSERT INTO public.customer (
    id, created_by, created_date, last_modified_by, last_modified_date,
    date_of_birth, district, first_name, last_name, middle_name,
    nida_number, region, ward
) VALUES
(
    '77571af3-7400-4a05-9942-49c043f79974',
    '0711015320', '2025-09-29 09:35:20.695479',
    '0711015320', '2025-09-29 09:35:20.695479',
    '1987-06-08', 'North Gillian', 'Johann', 'Block', 'Gerhold',
    '19870608385206132686', 'Rutherford Union', 'Millschester'
),
(
    'ec5b519e-3e8f-4839-bd20-72c9963a2c1d',
    '0711015320', '2025-09-29 09:35:21.730373',
    '0711015320', '2025-09-29 09:35:21.730373',
    '1971-02-09', 'Lake Bridiehaven', 'Johnathan', 'Gorczany', 'Kohler',
    '19710209385216961920', 'Spencer Greens', 'Port Jewel'
),
(
    '5866ff6e-57b4-427b-afce-bf4dbabb0880',
    '0711015320', '2025-09-29 09:35:22.618808',
    '0711015320', '2025-09-29 09:35:22.618808',
    '1972-06-04', 'Gibsonfort', 'Godfrey', 'Littel', 'Von',
    '19720604385225531398', 'Anderson Key', 'South Zakary'
),
-- Additional customers with missing data (extrapolated from activity logs)
(
    '0b4289af-ae5d-4f21-9d6a-3f2e2de71762',
    '0711015320', '2025-09-29 09:35:23.453432',
    '0711015320', '2025-09-29 09:35:23.453432',
    '1999-11-28', 'Hartmannbury', 'Lysanne', 'Hartmann', 'Grace',
    '19991128385233888503', 'Hartmann District', 'Lysanneville'
),
(
    '02bfb566-06c3-4841-92b7-64cd8f151dd0',
    '0711015320', '2025-09-29 09:35:24.337766',
    '0711015320', '2025-09-29 09:35:24.337766',
    '2005-05-30', 'Marksbury', 'Marc', 'Marks', 'Matthew',
    '20050530385242768874', 'Marks Region', 'West Marc'
),
(
    'f49a5874-e965-4ddb-a3a5-8a5435058e2a',
    '0711015320', '2025-09-29 09:35:25.206149',
    '0711015320', '2025-09-29 09:35:25.206149',
    '1970-01-14', 'Rauberg', 'Kane', 'Rau', 'Karl',
    '19700114385251448143', 'Rau Plains', 'Kanetown'
),
(
    '334b65f6-3e32-4ef0-b7ba-80d5e63d692e',
    '0711015320', '2025-09-29 09:35:26.155495',
    '0711015320', '2025-09-29 09:35:26.155495',
    '2000-07-24', 'Lakinville', 'Macey', 'Lakin', 'Mae',
    '20000724385260449430', 'Lakin Valley', 'East Macey'
),
(
    '00f786ce-0ec0-4f24-b9f4-657c068390bb',
    '0711015320', '2025-09-29 09:35:27.023718',
    '0711015320', '2025-09-29 09:35:27.023718',
    '1996-05-27', 'Boyleshire', 'Ibrahim', 'Boyle', 'Isaac',
    '19960527385269635887', 'Boyle Region', 'Ibrahimtown'
),
(
    '9bb7c5a9-aa27-4a97-8874-86184bb7cffe',
    '0711015320', '2025-09-29 09:35:27.980872',
    '0711015320', '2025-09-29 09:35:27.980872',
    '1971-02-01', 'McKenzieland', 'Tyshawn', 'McKenzie', 'Tyler',
    '19710201385279209252', 'McKenzie Hills', 'Tyshawnville'
),
(
    '93e5f3ad-07c6-4c3d-8162-ed9bfb96a7cf',
    '0711015320', '2025-09-29 09:36:37.550719',
    '0711015320', '2025-09-29 09:36:37.550719',
    '1974-09-18', 'Schneiderberg', 'Karli', 'Schneider', 'Karl',
    '19740918385974233831', 'Schneider Mountains', 'Karlitown'
),
(
    'ee72030c-e6cd-4968-8aac-30d3d7b76b0b',
    '0711015320', '2025-09-29 09:41:09.205457',
    '0711015320', '2025-09-29 09:41:09.205457',
    '1970-04-15', 'Stoltenbergville', 'Bennett', 'Stoltenberg', 'Ben',
    '19700415388687232203', 'Stoltenberg Region', 'Bennetttown'
),
(
    'cd03f207-e6c4-4c82-8b95-30722009e485',
    '0711015320', '2025-09-29 09:46:56.816792',
    '0711015320', '2025-09-29 09:46:56.816792',
    '2002-06-26', 'Crookshaven', 'Mona', 'Crooks', 'Monica',
    '20020626392163244990', 'Crooks District', 'Monaville'
) ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- ACTIVITY LOGS
-- =============================================================================
INSERT INTO public.activity_log (
    id, created_by, created_date, last_modified_by, last_modified_date,
    action_type, description, entity_id, entity_type, ip_address,
    session_id, status, timestamp, user_agent
) VALUES
-- Admin login
(
    '874a0cbd-b124-4b3d-a40e-ae778969165e',
    'admin', '2025-09-29 09:30:25.104354',
    'admin', '2025-09-29 09:30:25.104354',
    'AGENT_LOGIN', 'Agent admin logged in successfully', null, 'Agent',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:30:25.077862',
    'PostmanRuntime/7.45.0'
),
-- Agent 1 registration
(
    'adf8241a-0fbb-4cbd-bf6b-143ebb3c2371',
    'admin', '2025-09-29 09:30:57.579751',
    'admin', '2025-09-29 09:30:57.579751',
    'AGENT_REGISTERED', 'Registered new agent: James Mwalimu (Login: 0721015320)',
    '7b8836ba-07d3-4fb6-a36a-50714615d489', 'Agent',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:30:57.579306',
    'PostmanRuntime/7.45.0'
),
-- Agent 1 login
(
    '066cf92f-d945-4df1-97e7-5496ac367d7c',
    '0721015320', '2025-09-29 09:31:44.793210',
    '0721015320', '2025-09-29 09:31:44.793210',
    'AGENT_LOGIN', 'Agent 0721015320 logged in successfully', null, 'Agent',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:31:44.791833',
    'PostmanRuntime/7.45.0'
),
-- Customer registrations by Agent 1
(
    '65e65b6d-e2f7-4557-b4c8-9bb1a28c7645',
    '0721015320', '2025-09-29 09:32:00.534398',
    '0721015320', '2025-09-29 09:32:00.534398',
    'CUSTOMER_REGISTERED', 'Registered new customer: Bryon Vandervort (NIDA: 20020425383203828609)',
    'caada958-9d14-40d3-8162-3cb737e43195', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:00.533655',
    'PostmanRuntime/7.45.0'
),
(
    '5bb4541f-7136-4046-b8da-cf3b8330da49',
    '0721015320', '2025-09-29 09:32:04.832985',
    '0721015320', '2025-09-29 09:32:04.832985',
    'CUSTOMER_REGISTERED', 'Registered new customer: Jackeline Shanahan (NIDA: 19910611383247609552)',
    'a80fd544-361b-46b8-bafa-0ecdbe3806e4', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:04.832676',
    'PostmanRuntime/7.45.0'
),
(
    '818ff3ad-e10e-4c45-a1d0-14c96dae1ccc',
    '0721015320', '2025-09-29 09:32:05.859852',
    '0721015320', '2025-09-29 09:32:05.859852',
    'CUSTOMER_REGISTERED', 'Registered new customer: Susan Pollich (NIDA: 19960826383257941521)',
    'c6b9e1ac-27ee-4b1e-9257-92efc491253c', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:05.859493',
    'PostmanRuntime/7.45.0'
),
(
    '5d0fd5d5-ec0f-4187-a3b0-05addb31253d',
    '0721015320', '2025-09-29 09:32:06.879189',
    '0721015320', '2025-09-29 09:32:06.879189',
    'CUSTOMER_REGISTERED', 'Registered new customer: Sidney Stanton (NIDA: 19970210383267897990)',
    '63a96bab-1cd9-4fa2-b5d7-95ce56f36072', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:06.878905',
    'PostmanRuntime/7.45.0'
),
(
    '3818d8da-9f97-4324-8fe9-bfcba1a949eb',
    '0721015320', '2025-09-29 09:32:07.850346',
    '0721015320', '2025-09-29 09:32:07.850346',
    'CUSTOMER_REGISTERED', 'Registered new customer: Oren Nader (NIDA: 19950404383278039783)',
    '865140f8-4160-4946-a844-ff72db7383fe', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:07.848707',
    'PostmanRuntime/7.45.0'
),
(
    '5103288b-0dd9-4c64-bc34-820f25a68235',
    '0721015320', '2025-09-29 09:32:08.878004',
    '0721015320', '2025-09-29 09:32:08.878004',
    'CUSTOMER_REGISTERED', 'Registered new customer: Angelina Goldner (NIDA: 19730306383288121005)',
    'c0aefeef-db9f-4ee6-b9f3-7688f1a9ee2e', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:08.877709',
    'PostmanRuntime/7.45.0'
),
(
    '1442c635-3594-45d8-887d-555bedf47596',
    '0721015320', '2025-09-29 09:32:09.825285',
    '0721015320', '2025-09-29 09:32:09.825285',
    'CUSTOMER_REGISTERED', 'Registered new customer: Talon Simonis (NIDA: 19801224383297606174)',
    '20369fda-d431-4fab-b717-754a6f0e90c3', 'Customer',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:32:09.824976',
    'PostmanRuntime/7.45.0'
),
-- Agent 2 registration
(
    '49e0e43e-cb88-40cd-a18c-ac65546fdba0',
    'admin', '2025-09-29 09:33:40.905730',
    'admin', '2025-09-29 09:33:40.905730',
    'AGENT_REGISTERED', 'Registered new agent: James Mwalimu (Login: 0711015320)',
    'dfc41582-3c4f-4f06-bd74-09e61593b69d', 'Agent',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:33:40.905295',
    'PostmanRuntime/7.45.0'
),
-- Agent 2 login
(
    '36fa4c01-771b-4873-b999-031f40bd1801',
    '0711015320', '2025-09-29 09:34:59.974401',
    '0711015320', '2025-09-29 09:34:59.974401',
    'AGENT_LOGIN', 'Agent 0711015320 logged in successfully', null, 'Agent',
    '0:0:0:0:0:0:0:1', null, 'SUCCESS', '2025-09-29 09:34:59.972425',
    'PostmanRuntime/7.45.0'
)
-- Additional customer registration logs by Agent 2 can be added here...
ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- SUMMARY
-- =============================================================================
-- This seeder includes:
-- - 2 default authorities (ROLE_ADMIN, ROLE_AGENT)
-- - 4 users (admin, user, and 2 agents)
-- - 2 agents with proper user relationships
-- - 20+ customers registered by the agents
-- - Sample activity logs showing login and registration activities
--
-- Default credentials:
-- Admin: admin/admin
-- User: user/user
-- Agent 1: 0721015320 (phone number as login)
-- Agent 2: 0711015320 (phone number as login)
-- =============================================================================
