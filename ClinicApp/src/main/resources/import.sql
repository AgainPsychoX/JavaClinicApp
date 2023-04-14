
--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.0

-- Started on 2023-04-14 15:42:37

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3395 (class 0 OID 17205)
-- Dependencies: 217
-- Data for Name: doctor_specialities; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (1, 15, 'lekarz ogólny');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (2, 15, 'pediatra');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (3, 15, 'dermatolog');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (4, 45, 'kardiolog');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (5, 30, 'okulista');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (6, 15, 'laryngolog');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (7, 20, 'neurolog');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (8, 60, 'psycholog');
INSERT INTO public.doctor_specialities (id, default_visit_time, name) VALUES (9, 60, 'stomatolog');


--
-- TOC entry 3407 (class 0 OID 17252)
-- Dependencies: 229
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (1, 'PP8259', 'pprzybylski@gmail.com', 'Paweł', '798892455', 'Lekarz', 'Przybylski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (2, 'LW2972', 'lwojcik@gmail.com', 'Lucyna', '782426037', 'Lekarz', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (3, 'OM3316', 'omichalak@gmail.com', 'Oktawian', '725295963', 'Lekarz', 'Michalak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (4, 'AB3920', 'aborkowski@gmail.com', 'Anastazy', '246204561', 'Lekarz', 'Borkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (5, 'OK7685', 'okaźmierczak@gmail.com', 'Ola', '524736909', 'Lekarz', 'Kaźmierczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (6, 'PL7829', 'plewandowska@gmail.com', 'Paula', '671421234', 'Lekarz', 'Lewandowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (7, 'KW5503', 'kwozniak@gmail.com', 'Kazimierz', '934639929', 'Lekarz', 'Woźniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (8, 'WU6348', 'wurbanska@gmail.com', 'Wioletta', '934623929', 'Lekarz', 'Urbańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (9, 'FM3054', 'fmichalak@gmail.com', 'Florian', '624837074', 'Lekarz', 'Michalak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (10, 'AM1326', 'amaciejewska@gmail.com', 'Amalia', '457747376', 'Lekarz', 'Maciejewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (11, 'BS6736', 'bsadowski@gmail.com', 'Bartłomiej', '673305244', 'Lekarz', 'Sadowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (12, 'AD8711', 'aduda@gmail.com', 'Artur', '281004069', 'Lekarz', 'Duda');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (13, 'AW1269', 'awisniewska@gmail.com', 'Alana', '272904199', 'Lekarz', 'Wiśniewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (14, 'OM4855', 'omroz@gmail.com', 'Oskar', '543415035', 'Lekarz', 'Mróz');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (15, 'AB1977', 'ablaszczyk@gmail.com', 'Arkadiusz', '385952279', 'Lekarz', 'Błaszczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (16, 'LB7340', 'lbaran@gmail.com', 'Luiza', '304945763', 'Lekarz', 'Baran');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (17, 'EB3850', 'ebaranowska@gmail.com', 'Eliza', '472801213', 'Lekarz', 'Baranowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (18, 'ES8714', 'eszymanski@gmail.com', 'Eryk', '499187792', 'Lekarz', 'Szymański');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (19, 'DB1842', 'dblaszczyk@gmail.com', 'Dobromił', '224937011', 'Administrator', 'Błaszczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (20, 'KT2931', 'ktomaszewska@gmail.com', 'Karolina', '706614047', 'Administrator', 'Tomaszewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (21, 'MB8806', 'mborkowski@gmail.com', 'Miłosz', '273162175', 'Pacjent', 'Borkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (22, 'JC8219', 'jcieslak@gmail.com', 'Justyna', '295657686', 'Pacjent', 'Cieślak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (23, 'KM5491', 'kmichalak@gmail.com', 'Kinga', '775609934', 'Pacjent', 'Michalak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (24, 'JJ2254', 'jjankowska@gmail.com', 'Józefa', '305252645', 'Pacjent', 'Jankowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (25, 'EB6282', 'ebak@gmail.com', 'Emil', '443735081', 'Pacjent', 'Bąk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (26, 'DS8736', 'dszymanski@gmail.com', 'Daniel', '686056843', 'Pacjent', 'Szymański');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (27, 'LJ6459', 'ljankowska@gmail.com', 'Lidia', '216534326', 'Pacjent', 'Jankowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (28, 'AW1866', 'awojcik@gmail.com', 'Alek', '396861810', 'Pacjent', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (29, 'OM4307', 'omalinowska@gmail.com', 'Ola', '606963615', 'Pacjent', 'Malinowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (30, 'BC6266', 'bczerwinski@gmail.com', 'Bogumił', '480889655', 'Pacjent', 'Czerwiński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (31, 'AO6499', 'aostrowska@gmail.com', 'Aneta', '539675398', 'Pacjent', 'Ostrowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (32, 'MP7612', 'mprzybylska@gmail.com', 'Marysia', '843260876', 'Pacjent', 'Przybylska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (33, 'AS6402', 'asadowska@gmail.com', 'Aneta', '755070098', 'Pacjent', 'Sadowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (34, 'JM8492', 'jmaciejewski@gmail.com', 'Janusz', '506253670', 'Pacjent', 'Maciejewski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (35, 'MB2360', 'mbaranowski@gmail.com', 'Miron', '885543289', 'Pacjent', 'Baranowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (36, 'JS1728', 'jsikorska@gmail.com', 'Jędrzej', '361864376', 'Pacjent', 'Sikorska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (37, 'AA8918', 'aadamska@gmail.com', 'Amelia', '540952564', 'Pacjent', 'Adamska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (38, 'KL7411', 'klewandowska@gmail.com', 'Klementyna', '422960930', 'Pacjent', 'Lewandowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (39, 'IZ6077', 'izawadzki@gmail.com', 'Ireneusz', '318586755', 'Pacjent', 'Zawadzki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (40, 'AZ4864', 'azawadzka@gmail.com', 'Amanda', '295980034', 'Pacjent', 'Zawadzka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (41, 'MK6034', 'mkucharska@gmail.com', 'Maria', '459587613', 'Pacjent', 'Kucharska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (42, 'BZ8480', 'bzakrzewska@gmail.com', 'Bogusława', '526764462', 'Pacjent', 'Zakrzewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (43, 'EU6169', 'eurbanska@gmail.com', 'Eliza', '867125333', 'Pacjent', 'Urbańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (44, 'AS8888', 'aszymczak@gmail.com', 'Ariel', '838322441', 'Pacjent', 'Szymczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (45, 'KS8798', NULL, 'Kamil', NULL, 'Pacjent', 'Sadowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (46, 'HS4039', 'hsobczak@gmail.com', 'Henryk', '639873028', 'Pacjent', 'Sobczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (47, 'DA4482', 'dandrzejewski@gmail.com', 'Dominik', '268584857', 'Pacjent', 'Andrzejewski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (48, 'OK4459', 'okrupa@gmail.com', 'Otylia', '709231889', 'Pacjent', 'Krupa');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (49, 'KM8184', 'kmalinowski@gmail.com', 'Kewin', '603299508', 'Pacjent', 'Malinowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (50, 'BS4737', 'besadowska@gmail.com', 'Bernadetta', '740892972', 'Pacjent', 'Sadowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (51, 'AD6868', 'amduda@gmail.com', 'Amanda', '560822317', 'Pacjent', 'Duda');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (52, 'EK7255', 'ekrupa@gmail.com', 'Eustachy', '497821542', 'Pacjent', 'Krupa');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (53, 'AP7575', 'apiotrowska@gmail.com', 'Andrea', '421283716', 'Pacjent', 'Piotrowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (54, 'IW2684', 'iwalczak@gmail.com', 'Izyda', '523304942', 'Pacjent', 'Walczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (55, 'RW7987', 'rwojcik@gmail.com', 'Róża', '743269885', 'Pacjent', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (56, 'JC6114', 'jchmielewska@gmail.com', 'Józefa', '366537202', 'Pacjent', 'Chmielewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (57, 'KR3609', 'krutkowska@gmail.com', 'Kaja', '745782425', 'Pacjent', 'Rutkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (58, 'IS8067', 'istepien@gmail.com', 'Ignacy', '692543078', 'Pacjent', 'Stępień');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (59, 'BS6381', 'basadowska@gmail.com', 'Balbina', '370451221', 'Pacjent', 'Sadowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (60, 'MK8333', 'mkrupa@gmail.com', 'Milan', '361292454', 'Pacjent', 'Krupa');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (61, 'JZ1627', 'jziolkowska@gmail.com', 'Jan', '485949478', 'Pacjent', 'Ziółkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (62, 'RZ6585', 'rziolkowska@gmail.com', 'Regina', '564912489', 'Pacjent', 'Ziółkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (63, 'IP2034', 'ipiotrowska@gmail.com', 'Irena', '488232844', 'Pacjent', 'Piotrowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (64, 'AK6944', 'akozlowski@gmail.com', 'Anastazy', '422754531', 'Pacjent', 'Kozłowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (65, 'AT3296', 'atomaszewska@gmail.com', 'Alana', '861171485', 'Pacjent', 'Tomaszewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (66, 'PW8033', 'pwojcik@gmail.com', 'Paula', '482029819', 'Pacjent', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (67, 'RP8127', 'rpawlak@gmail.com', 'Roman', '295514192', 'Pacjent', 'Pawlak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (68, 'CK6813', 'ckaczmarczyk@gmail.com', 'Cezary', '594701831', 'Pacjent', 'Kaczmarczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (69, 'AK8449', 'adkozlowski@gmail.com', 'Adam', '326449678', 'Pacjent', 'Kozłowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (70, 'JR3401', 'jrutkowski@gmail.com', 'Janusz', '882169763', 'Pacjent', 'Rutkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (71, 'MM7740', 'mmarciniak@gmail.com', 'Marcel', '200890363', 'Pacjent', 'Marciniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (72, 'EK8278', 'ekwiatkowski@gmail.com', 'Edward', '477532863', 'Pacjent', 'Kwiatkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (73, 'FW7179', 'fwojcik@gmail.com', 'Felicja', '658003880', 'Pacjent', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (74, 'PL2613', 'palewandowska@gmail.com', 'Patrycja', '234055965', 'Pacjent', 'Lewandowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (75, 'WU1604', 'weurbanska@gmail.com', 'Weronika', '746569304', 'Pacjent', 'Urbańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (76, 'CW1130', 'cwisniewska@gmail.com', 'Cecylia', '448097519', 'Pacjent', 'Wiśniewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (77, 'MS3613', 'msikorska@gmail.com', 'Mikołaj', '766343813', 'Pacjent', 'Sikorska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (78, 'AL2519', 'alis@gmail.com', 'Amanda', '467173565', 'Pacjent', 'Lis');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (79, 'JK4868', 'jkrupa@gmail.com', 'Jacek', '667862235', 'Pacjent', 'Krupa');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (80, 'GU6869', NULL, 'Grzegorz', '401869877', 'Pacjent', 'Urbański');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (81, 'AS5410', 'asawicki@gmail.com', 'Andrzej', '387052065', 'Pacjent', 'Sawicki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (82, 'OR7486', 'orutkowska@gmail.com', 'Oksana', '562181728', 'Pacjent', 'Rutkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (83, 'FS4551', 'fszulc@gmail.com', 'Florentyna', '352669982', 'Pacjent', 'Szulc');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (84, 'FA7803', NULL, 'Florian', '885390866', 'Pacjent', 'Adamska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (85, 'OM4660', 'omazur@gmail.com', 'Oktawian', '969332135', 'Pacjent', 'Mazur');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (86, 'BK7925', 'bkubiak@gmail.com', 'Bogumił', '759456493', 'Pacjent', 'Kubiak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (87, 'DM3279', 'dmroz@gmail.com', 'Dobromił', '938606190', 'Pacjent', 'Mróz');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (88, 'AA5691', 'aadamski@gmail.com', 'Ariel', '309897152', 'Pacjent', 'Adamski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (89, 'AK2960', 'akubiak@gmail.com', 'Alina', '486364266', 'Pacjent', 'Kubiak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (90, 'DR2075', 'drutkowska@gmail.com', 'Dagmara', '742518848', 'Pacjent', 'Rutkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (91, 'MW6154', 'mwroblewski@gmail.com', 'Milan', '860744568', 'Pacjent', 'Wróblewski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (92, 'MW4458', 'mwalczak@gmail.com', 'Monika', '745168640', 'Pacjent', 'Walczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (93, 'CB4630', 'cbak@gmail.com', 'Celina', '674621046', 'Pacjent', 'Bąk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (94, 'AM7974', 'amalinowski@gmail.com', 'Aleks', '273562223', 'Pacjent', 'Malinowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (95, 'AK8169', 'akozlowska@gmail.com', 'Adela', '227688477', 'Pacjent', 'Kozłowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (96, 'KG4079', 'kgajewski@gmail.com', 'Krzysztof', '343324224', 'Pacjent', 'Gajewski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (97, 'BR3869', 'brutkowska@gmail.com', 'Bogumiła', '249494990', 'Pacjent', 'Rutkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (98, 'ES8253', 'esobczak@gmail.com', 'Eryk', '610275127', 'Pacjent', 'Sobczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (99, 'NK4981', 'nkaczmarczyk@gmail.com', 'Norbert', '709810747', 'Pacjent', 'Kaczmarczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (100, 'ZW2425', 'zwojciechowska@gmail.com', 'Zofia', '262858209', 'Pacjent', 'Wojciechowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (101, 'BS2833', 'bszulc@gmail.com', 'Bolesław', '271453216', 'Pacjent', 'Szulc');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (102, 'JW6907', 'jwitkowski@gmail.com', 'Julian', '757055465', 'Pacjent', 'Witkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (103, 'HW8596', 'hwysocka@gmail.com', 'Helena', '405267178', 'Pacjent', 'Wysocka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (104, 'EK5591', 'ekozlowska@gmail.com', 'Elena', '466709880', 'Pacjent', 'Kozłowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (105, 'AG6434', 'agajewska@gmail.com', 'Andrea', '260079530', 'Pacjent', 'Gajewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (106, 'AZ3820', 'azielinska@gmail.com', 'Andrea', '682367621', 'Pacjent', 'Zielińska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (107, 'LS2000', 'lsikora@gmail.com', 'Lila', '815158309', 'Pacjent', 'Sikora');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (108, 'MW3083', 'mwroblewska@gmail.com', 'Mirosława', '420838495', 'Pacjent', 'Wróblewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (109, 'IR2545', 'irutkowska@gmail.com', 'Irena', '203997847', 'Pacjent', 'Rutkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (110, 'AS3995', 'agsadowska@gmail.com', 'Agnieszka', '462311827', 'Pacjent', 'Sadowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (111, 'KM3758', 'kmazur@gmail.com', 'Karolina', '329409412', 'Pacjent', 'Mazur');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (112, 'FK1825', 'fkucharski@gmail.com', 'Filip', '604295627', 'Pacjent', 'Kucharski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (113, 'MK7697', 'mkowalski@gmail.com', 'Mikołaj', '441163076', 'Pacjent', 'Kowalski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (114, 'KC6162', 'kczarnecki@gmail.com', 'Kacper', '287715820', 'Pacjent', 'Czarnecki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (115, 'AU3799', 'aurbanska@gmail.com', 'Anatol', '885217929', 'Pacjent', 'Urbańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (116, 'HJ3156', 'hjasinski@gmail.com', 'Henryk', '501146985', 'Pacjent', 'Jasiński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (117, 'MJ3134', 'mjaworska@gmail.com', 'Magdalena', '634831241', 'Pacjent', 'Jaworska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (118, 'AZ1361', 'aizawadzka@gmail.com', 'Aisha', '276664496', 'Pacjent', 'Zawadzka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (119, 'PM2105', 'pmarciniak@gmail.com', 'Pamela', '514349151', 'Pacjent', 'Marciniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (120, 'LS2995', 'lszymczak@gmail.com', 'Leszek', '264655160', 'Pacjent', 'Szymczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (121, 'HS5958', 'hsokolowska@gmail.com', 'Hortensja', '309049760', 'Pacjent', 'Sokołowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (122, 'PL8483', 'plaskowski@gmail.com', 'Przemysław', '823552998', 'Pacjent', 'Laskowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (123, 'WM4211', 'wmroz@gmail.com', 'Wanda', '196827517', 'Pacjent', 'Mróz');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (124, 'DM2313', 'dmalinowska@gmail.com', 'Dominika', '822021690', 'Pacjent', 'Malinowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (125, 'KC8172', 'kczerwinski@gmail.com', 'Kacper', '679494153', 'Pacjent', 'Czerwiński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (126, 'AA8358', 'aandrzejewska@gmail.com', 'Alisa', '704942098', 'Pacjent', 'Andrzejewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (127, 'EM6331', 'emarciniak@gmail.com', 'Emilia', '282566727', 'Pacjent', 'Marciniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (128, 'RL8469', 'rlis@gmail.com', 'Radosław', '268592561', 'Pacjent', 'Lis');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (129, 'BM1030', 'bmazurek@gmail.com', 'Beata', '674549903', 'Pacjent', 'Mazurek');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (130, 'KP1037', 'kprzybylski@gmail.com', 'Krystian', '697196209', 'Pacjent', 'Przybylski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (131, 'OK8030', 'okucharski@gmail.com', 'Oskar', '557962350', 'Pacjent', 'Kucharski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (132, 'KS3363', 'kszewczyk@gmail.com', 'Kewin', '300359069', 'Pacjent', 'Szewczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (133, 'AS7405', 'astepien@gmail.com', 'Alexander', '558361135', 'Pacjent', 'Stępień');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (134, 'RU7282', 'rurbanski@gmail.com', 'Rafał', '760608765', 'Pacjent', 'Urbański');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (135, 'KC3922', 'kczerwinska@gmail.com', 'Kaja', '677120440', 'Pacjent', 'Czerwińska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (136, 'JK2066', NULL, 'Jakub', '248163616', 'Pacjent', 'Kalinowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (137, 'DC7390', 'dczerwinski@gmail.com', 'Damian', '354961607', 'Pacjent', 'Czerwiński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (138, 'EB5367', NULL, 'Emanuel', '596533134', 'Pacjent', 'Brzeziński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (139, 'RP6851', 'rprzybylski@gmail.com', 'Roman', '421904431', 'Pacjent', 'Przybylski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (140, 'SM7695', 'smroz@gmail.com', 'Sylwia', '547711288', 'Pacjent', 'Mróz');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (141, 'ER5396', 'erutkowski@gmail.com', 'Emil', '421095900', 'Pacjent', 'Rutkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (142, 'IS3618', 'isobczak@gmail.com', 'Iga', '679720091', 'Pacjent', 'Sobczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (143, 'AB2473', 'abaranowska@gmail.com', 'Aniela', '758585941', 'Pacjent', 'Baranowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (144, 'LS6179', 'lsikorska@gmail.com', 'Ludwik', '890063762', 'Pacjent', 'Sikorska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (145, 'KP3180', 'kpietrzak@gmail.com', 'Konrad', '814373796', 'Pacjent', 'Pietrzak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (146, 'RW5817', 'rwysocki@gmail.com', 'Robert', '289027390', 'Pacjent', 'Wysocki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (147, 'ET3182', 'etomaszewska@gmail.com', 'Edyta', '242494531', 'Pacjent', 'Tomaszewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (148, 'PK8570', 'pkaczmarczyk@gmail.com', 'Paweł', '711244939', 'Pacjent', 'Kaczmarczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (149, 'JW5588', 'jwoźniak@gmail.com', 'Jakub', '692289945', 'Pacjent', 'Woźniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (150, 'DS6282', 'doszymanski@gmail.com', 'Dominik', '693191162', 'Pacjent', 'Szymański');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (151, 'OS8638', 'ostepien@gmail.com', 'Ola', '749101127', 'Pacjent', 'Stępień');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (152, 'AS6215', 'asobczak@gmail.com', 'Alojzy', '642815808', 'Pacjent', 'Sobczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (153, 'AB7154', 'agblaszczyk@gmail.com', 'Agnieszka', '339206413', 'Pacjent', 'Błaszczyk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (154, 'AS8932', 'aszulc@gmail.com', 'Andrea', '222186738', 'Pacjent', 'Szulc');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (155, 'DL1033', 'dlis@gmail.com', 'Damian', '200827314', 'Pacjent', 'Lis');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (156, 'NG6121', 'nglowacka@gmail.com', 'Natalia', '814023702', 'Pacjent', 'Głowacka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (157, 'FK2738', 'fkaminska@gmail.com', 'Florentyna', '350991109', 'Pacjent', 'Kamińska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (158, 'AS8201', 'aszymanska@gmail.com', 'Anna', '462414199', 'Pacjent', 'Szymańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (159, 'JC3749', 'jczerwinski@gmail.com', 'Jędrzej', '382399449', 'Pacjent', 'Czerwiński');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (160, 'EK8847', 'ekolodziej@gmail.com', 'Elżbieta', '260078392', 'Pacjent', 'Kołodziej');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (161, 'JM1968', 'jmaciejewska@gmail.com', 'Jagoda', '655775356', 'Pacjent', 'Maciejewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (162, 'KA8749', 'kadamski@gmail.com', 'Kewin', '505369786', 'Pacjent', 'Adamski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (163, 'FK6380', 'fkalinowska@gmail.com', 'Faustyna', '432410296', 'Pacjent', 'Kalinowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (164, 'HN4351', 'hnowak@gmail.com', 'Heronim', '869581653', 'Pacjent', 'Nowak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (165, 'RM7816', 'rmazur@gmail.com', 'Róża', '868736928', 'Pacjent', 'Mazur');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (166, 'FC4423', NULL, 'Florencja', NULL, 'Pacjent', 'Czarnecka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (167, 'AS4436', 'aszczepanska@gmail.com', 'Anastazja', '835115898', 'Pacjent', 'Szczepańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (168, 'JG2434', 'jgorski@gmail.com', 'Jacek', '767698850', 'Pacjent', 'Górski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (169, 'BP6577', 'bprzybylska@gmail.com', 'Barbara', '215900633', 'Pacjent', 'Przybylska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (170, 'JG5344', 'jglowacka@gmail.com', 'Joachim', '307495902', 'Pacjent', 'Głowacka');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (171, 'PM1401', 'pmakowska@gmail.com', 'Pamela', '868413631', 'Pacjent', 'Makowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (172, 'KJ3236', 'kjakubowski@gmail.com', 'Krzysztof', '663521156', 'Pacjent', 'Jakubowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (173, 'OW3456', 'owitkowski@gmail.com', 'Oktawian', '471320192', 'Pacjent', 'Witkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (174, 'DJ5741', 'djakubowski@gmail.com', 'Daniel', '236359967', 'Pacjent', 'Jakubowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (175, 'KC8514', 'koczarnecki@gmail.com', 'Konrad', '696510073', 'Pacjent', 'Czarnecki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (176, 'MT6435', 'mtomaszewski@gmail.com', 'Mirosław', '683638492', 'Pacjent', 'Tomaszewski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (177, 'EB1974', 'eborkowski@gmail.com', 'Emil', '397758736', 'Pacjent', 'Borkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (178, 'PS6982', 'pszczepanska@gmail.com', 'Pamela', '278768857', 'Pacjent', 'Szczepańska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (179, 'KM6019', 'kmarciniak@gmail.com', 'Konrad', '575140630', 'Pacjent', 'Marciniak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (180, 'EJ4920', 'ejasinska@gmail.com', 'Emilia', '762070778', 'Pacjent', 'Jasińska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (181, 'FP8217', 'fpawlak@gmail.com', 'Fabian', '605554205', 'Pacjent', 'Pawlak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (182, 'BM3417', 'bmakowski@gmail.com', 'Borys', '441579317', 'Pacjent', 'Makowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (183, 'AK4840', 'akalinowska@gmail.com', 'Alana', '437197088', 'Pacjent', 'Kalinowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (184, 'JS8075', 'jszymczak@gmail.com', 'Jarosław', '188709687', 'Pacjent', 'Szymczak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (185, 'WL3956', 'wlis@gmail.com', 'Wanda', '582555401', 'Pacjent', 'Lis');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (186, 'KS2091', 'ksawicki@gmail.com', 'Korneliusz', '792085191', 'Pacjent', 'Sawicki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (187, 'MC3013', 'mczarnecki@gmail.com', 'Martin', '490827078', 'Pacjent', 'Czarnecki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (188, 'JP7307', 'jpiotrowski@gmail.com', 'Janusz', '599831654', 'Pacjent', 'Piotrowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (189, 'EZ5154', 'eziolkowska@gmail.com', 'Eliza', '338257038', 'Pacjent', 'Ziółkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (190, 'TB1544', 'tbak@gmail.com', 'Teresa', '584475948', 'Pacjent', 'Bąk');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (191, 'NK4968', 'nkrupa@gmail.com', 'Natasza', '588503857', 'Pacjent', 'Krupa');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (192, 'LK7696', 'lkowalska@gmail.com', 'Lara', '228147213', 'Pacjent', 'Kowalska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (193, 'WK1974', 'wkwiatkowska@gmail.com', 'Wioletta', '780460423', 'Pacjent', 'Kwiatkowska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (194, 'GP6329', 'gpietrzak@gmail.com', 'Gustaw', '555476175', 'Pacjent', 'Pietrzak');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (195, 'NW1654', 'nwasilewska@gmail.com', 'Norbert', '755997987', 'Pacjent', 'Wasilewska');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (196, 'BW7822', 'bwojcik@gmail.com', 'Bogna', '430213357', 'Pacjent', 'Wójcik');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (197, 'DZ6012', NULL, 'Dawid', '255173539', 'Pacjent', 'Zawadzki');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (198, 'FP8646', 'fprzybylski@gmail.com', 'Fryderyk', '196824924', 'Pacjent', 'Przybylski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (199, 'BM5526', 'bmalinowski@gmail.com', 'Bolesław', '500919561', 'Pacjent', 'Malinowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (200, 'KK2500', NULL, 'Kacper', NULL, 'Pacjent', 'Kwiatkowski');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (201, 'RE2716', 'klinika.recepcja@gmail.com', '-', '262529387', 'Recepcja', '-');
INSERT INTO public.users (id, internal_name, email, name, phone, role, surname) VALUES (202, 'PI6872', 'klinika.pielegniarki@gmail.com', '-', '989452761', 'Pielęgniarka', '-');


--
-- TOC entry 3396 (class 0 OID 17211)
-- Dependencies: 218
-- Data for Name: doctors; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (780, 480, 960, 480, NULL, NULL, NULL, NULL, 960, 480, 960, 480, 960, 480, 1, 1);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (780, 480, 1080, 480, NULL, NULL, NULL, NULL, 1080, 480, 720, 540, 1080, 480, 2, 1);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1140, 900, 3, 2);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1080, 900, NULL, NULL, 4, 2);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (1170, 780, 1140, 1020, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1170, 870, 5, 3);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (960, 810, NULL, NULL, NULL, NULL, NULL, NULL, 750, 540, 780, 540, NULL, NULL, 6, 3);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, 960, 480, NULL, NULL, NULL, NULL, 960, 480, NULL, NULL, 960, 480, 7, 4);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (960, 480, NULL, NULL, NULL, NULL, NULL, NULL, 960, 480, 960, 480, NULL, NULL, 8, 4);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (750, 480, 960, 810, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 750, 480, 9, 5);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 750, 480, 960, 810, 750, 480, 10, 5);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, 960, 810, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 960, 810, 11, 6);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 960, 810, 750, 480, 12, 6);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, 1020, 840, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 13, 7);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1020, 780, NULL, NULL, 1020, 840, 14, 7);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (NULL, NULL, 840, 480, NULL, NULL, NULL, NULL, 1080, 780, 1080, 780, 840, 480, 15, 8);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (1080, 780, NULL, NULL, NULL, NULL, NULL, NULL, 840, 480, 840, 480, 1080, 780, 16, 8);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (960, 480, 960, 480, NULL, NULL, NULL, NULL, 960, 480, 960, 480, 960, 480, 17, 9);
INSERT INTO public.doctors (friday_end, friday_start, monday_end, monday_start, saturday_end, saturday_start, sunday_end, sunday_start, thursday_end, thursday_start, tuesday_end, tuesday_start, wednesday_end, wednesday_start, id, speciality_id) VALUES (1080, 600, 1080, 600, NULL, NULL, NULL, NULL, 1080, 600, 1080, 600, 1080, 600, 18, 9);


--
-- TOC entry 3399 (class 0 OID 17224)
-- Dependencies: 221
-- Data for Name: patients; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Stara Wieś', '49041814746', 'Stara Wieś', '23-090', 'Góra Przemysła', 1);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('39', 'Podlesie', '79103148416', 'Stara Wieś', '23-090', 'Zamkowa', 2);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Piaski', '99092166552', 'Stara Wieś', '23-090', 'Kozia', 3);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('14', 'Nowa Wieś', '96103135462', 'Stara Wieś', '23-090', 'Ślusarska', 4);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('59', 'Kolonia', '53011676746', 'Stara Wieś', '23-090', 'Stary Rynek', 5);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Góra', '89022787722', 'Stara Wieś', '23-090', 'Szkolna', 6);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Dół', '94011654956', 'Stara Wieś', '23-090', 'Woźna', 7);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('29', 'Dąbrowa', '93040883597', 'Stara Wieś', '23-090', 'Rynkowa', 8);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('52', 'Górki', '96120367455', 'Stara Wieś', '23-090', 'Ostrówek', 9);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('49', 'Folwark', '74032794163', 'Stara Wieś', '23-090', 'Śródecki', 10);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('68', 'Zalesie', '2291822217', 'Stara Wieś', '23-090', 'Lubrańskiego Jana', 11);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Kąty', '80060197757', 'Stara Wieś', '23-090', 'Świętojańska', 12);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('65', 'Góry', '63042912845', 'Stara Wieś', '23-090', 'Wieżowa', 13);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('34', 'Górka', '89111348771', 'Stara Wieś', '23-090', 'Różany Targ', 14);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('68', 'Zagórze', '86081599361', 'Stara Wieś', '23-090', 'Kurzanoga', 15);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('14', 'Zagrody', '1271293843', 'Stara Wieś', '23-090', 'Jana Baptysty Quadro', 16);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('67', 'Pod Lasem', '82020518974', 'Stara Wieś', '23-090', 'Garbary', 17);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('50', 'Budy', '83031358955', 'Stara Wieś', '23-090', 'Grobla', 18);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Błonie', '82082552974', 'Stara Wieś', '23-090', 'Wielka', 19);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('66', 'Dwór', '97051242989', 'Stara Wieś', '23-090', 'Wodna', 20);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('70', 'Dąbrówka', '59012651579', 'Stara Wieś', '23-090', 'Wrocławska', 21);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('47', 'Podlas', '4290956229', 'Stara Wieś', '23-090', 'Zagórze', 22);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('29', 'Borek', '79031411833', 'Stara Wieś', '23-090', 'Polna ', 23);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('50', 'Nowiny', '68031519276', 'Stara Wieś', '23-090', 'Leśna', 24);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('16', 'Wygoda', '4260777571', 'Stara Wieś', '23-090', 'Słoneczna', 25);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('69', 'Łazy', '94111875363', 'Stara Wieś', '23-090', 'Krótka', 26);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('61', 'Podgórze', '92010787556', 'Stara Wieś', '23-090', 'Szkolna', 27);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('40', 'Dział', '77033011419', 'Stara Wieś', '23-090', 'Ogrodowa', 28);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Borki', '93020545471', 'Stara Wieś', '23-090', 'Lipowa', 29);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('37', 'Cegielnia', '59121729297', 'Stara Wieś', '23-090', 'Łąkowa', 30);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('59', 'Zarzecze', '72042323627', 'Stara Wieś', '23-090', 'Brzozowa', 31);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('39', 'Granice', '65120693976', 'Stara Wieś', '23-090', 'Kwiatowa ', 32);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('61', 'Kresy', '48042755645', 'Stara Wieś', '23-090', 'Kościelna', 33);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('61', 'Kamionka', '81112133635', 'Stara Wieś', '23-090', 'Sosnowa', 34);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('14', 'Bugaj', '69110256631', 'Stara Wieś', '23-090', 'Zielona', 35);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Józefów', '92062068517', 'Stara Wieś', '23-090', 'Parkowa', 36);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('42', 'Przymiarki', '59081798733', 'Stara Wieś', '23-090', 'Akacjowa', 37);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('20', 'Brzeziny', '63031368929', 'Stara Wieś', '23-090', 'Kolejowa', 38);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('56', 'Doły', '70090717179', 'Stara Wieś', '23-090', 'Śródecki', 39);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Gaj', '94092367231', 'Stara Wieś', '23-090', 'Lubrańskiego Jana', 40);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('54', 'Zawodzie', '68020378323', 'Stara Wieś', '23-090', 'Świętojańska', 41);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('70', 'Poręby', '83042034671', 'Stara Wieś', '23-090', 'Wieżowa', 42);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('16', 'Ruda', '54110236563', 'Stara Wieś', '23-090', 'Różany Targ', 43);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Piekło', '71061973217', 'Stara Wieś', '23-090', 'Kurzanoga', 44);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Majdan', '53063031498', 'Stara Wieś', '23-090', 'Jana Baptysty Quadro', 45);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('68', 'Morgi', '89021916572', 'Stara Wieś', '23-090', 'Garbary', 46);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('62', 'Gościniec', '89071152579', 'Stara Wieś', '23-090', 'Grobla', 47);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('69', 'Działy', '52021033194', 'Stara Wieś', '23-090', 'Wielka', 48);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Jeziorki', '50030811992', 'Stara Wieś', '23-090', 'Wodna', 49);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Kawęczyn', '79071444617', 'Stara Wieś', '23-090', 'Wrocławska', 50);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('54', 'Krzaki', '95060863931', 'Stara Wieś', '23-090', 'Zagórze', 51);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('40', 'Krzywda', '76112243587', 'Stara Wieś', '23-090', 'Polna ', 52);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Łany', '79011094311', 'Stara Wieś', '23-090', 'Łąkowa', 53);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Marianka', '87070975823', 'Stara Wieś', '23-090', 'Brzozowa', 54);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Mościska', '55060184476', 'Stara Wieś', '23-090', 'Kwiatowa ', 55);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('60', 'Nowe Osiedle', '80021278693', 'Stara Wieś', '23-090', 'Kościelna', 56);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Skała', '69111351168', 'Stara Wieś', '23-090', 'Sosnowa', 57);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('22', 'Smolarnia', '1273055328', 'Stary Dwór', '45-954', 'Zielona', 58);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Stary Dwór', '96011564451', 'Stary Dwór', '45-954', 'Parkowa', 59);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('56', 'Stawy', '86112298739', 'Stary Dwór', '45-954', 'Akacjowa', 60);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('37', 'Łazy', '90031015186', 'Stary Dwór', '45-954', 'Ślusarska', 61);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('27', 'Podgórze', '92080781519', 'Stary Dwór', '45-954', 'Stary Rynek', 62);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Dział', '85012684624', 'Stary Dwór', '45-954', 'Szkolna', 63);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Borki', '5243061964', 'Stary Dwór', '45-954', 'Woźna', 64);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Cegielnia', '97120829945', 'Stary Dwór', '45-954', 'Rynkowa', 65);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('24', 'Zarzecze', '242486297', 'Stary Dwór', '45-954', 'Ostrówek', 66);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Granice', '72081468374', 'Stary Dwór', '45-954', 'Śródecki', 67);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('30', 'Kresy', '5320364399', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 68);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('66', 'Kamionka', '54110116232', 'Stary Dwór', '45-954', 'Świętojańska', 69);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('16', 'Bugaj', '51082047472', 'Stary Dwór', '45-954', 'Wieżowa', 70);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('70', 'Józefów', '92080245714', 'Stary Dwór', '45-954', 'Różany Targ', 71);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('20', 'Przymiarki', '48022793933', 'Stary Dwór', '45-954', 'Ulica', 72);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Brzeziny', '64082351661', 'Stary Dwór', '45-954', 'Góra Przemysła', 73);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Doły', '91090587591', 'Stary Dwór', '45-954', 'Zamkowa', 74);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Gaj', '83090224921', 'Stary Dwór', '45-954', 'Kozia', 75);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('17', 'Zawodzie', '69121778177', 'Stary Dwór', '45-954', 'Ślusarska', 76);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Poręby', '2321428938', 'Stary Dwór', '45-954', 'Stary Rynek', 77);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('69', 'Ruda', '99062858564', 'Stary Dwór', '45-954', 'Szkolna', 78);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('49', 'Piekło', '52051295894', 'Stary Dwór', '45-954', 'Woźna', 79);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Majdan', '48101852939', 'Stary Dwór', '45-954', 'Rynkowa', 80);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('55', 'Morgi', '55121982812', 'Stary Dwór', '45-954', 'Śródecki', 81);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Gościniec', '98020244795', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 82);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('65', 'Budzyń', '48082587585', 'Stary Dwór', '45-954', 'Świętojańska', 83);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('24', 'Czarny Las', '67041249636', 'Stary Dwór', '45-954', 'Wieżowa', 84);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('34', 'Kuźnica', '94062082856', 'Stary Dwór', '45-954', 'Różany Targ', 85);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('47', 'Miasteczko', '48050294651', 'Stary Dwór', '45-954', 'Kurzanoga', 86);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('42', 'Niwki', '5260485813', 'Stary Dwór', '45-954', 'Jana Baptysty Quadro', 87);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('36', 'Piasek', '98042974694', 'Stary Dwór', '45-954', 'Garbary', 88);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Rybaki', '55010172438', 'Stary Dwór', '45-954', 'Grobla', 89);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Środek', '4281454374', 'Stary Dwór', '45-954', 'Wielka', 90);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Wrzosy', '73021985113', 'Stary Dwór', '45-954', 'Wodna', 91);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('35', 'Długie', '60080854922', 'Stary Dwór', '45-954', 'Wrocławska', 92);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Łaziska', '92061299369', 'Stary Dwór', '45-954', 'Zagórze', 93);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Łęgi', '79122462687', 'Stary Dwór', '45-954', 'Polna ', 94);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('56', 'Mała Wieś', '79120523555', 'Stary Dwór', '45-954', 'Leśna', 95);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('53', 'Marianowo', '92121732337', 'Stary Dwór', '45-954', 'Słoneczna', 96);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('60', 'Miasto', '67032641948', 'Stary Dwór', '45-954', 'Krótka', 97);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('35', 'Nowe Miasto', '72051014459', 'Stary Dwór', '45-954', 'Kolejowa', 98);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Nowinki', '70123159198', 'Stary Dwór', '45-954', 'Śródecki', 99);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Osiedle Młodych', '55122293281', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 100);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('12', 'Piotrowice', '65052553753', 'Stary Dwór', '45-954', 'Świętojańska', 101);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('62', 'Plebanka', '68121311894', 'Stary Dwór', '45-954', 'Wieżowa', 102);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Podgaj', '3261574194', 'Stary Dwór', '45-954', 'Różany Targ', 103);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('53', 'Poduchowne', '60122211593', 'Stary Dwór', '45-954', 'Kurzanoga', 104);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('39', 'Sosnówka', '62020716343', 'Stary Dwór', '45-954', 'Jana Baptysty Quadro', 105);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Wysoka', '91012316397', 'Stary Dwór', '45-954', 'Garbary', 106);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('50', 'Za Lasem', '62032484746', 'Stary Dwór', '45-954', 'Grobla', 107);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Zakrzewo', '92010755555', 'Stary Dwór', '45-954', 'Wielka', 108);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('42', 'Doły', '66050463758', 'Stary Dwór', '45-954', 'Wodna', 109);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('47', 'Gaj', '61090734637', 'Stary Dwór', '45-954', 'Wrocławska', 110);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('63', 'Zawodzie', '2281122842', 'Stary Dwór', '45-954', 'Zagórze', 111);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('68', 'Poręby', '96051585131', 'Stary Dwór', '45-954', 'Polna ', 112);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Ruda', '52070296317', 'Stary Dwór', '45-954', 'Łąkowa', 113);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('55', 'Piekło', '52022497919', 'Stary Dwór', '45-954', 'Brzozowa', 114);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('27', 'Majdan', '77073172297', 'Stary Dwór', '45-954', 'Kwiatowa ', 115);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('51', 'Morgi', '57120971394', 'Stary Dwór', '45-954', 'Kościelna', 116);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Gościniec', '78042275575', 'Stary Dwór', '45-954', 'Sosnowa', 117);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Działy', '99061872824', 'Stary Dwór', '45-954', 'Zielona', 118);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Jeziorki', '95061398939', 'Stary Dwór', '45-954', 'Parkowa', 119);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Kawęczyn', '75083119743', 'Stary Dwór', '45-954', 'Akacjowa', 120);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('51', 'Krzaki', '92030192714', 'Stary Dwór', '45-954', 'Ślusarska', 121);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('13', 'Krzywda', '66102329638', 'Stary Dwór', '45-954', 'Stary Rynek', 122);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('62', 'Łany', '55032418789', 'Stary Dwór', '45-954', 'Szkolna', 123);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('34', 'Marianka', '58032816939', 'Stary Dwór', '45-954', 'Woźna', 124);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Mościska', '93041996766', 'Stary Dwór', '45-954', 'Rynkowa', 125);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Nowe Osiedle', '79041922789', 'Stary Dwór', '45-954', 'Ostrówek', 126);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('36', 'Skała', '92022588552', 'Stary Dwór', '45-954', 'Śródecki', 127);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('62', 'Smolarnia', '57102066436', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 128);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('52', 'Stary Dwór', '69073038541', 'Stary Dwór', '45-954', 'Świętojańska', 129);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Stawy', '281831511', 'Stary Dwór', '45-954', 'Wieżowa', 130);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('57', 'Łazy', '84060121772', 'Stary Dwór', '45-954', 'Różany Targ', 131);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Podgórze', '61070177379', 'Stary Dwór', '45-954', 'Ulica', 132);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('30', 'Dział', '55092026672', 'Stary Dwór', '45-954', 'Góra Przemysła', 133);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('50', 'Borki', '51080521835', 'Stary Dwór', '45-954', 'Zamkowa', 134);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('57', 'Cegielnia', '74070832739', 'Stary Dwór', '45-954', 'Kozia', 135);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('49', 'Baranówka', '3302176927', 'Stary Dwór', '45-954', 'Ślusarska', 136);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('59', 'Bielany', '89011566615', 'Stary Dwór', '45-954', 'Stary Rynek', 137);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('15', 'Dworskie', '93060825777', 'Stary Dwór', '45-954', 'Szkolna', 138);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('22', 'Dziadówki', '87010674672', 'Stary Dwór', '45-954', 'Woźna', 139);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('70', 'Henryków', '83070973915', 'Stary Dwór', '45-954', 'Kolejowa', 140);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('66', 'Jeziorko', '81093051797', 'Stary Dwór', '45-954', 'Śródecki', 141);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('25', 'Józefin', '81062055935', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 142);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Majdany', '71052276295', 'Stary Dwór', '45-954', 'Świętojańska', 143);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Międzylesie', '87041855185', 'Stary Dwór', '45-954', 'Wieżowa', 144);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('59', 'Osiedle Słoneczne', '56122544551', 'Stary Dwór', '45-954', 'Różany Targ', 145);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Podedwór', '5213191936', 'Stary Dwór', '45-954', 'Kurzanoga', 146);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('63', 'Równie', '72071022575', 'Stary Dwór', '45-954', 'Jana Baptysty Quadro', 147);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('52', 'Wybudowanie', '98051591936', 'Stary Dwór', '45-954', 'Garbary', 148);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('60', 'Żabieniec', '321995494', 'Stary Dwór', '45-954', 'Grobla', 149);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('44', 'Brody', '71090319453', 'Stary Dwór', '45-954', 'Wielka', 150);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('65', 'Czekaj', '76032527516', 'Stary Dwór', '45-954', 'Wodna', 151);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('60', 'Dębniak', '3302438553', 'Stary Dwór', '45-954', 'Wrocławska', 152);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('15', 'Górna Wieś', '52030512941', 'Stary Dwór', '45-954', 'Zagórze', 153);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Krzyżówka', '94051445237', 'Stary Dwór', '45-954', 'Polna ', 154);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Michałówka', '77051123262', 'Stary Dwór', '45-954', 'Łąkowa', 155);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('67', 'Młynki', '6262538189', 'Stary Dwór', '45-954', 'Brzozowa', 156);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('28', 'Mokre', '50112549645', 'Stary Dwór', '45-954', 'Kwiatowa ', 157);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Polanka', '65061828417', 'Stary Dwór', '45-954', 'Kościelna', 158);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('29', 'Romanów', '85112773712', 'Stary Dwór', '45-954', 'Sosnowa', 159);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Trzcianka', '66101437941', 'Stary Dwór', '45-954', 'Zielona', 160);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('52', 'Ustronie', '59030545454', 'Stary Dwór', '45-954', 'Parkowa', 161);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Wieś', '79060886336', 'Stary Dwór', '45-954', 'Akacjowa', 162);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('61', 'Wysokie', '83080254813', 'Stary Dwór', '45-954', 'Ślusarska', 163);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('68', 'Zadziele', '95092644339', 'Stary Dwór', '45-954', 'Stary Rynek', 164);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('50', 'Antoniów', '91062893596', 'Stary Dwór', '45-954', 'Szkolna', 165);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('55', 'Błota', '7220177712', 'Stary Dwór', '45-954', 'Woźna', 166);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('67', 'Glinianki', '91062034562', 'Stary Dwór', '45-954', 'Rynkowa', 167);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('22', 'Jankowice', '73091289696', 'Stary Dwór', '45-954', 'Ostrówek', 168);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Kamionki', '74071767438', 'Stary Dwór', '45-954', 'Śródecki', 169);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('25', 'Konary', '85013051216', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 170);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('24', 'Kopalina', '98032715876', 'Stary Dwór', '45-954', 'Świętojańska', 171);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('46', 'Krzyżówki', '51102455964', 'Stary Dwór', '45-954', 'Wieżowa', 172);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('70', 'Mazury', '72042143346', 'Stary Dwór', '45-954', 'Różany Targ', 173);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Nowy Młyn', '80010528774', 'Stary Dwór', '45-954', 'Ulica', 174);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('57', 'Olendry', '59111597345', 'Stary Dwór', '45-954', 'Góra Przemysła', 175);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Raj', '59112377238', 'Stary Dwór', '45-954', 'Zamkowa', 176);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Rogatka', '71100942543', 'Stary Dwór', '45-954', 'Kozia', 177);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('14', 'Rudnik', '83082816868', 'Stary Dwór', '45-954', 'Ślusarska', 178);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('13', 'Rudniki', '77100754133', 'Stary Dwór', '45-954', 'Stary Rynek', 179);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('59', 'Stoki', '92120964487', 'Stary Dwór', '45-954', 'Szkolna', 180);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('66', 'Struga', '73121019628', 'Stary Dwór', '45-954', 'Woźna', 181);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('23', 'Brzezinka', '6262368689', 'Stary Dwór', '45-954', 'Kolejowa', 182);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('43', 'Chrusty', '87121245242', 'Stary Dwór', '45-954', 'Śródecki', 183);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('28', 'Czerwonka', '91050542392', 'Stary Dwór', '45-954', 'Lubrańskiego Jana', 184);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Janowice', '55112169716', 'Janowice', '12-346', 'Świętojańska', 185);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Jaźwiny', '98061973399', 'Janowice', '12-346', 'Wieżowa', 186);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('40', 'Kamienna Góra', '78092929677', 'Janowice', '12-346', 'Różany Targ', 187);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('69', 'Karczunek', '52090638359', 'Janowice', '12-346', 'Kurzanoga', 188);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('46', 'Kolonia Pierwsza', '57092714465', 'Janowice', '12-346', 'Jana Baptysty Quadro', 189);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('14', 'Kopaliny', '70042953112', 'Janowice', '12-346', 'Garbary', 190);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('28', 'Kopiec', '1301179323', 'Janowice', '12-346', 'Grobla', 191);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Lipnik', '5292887159', 'Janowice', '12-346', 'Wielka', 192);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('17', 'Olszanka', '86072746457', 'Janowice', '12-346', 'Wodna', 193);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('61', 'Osiedle Leśne', '52092269434', 'Janowice', '12-346', 'Wrocławska', 194);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('47', 'Piachy', '89022186194', 'Janowice', '12-346', 'Zagórze', 195);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('53', 'Piekiełko', '77101113151', 'Janowice', '12-346', 'Polna ', 196);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Polana', '270721788', 'Janowice', '12-346', 'Łąkowa', 197);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Stawek', '60072289778', 'Janowice', '12-346', 'Brzozowa', 198);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('38', 'Wincentów', '53022837312', 'Janowice', '12-346', 'Kwiatowa ', 199);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Piachy', '91021372447', 'Janowice', '12-346', 'Kościelna', 200);


--
-- TOC entry 3393 (class 0 OID 17197)
-- Dependencies: 215
-- Data for Name: appointments; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (1, '2023-04-12 15:02:25', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 15, 100, 100, 1);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (2, '2023-04-12 15:02:26', 'Wizyta kontrolna.', '', '2023-06-26 08:15:00', 15, 19, 19, 1);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (3, '2023-04-12 15:02:27', 'Omówienie wyników badań krwi.', '', '2023-06-26 08:30:00', 15, 102, 102, 1);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (4, '2023-04-12 15:02:27', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 15, 103, 103, 2);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (5, '2023-04-12 15:02:29', 'Wizyta kontrolna.', '', '2023-06-26 08:15:00', 15, 104, 104, 2);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (6, '2023-04-12 15:02:30', 'Przeziębienie.', '', '2023-06-26 17:30:00', 15, 201, 105, 2);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (7, '2023-04-12 15:02:31', 'Wizyta kontrolna.', '', '2023-06-28 15:00:00', 15, 106, 106, 3);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (8, '2023-04-12 15:02:32', 'Wizyta kontrolna.', '', '2023-06-28 15:15:00', 15, 107, 107, 3);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (9, '2023-04-12 15:02:33', 'Przeziębienie.', '', '2023-06-28 15:30:00', 15, 108, 108, 3);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (10, '2023-04-12 15:02:34', 'Wizyta kontrolna.', '', '2023-06-27 15:00:00', 15, 201, 109, 4);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (11, '2023-04-12 15:02:35', 'Wizyta kontrolna.', '', '2023-06-27 15:15:00', 15, 60, 60, 4);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (12, '2023-04-12 15:02:36', 'Wizyta kontrolna.', '', '2023-06-27 15:30:00', 15, 201, 111, 4);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (13, '2023-04-12 15:02:37', 'Zmiany skórne.', '', '2023-06-26 17:00:00', 15, 112, 112, 5);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (14, '2023-04-12 15:02:38', 'Wysypka.', '', '2023-06-26 17:15:00', 15, 113, 113, 5);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (15, '2023-04-12 15:02:39', 'Trądzik.', '', '2023-06-26 17:30:00', 15, 56, 56, 5);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (16, '2023-04-12 15:02:40', 'Zmiany skórne.', '', '2023-06-27 09:00:00', 15, 201, 78, 6);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (17, '2023-04-12 15:02:41', 'Zmiany skórne.', '', '2023-06-27 09:15:00', 15, 116, 116, 6);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (18, '2023-04-12 15:02:42', 'Zmiany skórne.', '', '2023-06-27 09:30:00', 15, 201, 117, 6);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (19, '2023-04-12 15:02:43', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 45, 118, 118, 7);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (20, '2023-04-12 15:02:44', 'Badanie EKG.', '', '2023-06-26 08:45:00', 45, 23, 23, 7);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (21, '2023-04-12 15:02:45', 'Wizyta kontrolna.', '', '2023-06-28 08:00:00', 45, 120, 120, 7);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (22, '2023-04-12 15:02:46', 'Założenie holtera.', '', '2023-06-27 08:00:00', 45, 121, 121, 8);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (23, '2023-04-12 15:02:47', 'Omówienie wyników badania holterem.', '', '2023-06-27 08:45:00', 45, 122, 122, 8);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (24, '2023-04-12 15:02:48', 'Echo serca.', '', '2023-06-29 08:00:00', 45, 201, 123, 8);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (25, '2023-04-12 15:02:49', 'Badanie wzroku.', '', '2023-06-26 13:30:00', 30, 201, 124, 9);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (26, '2023-04-12 15:02:50', 'Badanie wzroku.', '', '2023-06-26 14:00:00', 30, 201, 125, 9);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (27, '2023-04-12 15:02:51', 'Badanie wzroku.', '', '2023-06-26 14:30:00', 30, 126, 126, 9);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (28, '2023-04-12 15:02:52', 'Badanie wzroku.', '', '2023-06-27 13:30:00', 30, 127, 127, 10);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (29, '2023-04-12 15:02:53', 'Badanie wzroku.', '', '2023-06-27 14:00:00', 30, 201, 128, 10);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (30, '2023-04-12 15:02:54', 'Badanie wzroku.', '', '2023-06-27 14:30:00', 30, 129, 129, 10);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (31, '2023-04-12 15:02:55', 'Badanie słuchu.', '', '2023-06-26 13:30:00', 15, 130, 130, 11);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (32, '2023-04-12 15:02:56', 'Badanie słuchu.', '', '2023-06-26 13:45:00', 15, 131, 131, 11);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (33, '2023-04-12 15:02:57', 'Badanie słuchu.', '', '2023-06-26 14:00:00', 15, 132, 132, 11);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (34, '2023-04-12 15:02:58', 'Badanie słuchu.', '', '2023-06-27 13:30:00', 15, 201, 133, 12);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (35, '2023-04-12 15:02:59', 'Badanie słuchu.', '', '2023-06-27 13:45:00', 15, 134, 134, 12);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (36, '2023-04-12 15:03:00', 'Badanie słuchu.', '', '2023-06-27 14:00:00', 15, 135, 135, 12);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (37, '2023-04-12 15:03:01', 'Wizyta kontrolna.', '', '2023-06-26 14:00:00', 20, 136, 136, 13);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (38, '2023-04-12 15:03:02', 'Konsultacja neurologiczna.', '', '2023-06-26 14:20:00', 20, 137, 137, 13);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (39, '2023-04-12 15:03:03', 'Wizyta kontrolna.', '', '2023-06-26 14:40:00', 20, 138, 138, 13);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (40, '2023-04-12 15:03:04', 'Konsultacja neurologiczna.', '', '2023-06-28 14:00:00', 20, 139, 139, 14);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (41, '2023-04-12 15:03:05', 'Konsultacja neurologiczna.', '', '2023-06-28 14:20:00', 20, 140, 140, 14);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (42, '2023-04-12 15:03:06', 'Wizyta kontrolna.', '', '2023-06-28 14:40:00', 20, 201, 141, 14);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (43, '2023-04-12 15:03:07', 'Kontynuacja terapii.', '', '2023-06-26 08:00:00', 60, 142, 142, 15);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (44, '2023-04-12 15:03:08', 'Kontynuacja terapii.', '', '2023-06-26 09:00:00', 60, 143, 143, 15);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (45, '2023-04-12 15:03:09', 'Kontynuacja terapii.', '', '2023-06-26 10:00:00', 60, 144, 144, 15);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (46, '2023-04-12 15:03:10', 'Kontynuacja terapii.', '', '2023-06-27 08:00:00', 60, 145, 145, 16);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (47, '2023-04-12 15:03:11', 'Kontynuacja terapii.', '', '2023-06-27 09:00:00', 60, 146, 146, 16);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (48, '2023-04-12 15:03:12', 'Kontynuacja terapii.', '', '2023-06-27 10:00:00', 60, 147, 147, 16);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (49, '2023-04-12 15:03:13', 'Leczenie kanałowe.', '', '2023-06-26 08:00:00', 60, 201, 148, 17);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (50, '2023-04-12 15:03:14', 'Wybielanie.', '', '2023-06-26 09:00:00', 60, 149, 149, 17);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (51, '2023-04-12 15:03:15', 'Korona tymczasowa.', '', '2023-06-26 10:00:00', 60, 201, 150, 17);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (52, '2023-04-12 15:03:16', 'Wizyta kontrolna.', '', '2023-06-26 10:00:00', 60, 151, 151, 18);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (53, '2023-04-12 15:03:17', 'Wybielanie.', '', '2023-06-26 11:00:00', 60, 152, 152, 18);
INSERT INTO public.appointments (id, added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES (54, '2023-04-12 15:03:18', 'Wizyta kontrolna.', '', '2023-06-26 12:00:00', 60, 153, 153, 18);


--
-- TOC entry 3398 (class 0 OID 17217)
-- Dependencies: 220
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.notifications (id, content, read_date, sent_date, destination_user_id, source_user_id) VALUES (1, 'Umówiono wizytę \nPacjent: Zofia Wojciechowska\nData:  2023-06-26 08:00:00\nCel wizyty: Wizyta kontrolna.', '2023-04-12 16:02:25', '2023-04-12 15:02:25', 1, 100);
INSERT INTO public.notifications (id, content, read_date, sent_date, destination_user_id, source_user_id) VALUES (2, 'Umówiono wizytę \nPacjent: Dobromir Błaszczyk\nData:  2023-06-26 08:15:00\nCel wizyty: Wizyta kontrolna.', '2023-04-12 16:02:26', '2023-04-12 15:02:26', 1, 19);
INSERT INTO public.notifications (id, content, read_date, sent_date, destination_user_id, source_user_id) VALUES (3, 'Umówiono wizytę \nPacjent: Julian Witkowski\nData:  2023-06-26 08:30:00\nCel wizyty: Omówienie wyników badań krwi.', '2023-04-12 16:02:27', '2023-04-12 15:02:27', 1, 102);


--
-- TOC entry 3401 (class 0 OID 17230)
-- Dependencies: 223
-- Data for Name: prescriptions; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (1, '2023-04-10 13:02:25', 'Krople do oczu Kroplex; dawkowanie: codziennie rano i wieczorem ', '', '7275', 9, 96);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (2, '2023-04-10 08:02:25', 'Tabletki Cardio; dawkowanie: codziennie rano 1 tabletka', '', '4434', 7, 97);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (3, '2023-04-11 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '4943', 1, 97);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (4, '2023-04-12 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7466', 1, 97);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (5, '2023-04-13 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '3979', 2, 97);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (6, '2023-04-14 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '4163', 2, 97);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (7, '2023-04-17 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '3095', 3, 84);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (8, '2023-04-18 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7478', 3, 34);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (9, '2023-04-19 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7443', 4, 71);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (10, '2023-04-20 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '6486', 4, 50);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (11, '2023-04-21 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7487', 7, 34);
INSERT INTO public.prescriptions (id, added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES (12, '2023-04-24 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '2851', 7, 97);


--
-- TOC entry 3403 (class 0 OID 17238)
-- Dependencies: 225
-- Data for Name: referrals; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.referrals (id, added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES (1, '2023-04-12 15:02:00', 'Podejrzenie pogorszenia słuchu.', '', 'Pacjentka doznała częściowej utraty słuchu i wymaga aparatu słuchowego.', '2023-04-17 14:00:00', '4221', 'Poradnia laryngologiczna', 1, 56);
INSERT INTO public.referrals (id, added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES (2, '2023-04-13 15:02:00', 'Zmiany skórne charakterystyczne dla alergii. Wymagane badania alergologiczne.', '', 'Stwierdzono alergię na kocią sierść.', '2023-04-18 14:00:00', '6013', 'Poradnia alergologiczna', 2, 58);
INSERT INTO public.referrals (id, added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES (3, '2023-04-13 08:02:00', 'Zmiany skórne charakterystyczne dla alergii. Wymagane badania alergologiczne.', '', NULL, NULL, '4481', 'Poradnia alergologiczna', 2, 33);


--
-- TOC entry 3405 (class 0 OID 17246)
-- Dependencies: 227
-- Data for Name: schedule_entries; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.schedule_entries (id, date_begin, date_end, type, user_id) VALUES (1, '2023-08-07 00:00:00', '2023-08-21 00:00:00', 'urlop', 5);
INSERT INTO public.schedule_entries (id, date_begin, date_end, type, user_id) VALUES (2, '2023-08-21 00:00:00', '2023-09-04 00:00:00', 'urlop', 2);


--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 214
-- Name: appointments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.appointments_id_seq', 1, false);


--
-- TOC entry 3414 (class 0 OID 0)
-- Dependencies: 216
-- Name: doctor_specialities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.doctor_specialities_id_seq', 1, false);


--
-- TOC entry 3415 (class 0 OID 0)
-- Dependencies: 219
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.notifications_id_seq', 1, false);


--
-- TOC entry 3416 (class 0 OID 0)
-- Dependencies: 222
-- Name: prescriptions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.prescriptions_id_seq', 1, false);


--
-- TOC entry 3417 (class 0 OID 0)
-- Dependencies: 224
-- Name: referrals_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.referrals_id_seq', 1, false);


--
-- TOC entry 3418 (class 0 OID 0)
-- Dependencies: 226
-- Name: schedule_entries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.schedule_entries_id_seq', 1, false);


--
-- TOC entry 3419 (class 0 OID 0)
-- Dependencies: 228
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


-- Completed on 2023-04-14 15:42:38

--
-- PostgreSQL database dump complete
--

