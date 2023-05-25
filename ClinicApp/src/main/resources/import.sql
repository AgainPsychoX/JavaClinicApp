
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
SET row_security = on;

--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 214
-- Name: appointments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.appointments_id_seq', 1, false);


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
-- Name: schedule_simple_entries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.schedule_simple_entries_id_seq', 1, false);


--
-- TOC entry 3419 (class 0 OID 0)
-- Dependencies: 228
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- TOC entry 3407 (class 0 OID 17252)
-- Dependencies: 229
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upp8259', 'pprzybylski@gmail.com', 'Paweł', '798892455', 'DOCTOR', 'Przybylski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ulw2972', 'lwojcik@gmail.com', 'Lucyna', '782426037', 'DOCTOR', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uom3316', 'omichalak@gmail.com', 'Oktawian', '725295963', 'DOCTOR', 'Michalak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uab3920', 'aborkowski@gmail.com', 'Anastazy', '246204561', 'DOCTOR', 'Borkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uok7685', 'okaźmierczak@gmail.com', 'Ola', '524736909', 'DOCTOR', 'Kaźmierczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upl7829', 'plewandowska@gmail.com', 'Paula', '671421234', 'DOCTOR', 'Lewandowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukw5503', 'kwozniak@gmail.com', 'Kazimierz', '934639929', 'DOCTOR', 'Woźniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uwu6348', 'wurbanska@gmail.com', 'Wioletta', '934623929', 'DOCTOR', 'Urbańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufm3054', 'fmichalak@gmail.com', 'Florian', '624837074', 'DOCTOR', 'Michalak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uam1326', 'amaciejewska@gmail.com', 'Amalia', '457747376', 'DOCTOR', 'Maciejewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubs6736', 'bsadowski@gmail.com', 'Bartłomiej', '673305244', 'DOCTOR', 'Sadowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uad8711', 'aduda@gmail.com', 'Artur', '281004069', 'DOCTOR', 'Duda');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaw1269', 'awisniewska@gmail.com', 'Alana', '272904199', 'DOCTOR', 'Wiśniewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uom4855', 'omroz@gmail.com', 'Oskar', '543415035', 'DOCTOR', 'Mróz');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uab1977', 'ablaszczyk@gmail.com', 'Arkadiusz', '385952279', 'DOCTOR', 'Błaszczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ulb7340', 'lbaran@gmail.com', 'Luiza', '304945763', 'DOCTOR', 'Baran');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ueb3850', 'ebaranowska@gmail.com', 'Eliza', '472801213', 'DOCTOR', 'Baranowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ues8714', 'eszymanski@gmail.com', 'Eryk', '499187792', 'DOCTOR', 'Szymański');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udb1842', 'dblaszczyk@gmail.com', 'Dobromił', '224937011', 'ADMIN', 'Błaszczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukt2931', 'ktomaszewska@gmail.com', 'Karolina', '706614047', 'ADMIN', 'Tomaszewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umb8806', 'mborkowski@gmail.com', 'Miłosz', '273162175', 'PATIENT', 'Borkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujc8219', 'jcieslak@gmail.com', 'Justyna', '295657686', 'PATIENT', 'Cieślak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukm5491', 'kmichalak@gmail.com', 'Kinga', '775609934', 'PATIENT', 'Michalak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujj2254', 'jjankowska@gmail.com', 'Józefa', '305252645', 'PATIENT', 'Jankowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ueb6282', 'ebak@gmail.com', 'Emil', '443735081', 'PATIENT', 'Bąk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uds8736', 'dszymanski@gmail.com', 'Daniel', '686056843', 'PATIENT', 'Szymański');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ulj6459', 'ljankowska@gmail.com', 'Lidia', '216534326', 'PATIENT', 'Jankowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaw1866', 'awojcik@gmail.com', 'Alek', '396861810', 'PATIENT', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uom4307', 'omalinowska@gmail.com', 'Ola', '606963615', 'PATIENT', 'Malinowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubc6266', 'bczerwinski@gmail.com', 'Bogumił', '480889655', 'PATIENT', 'Czerwiński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uao6499', 'aostrowska@gmail.com', 'Aneta', '539675398', 'PATIENT', 'Ostrowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ump7612', 'mprzybylska@gmail.com', 'Marysia', '843260876', 'PATIENT', 'Przybylska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas6402', 'asadowska@gmail.com', 'Aneta', '755070098', 'PATIENT', 'Sadowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujm8492', 'jmaciejewski@gmail.com', 'Janusz', '506253670', 'PATIENT', 'Maciejewski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umb2360', 'mbaranowski@gmail.com', 'Miron', '885543289', 'PATIENT', 'Baranowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujs1728', 'jsikorska@gmail.com', 'Jędrzej', '361864376', 'PATIENT', 'Sikorska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaa8918', 'aadamska@gmail.com', 'Amelia', '540952564', 'PATIENT', 'Adamska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukl7411', 'klewandowska@gmail.com', 'Klementyna', '422960930', 'PATIENT', 'Lewandowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uiz6077', 'izawadzki@gmail.com', 'Ireneusz', '318586755', 'PATIENT', 'Zawadzki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaz4864', 'azawadzka@gmail.com', 'Amanda', '295980034', 'PATIENT', 'Zawadzka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umk6034', 'mkucharska@gmail.com', 'Maria', '459587613', 'PATIENT', 'Kucharska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubz8480', 'bzakrzewska@gmail.com', 'Bogusława', '526764462', 'PATIENT', 'Zakrzewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ueu6169', 'eurbanska@gmail.com', 'Eliza', '867125333', 'PATIENT', 'Urbańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas8888', 'aszymczak@gmail.com', 'Ariel', '838322441', 'PATIENT', 'Szymczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uks8798', null, 'Kamil', null, 'PATIENT', 'Sadowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uhs4039', 'hsobczak@gmail.com', 'Henryk', '639873028', 'PATIENT', 'Sobczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uda4482', 'dandrzejewski@gmail.com', 'Dominik', '268584857', 'PATIENT', 'Andrzejewski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uok4459', 'okrupa@gmail.com', 'Otylia', '709231889', 'PATIENT', 'Krupa');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukm8184', 'kmalinowski@gmail.com', 'Kewin', '603299508', 'PATIENT', 'Malinowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubs4737', 'besadowska@gmail.com', 'Bernadetta', '740892972', 'PATIENT', 'Sadowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uad6868', 'amduda@gmail.com', 'Amanda', '560822317', 'PATIENT', 'Duda');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uek7255', 'ekrupa@gmail.com', 'Eustachy', '497821542', 'PATIENT', 'Krupa');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uap7575', 'apiotrowska@gmail.com', 'Andrea', '421283716', 'PATIENT', 'Piotrowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uiw2684', 'iwalczak@gmail.com', 'Izyda', '523304942', 'PATIENT', 'Walczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urw7987', 'rwojcik@gmail.com', 'Róża', '743269885', 'PATIENT', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujc6114', 'jchmielewska@gmail.com', 'Józefa', '366537202', 'PATIENT', 'Chmielewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukr3609', 'krutkowska@gmail.com', 'Kaja', '745782425', 'PATIENT', 'Rutkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uis8067', 'istepien@gmail.com', 'Ignacy', '692543078', 'PATIENT', 'Stępień');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubs6381', 'basadowska@gmail.com', 'Balbina', '370451221', 'PATIENT', 'Sadowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umk8333', 'mkrupa@gmail.com', 'Milan', '361292454', 'PATIENT', 'Krupa');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujz1627', 'jziolkowska@gmail.com', 'Jan', '485949478', 'PATIENT', 'Ziółkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urz6585', 'rziolkowska@gmail.com', 'Regina', '564912489', 'PATIENT', 'Ziółkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uip2034', 'ipiotrowska@gmail.com', 'Irena', '488232844', 'PATIENT', 'Piotrowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uak6944', 'akozlowski@gmail.com', 'Anastazy', '422754531', 'PATIENT', 'Kozłowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uat3296', 'atomaszewska@gmail.com', 'Alana', '861171485', 'PATIENT', 'Tomaszewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upw8033', 'pwojcik@gmail.com', 'Paula', '482029819', 'PATIENT', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urp8127', 'rpawlak@gmail.com', 'Roman', '295514192', 'PATIENT', 'Pawlak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uck6813', 'ckaczmarczyk@gmail.com', 'Cezary', '594701831', 'PATIENT', 'Kaczmarczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uak8449', 'adkozlowski@gmail.com', 'Adam', '326449678', 'PATIENT', 'Kozłowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujr3401', 'jrutkowski@gmail.com', 'Janusz', '882169763', 'PATIENT', 'Rutkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umm7740', 'mmarciniak@gmail.com', 'Marcel', '200890363', 'PATIENT', 'Marciniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uek8278', 'ekwiatkowski@gmail.com', 'Edward', '477532863', 'PATIENT', 'Kwiatkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufw7179', 'fwojcik@gmail.com', 'Felicja', '658003880', 'PATIENT', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upl2613', 'palewandowska@gmail.com', 'Patrycja', '234055965', 'PATIENT', 'Lewandowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uwu1604', 'weurbanska@gmail.com', 'Weronika', '746569304', 'PATIENT', 'Urbańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ucw1130', 'cwisniewska@gmail.com', 'Cecylia', '448097519', 'PATIENT', 'Wiśniewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ums3613', 'msikorska@gmail.com', 'Mikołaj', '766343813', 'PATIENT', 'Sikorska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ual2519', 'alis@gmail.com', 'Amanda', '467173565', 'PATIENT', 'Lis');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujk4868', 'jkrupa@gmail.com', 'Jacek', '667862235', 'PATIENT', 'Krupa');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ugu6869', null, 'Grzegorz', '401869877', 'PATIENT', 'Urbański');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas5410', 'asawicki@gmail.com', 'Andrzej', '387052065', 'PATIENT', 'Sawicki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uor7486', 'orutkowska@gmail.com', 'Oksana', '562181728', 'PATIENT', 'Rutkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufs4551', 'fszulc@gmail.com', 'Florentyna', '352669982', 'PATIENT', 'Szulc');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufa7803', null, 'Florian', '885390866', 'PATIENT', 'Adamska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uom4660', 'omazur@gmail.com', 'Oktawian', '969332135', 'PATIENT', 'Mazur');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubk7925', 'bkubiak@gmail.com', 'Bogumił', '759456493', 'PATIENT', 'Kubiak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udm3279', 'dmroz@gmail.com', 'Dobromił', '938606190', 'PATIENT', 'Mróz');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaa5691', 'aadamski@gmail.com', 'Ariel', '309897152', 'PATIENT', 'Adamski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uak2960', 'akubiak@gmail.com', 'Alina', '486364266', 'PATIENT', 'Kubiak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udr2075', 'drutkowska@gmail.com', 'Dagmara', '742518848', 'PATIENT', 'Rutkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umw6154', 'mwroblewski@gmail.com', 'Milan', '860744568', 'PATIENT', 'Wróblewski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umw4458', 'mwalczak@gmail.com', 'Monika', '745168640', 'PATIENT', 'Walczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ucb4630', 'cbak@gmail.com', 'Celina', '674621046', 'PATIENT', 'Bąk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uam7974', 'amalinowski@gmail.com', 'Aleks', '273562223', 'PATIENT', 'Malinowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uak8169', 'akozlowska@gmail.com', 'Adela', '227688477', 'PATIENT', 'Kozłowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukg4079', 'kgajewski@gmail.com', 'Krzysztof', '343324224', 'PATIENT', 'Gajewski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubr3869', 'brutkowska@gmail.com', 'Bogumiła', '249494990', 'PATIENT', 'Rutkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ues8253', 'esobczak@gmail.com', 'Eryk', '610275127', 'PATIENT', 'Sobczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('unk4981', 'nkaczmarczyk@gmail.com', 'Norbert', '709810747', 'PATIENT', 'Kaczmarczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uzw2425', 'zwojciechowska@gmail.com', 'Zofia', '262858209', 'PATIENT', 'Wojciechowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubs2833', 'bszulc@gmail.com', 'Bolesław', '271453216', 'PATIENT', 'Szulc');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujw6907', 'jwitkowski@gmail.com', 'Julian', '757055465', 'PATIENT', 'Witkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uhw8596', 'hwysocka@gmail.com', 'Helena', '405267178', 'PATIENT', 'Wysocka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uek5591', 'ekozlowska@gmail.com', 'Elena', '466709880', 'PATIENT', 'Kozłowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uag6434', 'agajewska@gmail.com', 'Andrea', '260079530', 'PATIENT', 'Gajewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaz3820', 'azielinska@gmail.com', 'Andrea', '682367621', 'PATIENT', 'Zielińska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uls2000', 'lsikora@gmail.com', 'Lila', '815158309', 'PATIENT', 'Sikora');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umw3083', 'mwroblewska@gmail.com', 'Mirosława', '420838495', 'PATIENT', 'Wróblewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uir2545', 'irutkowska@gmail.com', 'Irena', '203997847', 'PATIENT', 'Rutkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas3995', 'agsadowska@gmail.com', 'Agnieszka', '462311827', 'PATIENT', 'Sadowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukm3758', 'kmazur@gmail.com', 'Karolina', '329409412', 'PATIENT', 'Mazur');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufk1825', 'fkucharski@gmail.com', 'Filip', '604295627', 'PATIENT', 'Kucharski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umk7697', 'mkowalski@gmail.com', 'Mikołaj', '441163076', 'PATIENT', 'Kowalski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukc6162', 'kczarnecki@gmail.com', 'Kacper', '287715820', 'PATIENT', 'Czarnecki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uau3799', 'aurbanska@gmail.com', 'Anatol', '885217929', 'PATIENT', 'Urbańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uhj3156', 'hjasinski@gmail.com', 'Henryk', '501146985', 'PATIENT', 'Jasiński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umj3134', 'mjaworska@gmail.com', 'Magdalena', '634831241', 'PATIENT', 'Jaworska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaz1361', 'aizawadzka@gmail.com', 'Aisha', '276664496', 'PATIENT', 'Zawadzka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upm2105', 'pmarciniak@gmail.com', 'Pamela', '514349151', 'PATIENT', 'Marciniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uls2995', 'lszymczak@gmail.com', 'Leszek', '264655160', 'PATIENT', 'Szymczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uhs5958', 'hsokolowska@gmail.com', 'Hortensja', '309049760', 'PATIENT', 'Sokołowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upl8483', 'plaskowski@gmail.com', 'Przemysław', '823552998', 'PATIENT', 'Laskowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uwm4211', 'wmroz@gmail.com', 'Wanda', '196827517', 'PATIENT', 'Mróz');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udm2313', 'dmalinowska@gmail.com', 'Dominika', '822021690', 'PATIENT', 'Malinowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukc8172', 'kczerwinski@gmail.com', 'Kacper', '679494153', 'PATIENT', 'Czerwiński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uaa8358', 'aandrzejewska@gmail.com', 'Alisa', '704942098', 'PATIENT', 'Andrzejewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uem6331', 'emarciniak@gmail.com', 'Emilia', '282566727', 'PATIENT', 'Marciniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('url8469', 'rlis@gmail.com', 'Radosław', '268592561', 'PATIENT', 'Lis');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubm1030', 'bmazurek@gmail.com', 'Beata', '674549903', 'PATIENT', 'Mazurek');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukp1037', 'kprzybylski@gmail.com', 'Krystian', '697196209', 'PATIENT', 'Przybylski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uok8030', 'okucharski@gmail.com', 'Oskar', '557962350', 'PATIENT', 'Kucharski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uks3363', 'kszewczyk@gmail.com', 'Kewin', '300359069', 'PATIENT', 'Szewczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas7405', 'astepien@gmail.com', 'Alexander', '558361135', 'PATIENT', 'Stępień');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uru7282', 'rurbanski@gmail.com', 'Rafał', '760608765', 'PATIENT', 'Urbański');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukc3922', 'kczerwinska@gmail.com', 'Kaja', '677120440', 'PATIENT', 'Czerwińska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujk2066', null, 'Jakub', '248163616', 'PATIENT', 'Kalinowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udc7390', 'dczerwinski@gmail.com', 'Damian', '354961607', 'PATIENT', 'Czerwiński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ueb5367', null, 'Emanuel', '596533134', 'PATIENT', 'Brzeziński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urp6851', 'rprzybylski@gmail.com', 'Roman', '421904431', 'PATIENT', 'Przybylski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('usm7695', 'smroz@gmail.com', 'Sylwia', '547711288', 'PATIENT', 'Mróz');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uer5396', 'erutkowski@gmail.com', 'Emil', '421095900', 'PATIENT', 'Rutkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uis3618', 'isobczak@gmail.com', 'Iga', '679720091', 'PATIENT', 'Sobczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uab2473', 'abaranowska@gmail.com', 'Aniela', '758585941', 'PATIENT', 'Baranowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uls6179', 'lsikorska@gmail.com', 'Ludwik', '890063762', 'PATIENT', 'Sikorska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukp3180', 'kpietrzak@gmail.com', 'Konrad', '814373796', 'PATIENT', 'Pietrzak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urw5817', 'rwysocki@gmail.com', 'Robert', '289027390', 'PATIENT', 'Wysocki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uet3182', 'etomaszewska@gmail.com', 'Edyta', '242494531', 'PATIENT', 'Tomaszewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upk8570', 'pkaczmarczyk@gmail.com', 'Paweł', '711244939', 'PATIENT', 'Kaczmarczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujw5588', 'jwoźniak@gmail.com', 'Jakub', '692289945', 'PATIENT', 'Woźniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uds6282', 'doszymanski@gmail.com', 'Dominik', '693191162', 'PATIENT', 'Szymański');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uos8638', 'ostepien@gmail.com', 'Ola', '749101127', 'PATIENT', 'Stępień');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas6215', 'asobczak@gmail.com', 'Alojzy', '642815808', 'PATIENT', 'Sobczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uab7154', 'agblaszczyk@gmail.com', 'Agnieszka', '339206413', 'PATIENT', 'Błaszczyk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas8932', 'aszulc@gmail.com', 'Andrea', '222186738', 'PATIENT', 'Szulc');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udl1033', 'dlis@gmail.com', 'Damian', '200827314', 'PATIENT', 'Lis');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ung6121', 'nglowacka@gmail.com', 'Natalia', '814023702', 'PATIENT', 'Głowacka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufk2738', 'fkaminska@gmail.com', 'Florentyna', '350991109', 'PATIENT', 'Kamińska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas8201', 'aszymanska@gmail.com', 'Anna', '462414199', 'PATIENT', 'Szymańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujc3749', 'jczerwinski@gmail.com', 'Jędrzej', '382399449', 'PATIENT', 'Czerwiński');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uek8847', 'ekolodziej@gmail.com', 'Elżbieta', '260078392', 'PATIENT', 'Kołodziej');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujm1968', 'jmaciejewska@gmail.com', 'Jagoda', '655775356', 'PATIENT', 'Maciejewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uka8749', 'kadamski@gmail.com', 'Kewin', '505369786', 'PATIENT', 'Adamski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufk6380', 'fkalinowska@gmail.com', 'Faustyna', '432410296', 'PATIENT', 'Kalinowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uhn4351', 'hnowak@gmail.com', 'Heronim', '869581653', 'PATIENT', 'Nowak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('urm7816', 'rmazur@gmail.com', 'Róża', '868736928', 'PATIENT', 'Mazur');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufc4423', null, 'Florencja', null, 'PATIENT', 'Czarnecka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uas4436', 'aszczepanska@gmail.com', 'Anastazja', '835115898', 'PATIENT', 'Szczepańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujg2434', 'jgorski@gmail.com', 'Jacek', '767698850', 'PATIENT', 'Górski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubp6577', 'bprzybylska@gmail.com', 'Barbara', '215900633', 'PATIENT', 'Przybylska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujg5344', 'jglowacka@gmail.com', 'Joachim', '307495902', 'PATIENT', 'Głowacka');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upm1401', 'pmakowska@gmail.com', 'Pamela', '868413631', 'PATIENT', 'Makowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukj3236', 'kjakubowski@gmail.com', 'Krzysztof', '663521156', 'PATIENT', 'Jakubowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uow3456', 'owitkowski@gmail.com', 'Oktawian', '471320192', 'PATIENT', 'Witkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udj5741', 'djakubowski@gmail.com', 'Daniel', '236359967', 'PATIENT', 'Jakubowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukc8514', 'koczarnecki@gmail.com', 'Konrad', '696510073', 'PATIENT', 'Czarnecki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umt6435', 'mtomaszewski@gmail.com', 'Mirosław', '683638492', 'PATIENT', 'Tomaszewski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ueb1974', 'eborkowski@gmail.com', 'Emil', '397758736', 'PATIENT', 'Borkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ups6982', 'pszczepanska@gmail.com', 'Pamela', '278768857', 'PATIENT', 'Szczepańska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukm6019', 'kmarciniak@gmail.com', 'Konrad', '575140630', 'PATIENT', 'Marciniak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uej4920', 'ejasinska@gmail.com', 'Emilia', '762070778', 'PATIENT', 'Jasińska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufp8217', 'fpawlak@gmail.com', 'Fabian', '605554205', 'PATIENT', 'Pawlak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubm3417', 'bmakowski@gmail.com', 'Borys', '441579317', 'PATIENT', 'Makowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uak4840', 'akalinowska@gmail.com', 'Alana', '437197088', 'PATIENT', 'Kalinowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujs8075', 'jszymczak@gmail.com', 'Jarosław', '188709687', 'PATIENT', 'Szymczak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uwl3956', 'wlis@gmail.com', 'Wanda', '582555401', 'PATIENT', 'Lis');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uks2091', 'ksawicki@gmail.com', 'Korneliusz', '792085191', 'PATIENT', 'Sawicki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('umc3013', 'mczarnecki@gmail.com', 'Martin', '490827078', 'PATIENT', 'Czarnecki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ujp7307', 'jpiotrowski@gmail.com', 'Janusz', '599831654', 'PATIENT', 'Piotrowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uez5154', 'eziolkowska@gmail.com', 'Eliza', '338257038', 'PATIENT', 'Ziółkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('utb1544', 'tbak@gmail.com', 'Teresa', '584475948', 'PATIENT', 'Bąk');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('unk4968', 'nkrupa@gmail.com', 'Natasza', '588503857', 'PATIENT', 'Krupa');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ulk7696', 'lkowalska@gmail.com', 'Lara', '228147213', 'PATIENT', 'Kowalska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('uwk1974', 'wkwiatkowska@gmail.com', 'Wioletta', '780460423', 'PATIENT', 'Kwiatkowska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ugp6329', 'gpietrzak@gmail.com', 'Gustaw', '555476175', 'PATIENT', 'Pietrzak');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('unw1654', 'nwasilewska@gmail.com', 'Norbert', '755997987', 'PATIENT', 'Wasilewska');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubw7822', 'bwojcik@gmail.com', 'Bogna', '430213357', 'PATIENT', 'Wójcik');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('udz6012', null, 'Dawid', '255173539', 'PATIENT', 'Zawadzki');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ufp8646', 'fprzybylski@gmail.com', 'Fryderyk', '196824924', 'PATIENT', 'Przybylski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ubm5526', 'bmalinowski@gmail.com', 'Bolesław', '500919561', 'PATIENT', 'Malinowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ukk2500', null, 'Kacper', null, 'PATIENT', 'Kwiatkowski');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('ure2716', 'klinika.recepcja@gmail.com', '-', '262529387', 'RECEPTION', '-');
INSERT INTO public.users (internal_name, email, name, phone, role, surname) VALUES ('upi6872', 'klinika.pielegniarki@gmail.com', '-', '989452761', 'NURSE', '-');


-- Drop/create users

DROP USER IF EXISTS upp8259;
DROP USER IF EXISTS ulw2972;
DROP USER IF EXISTS uom3316;
DROP USER IF EXISTS uab3920;
DROP USER IF EXISTS uok7685;
DROP USER IF EXISTS upl7829;
DROP USER IF EXISTS ukw5503;
DROP USER IF EXISTS uwu6348;
DROP USER IF EXISTS ufm3054;
DROP USER IF EXISTS uam1326;
DROP USER IF EXISTS ubs6736;
DROP USER IF EXISTS uad8711;
DROP USER IF EXISTS uaw1269;
DROP USER IF EXISTS uom4855;
DROP USER IF EXISTS uab1977;
DROP USER IF EXISTS ulb7340;
DROP USER IF EXISTS ueb3850;
DROP USER IF EXISTS ues8714;
DROP USER IF EXISTS udb1842;
DROP USER IF EXISTS ukt2931;
DROP USER IF EXISTS umb8806;
DROP USER IF EXISTS ujc8219;
DROP USER IF EXISTS ukm5491;
DROP USER IF EXISTS ujj2254;
DROP USER IF EXISTS ueb6282;
DROP USER IF EXISTS uds8736;
DROP USER IF EXISTS ulj6459;
DROP USER IF EXISTS uaw1866;
DROP USER IF EXISTS uom4307;
DROP USER IF EXISTS ubc6266;
DROP USER IF EXISTS uao6499;
DROP USER IF EXISTS ump7612;
DROP USER IF EXISTS uas6402;
DROP USER IF EXISTS ujm8492;
DROP USER IF EXISTS umb2360;
DROP USER IF EXISTS ujs1728;
DROP USER IF EXISTS uaa8918;
DROP USER IF EXISTS ukl7411;
DROP USER IF EXISTS uiz6077;
DROP USER IF EXISTS uaz4864;
DROP USER IF EXISTS umk6034;
DROP USER IF EXISTS ubz8480;
DROP USER IF EXISTS ueu6169;
DROP USER IF EXISTS uas8888;
DROP USER IF EXISTS uks8798;
DROP USER IF EXISTS uhs4039;
DROP USER IF EXISTS uda4482;
DROP USER IF EXISTS uok4459;
DROP USER IF EXISTS ukm8184;
DROP USER IF EXISTS ubs4737;
DROP USER IF EXISTS uad6868;
DROP USER IF EXISTS uek7255;
DROP USER IF EXISTS uap7575;
DROP USER IF EXISTS uiw2684;
DROP USER IF EXISTS urw7987;
DROP USER IF EXISTS ujc6114;
DROP USER IF EXISTS ukr3609;
DROP USER IF EXISTS uis8067;
DROP USER IF EXISTS ubs6381;
DROP USER IF EXISTS umk8333;
DROP USER IF EXISTS ujz1627;
DROP USER IF EXISTS urz6585;
DROP USER IF EXISTS uip2034;
DROP USER IF EXISTS uak6944;
DROP USER IF EXISTS uat3296;
DROP USER IF EXISTS upw8033;
DROP USER IF EXISTS urp8127;
DROP USER IF EXISTS uck6813;
DROP USER IF EXISTS uak8449;
DROP USER IF EXISTS ujr3401;
DROP USER IF EXISTS umm7740;
DROP USER IF EXISTS uek8278;
DROP USER IF EXISTS ufw7179;
DROP USER IF EXISTS upl2613;
DROP USER IF EXISTS uwu1604;
DROP USER IF EXISTS ucw1130;
DROP USER IF EXISTS ums3613;
DROP USER IF EXISTS ual2519;
DROP USER IF EXISTS ujk4868;
DROP USER IF EXISTS ugu6869;
DROP USER IF EXISTS uas5410;
DROP USER IF EXISTS uor7486;
DROP USER IF EXISTS ufs4551;
DROP USER IF EXISTS ufa7803;
DROP USER IF EXISTS uom4660;
DROP USER IF EXISTS ubk7925;
DROP USER IF EXISTS udm3279;
DROP USER IF EXISTS uaa5691;
DROP USER IF EXISTS uak2960;
DROP USER IF EXISTS udr2075;
DROP USER IF EXISTS umw6154;
DROP USER IF EXISTS umw4458;
DROP USER IF EXISTS ucb4630;
DROP USER IF EXISTS uam7974;
DROP USER IF EXISTS uak8169;
DROP USER IF EXISTS ukg4079;
DROP USER IF EXISTS ubr3869;
DROP USER IF EXISTS ues8253;
DROP USER IF EXISTS unk4981;
DROP USER IF EXISTS uzw2425;
DROP USER IF EXISTS ubs2833;
DROP USER IF EXISTS ujw6907;
DROP USER IF EXISTS uhw8596;
DROP USER IF EXISTS uek5591;
DROP USER IF EXISTS uag6434;
DROP USER IF EXISTS uaz3820;
DROP USER IF EXISTS uls2000;
DROP USER IF EXISTS umw3083;
DROP USER IF EXISTS uir2545;
DROP USER IF EXISTS uas3995;
DROP USER IF EXISTS ukm3758;
DROP USER IF EXISTS ufk1825;
DROP USER IF EXISTS umk7697;
DROP USER IF EXISTS ukc6162;
DROP USER IF EXISTS uau3799;
DROP USER IF EXISTS uhj3156;
DROP USER IF EXISTS umj3134;
DROP USER IF EXISTS uaz1361;
DROP USER IF EXISTS upm2105;
DROP USER IF EXISTS uls2995;
DROP USER IF EXISTS uhs5958;
DROP USER IF EXISTS upl8483;
DROP USER IF EXISTS uwm4211;
DROP USER IF EXISTS udm2313;
DROP USER IF EXISTS ukc8172;
DROP USER IF EXISTS uaa8358;
DROP USER IF EXISTS uem6331;
DROP USER IF EXISTS url8469;
DROP USER IF EXISTS ubm1030;
DROP USER IF EXISTS ukp1037;
DROP USER IF EXISTS uok8030;
DROP USER IF EXISTS uks3363;
DROP USER IF EXISTS uas7405;
DROP USER IF EXISTS uru7282;
DROP USER IF EXISTS ukc3922;
DROP USER IF EXISTS ujk2066;
DROP USER IF EXISTS udc7390;
DROP USER IF EXISTS ueb5367;
DROP USER IF EXISTS urp6851;
DROP USER IF EXISTS usm7695;
DROP USER IF EXISTS uer5396;
DROP USER IF EXISTS uis3618;
DROP USER IF EXISTS uab2473;
DROP USER IF EXISTS uls6179;
DROP USER IF EXISTS ukp3180;
DROP USER IF EXISTS urw5817;
DROP USER IF EXISTS uet3182;
DROP USER IF EXISTS upk8570;
DROP USER IF EXISTS ujw5588;
DROP USER IF EXISTS uds6282;
DROP USER IF EXISTS uos8638;
DROP USER IF EXISTS uas6215;
DROP USER IF EXISTS uab7154;
DROP USER IF EXISTS uas8932;
DROP USER IF EXISTS udl1033;
DROP USER IF EXISTS ung6121;
DROP USER IF EXISTS ufk2738;
DROP USER IF EXISTS uas8201;
DROP USER IF EXISTS ujc3749;
DROP USER IF EXISTS uek8847;
DROP USER IF EXISTS ujm1968;
DROP USER IF EXISTS uka8749;
DROP USER IF EXISTS ufk6380;
DROP USER IF EXISTS uhn4351;
DROP USER IF EXISTS urm7816;
DROP USER IF EXISTS ufc4423;
DROP USER IF EXISTS uas4436;
DROP USER IF EXISTS ujg2434;
DROP USER IF EXISTS ubp6577;
DROP USER IF EXISTS ujg5344;
DROP USER IF EXISTS upm1401;
DROP USER IF EXISTS ukj3236;
DROP USER IF EXISTS uow3456;
DROP USER IF EXISTS udj5741;
DROP USER IF EXISTS ukc8514;
DROP USER IF EXISTS umt6435;
DROP USER IF EXISTS ueb1974;
DROP USER IF EXISTS ups6982;
DROP USER IF EXISTS ukm6019;
DROP USER IF EXISTS uej4920;
DROP USER IF EXISTS ufp8217;
DROP USER IF EXISTS ubm3417;
DROP USER IF EXISTS uak4840;
DROP USER IF EXISTS ujs8075;
DROP USER IF EXISTS uwl3956;
DROP USER IF EXISTS uks2091;
DROP USER IF EXISTS umc3013;
DROP USER IF EXISTS ujp7307;
DROP USER IF EXISTS uez5154;
DROP USER IF EXISTS utb1544;
DROP USER IF EXISTS unk4968;
DROP USER IF EXISTS ulk7696;
DROP USER IF EXISTS uwk1974;
DROP USER IF EXISTS ugp6329;
DROP USER IF EXISTS unw1654;
DROP USER IF EXISTS ubw7822;
DROP USER IF EXISTS udz6012;
DROP USER IF EXISTS ufp8646;
DROP USER IF EXISTS ubm5526;
DROP USER IF EXISTS ukk2500;
DROP USER IF EXISTS ure2716;
DROP USER IF EXISTS upi6872;

CREATE USER upp8259 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ulw2972 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uom3316 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uab3920 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uok7685 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER upl7829 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ukw5503 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uwu6348 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ufm3054 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uam1326 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ubs6736 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uad8711 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uaw1269 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uom4855 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER uab1977 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ulb7340 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ueb3850 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER ues8714 LOGIN ENCRYPTED PASSWORD 'lekarz' IN ROLE gp_doctors;
CREATE USER udb1842 WITH SUPERUSER CREATEDB CREATEROLE REPLICATION BYPASSRLS LOGIN ENCRYPTED PASSWORD 'administrator' IN ROLE gp_admins;
CREATE USER ukt2931 WITH SUPERUSER CREATEDB CREATEROLE REPLICATION BYPASSRLS LOGIN ENCRYPTED PASSWORD 'administrator' IN ROLE gp_admins;
CREATE USER umb8806 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujc8219 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukm5491 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujj2254 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ueb6282 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uds8736 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ulj6459 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaw1866 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uom4307 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubc6266 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uao6499 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ump7612 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas6402 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujm8492 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umb2360 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujs1728 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaa8918 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukl7411 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uiz6077 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaz4864 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umk6034 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubz8480 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ueu6169 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas8888 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uks8798 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uhs4039 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uda4482 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uok4459 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukm8184 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubs4737 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uad6868 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uek7255 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uap7575 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uiw2684 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urw7987 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujc6114 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukr3609 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uis8067 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubs6381 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umk8333 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujz1627 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urz6585 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uip2034 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uak6944 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uat3296 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upw8033 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urp8127 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uck6813 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uak8449 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujr3401 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umm7740 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uek8278 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufw7179 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upl2613 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uwu1604 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ucw1130 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ums3613 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ual2519 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujk4868 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ugu6869 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas5410 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uor7486 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufs4551 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufa7803 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uom4660 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubk7925 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udm3279 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaa5691 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uak2960 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udr2075 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umw6154 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umw4458 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ucb4630 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uam7974 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uak8169 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukg4079 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubr3869 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ues8253 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER unk4981 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uzw2425 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubs2833 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujw6907 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uhw8596 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uek5591 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uag6434 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaz3820 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uls2000 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umw3083 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uir2545 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas3995 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukm3758 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufk1825 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umk7697 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukc6162 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uau3799 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uhj3156 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umj3134 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaz1361 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upm2105 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uls2995 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uhs5958 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upl8483 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uwm4211 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udm2313 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukc8172 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uaa8358 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uem6331 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER url8469 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubm1030 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukp1037 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uok8030 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uks3363 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas7405 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uru7282 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukc3922 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujk2066 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udc7390 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ueb5367 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urp6851 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER usm7695 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uer5396 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uis3618 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uab2473 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uls6179 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukp3180 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urw5817 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uet3182 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upk8570 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujw5588 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uds6282 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uos8638 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas6215 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uab7154 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas8932 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udl1033 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ung6121 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufk2738 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas8201 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujc3749 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uek8847 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujm1968 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uka8749 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufk6380 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uhn4351 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER urm7816 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufc4423 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uas4436 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujg2434 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubp6577 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujg5344 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER upm1401 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukj3236 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uow3456 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udj5741 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukc8514 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umt6435 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ueb1974 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ups6982 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukm6019 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uej4920 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufp8217 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubm3417 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uak4840 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujs8075 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uwl3956 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uks2091 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER umc3013 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ujp7307 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uez5154 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER utb1544 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER unk4968 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ulk7696 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER uwk1974 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ugp6329 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER unw1654 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubw7822 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER udz6012 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ufp8646 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ubm5526 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ukk2500 LOGIN ENCRYPTED PASSWORD 'pacjent' IN ROLE gp_patients;
CREATE USER ure2716 LOGIN ENCRYPTED PASSWORD 'recepcja' IN ROLE gp_receptionists;
CREATE USER upi6872 LOGIN ENCRYPTED PASSWORD 'pielęgniarka' IN ROLE gp_nurses;


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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('29', 'Dąbrowa', '93040883597', 'Stara Wieś', '23-090', '', 8);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('40', 'Dział', '77033011419', 'Stara Wieś', '23-090', '', 28);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Borki', '93020545471', 'Stara Wieś', '23-090', '', 29);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('62', 'Gościniec', '89071152579', 'Stara Wieś', '23-090', null, 47);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('69', 'Działy', '52021033194', 'Stara Wieś', '23-090', null, 48);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('32', 'Stary Dwór', '96011564451', 'Stary Dwór', '45-954', '', 59);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Gościniec', '98020244795', 'Stary Dwór', '45-954', null, 82);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('65', 'Budzyń', '48082587585', 'Stary Dwór', '45-954', 'Świętojańska', 83);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('24', 'Czarny Las', '67041249636', 'Stary Dwór', '45-954', 'Wieżowa', 84);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('34', 'Kuźnica', '94062082856', 'Stary Dwór', '45-954', 'Różany Targ', 85);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('47', 'Miasteczko', '48050294651', 'Stary Dwór', '45-954', 'Kurzanoga', 86);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('42', 'Niwki', '5260485813', 'Stary Dwór', '45-954', null, 87);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('36', 'Piasek', '98042974694', 'Stary Dwór', '45-954', 'Garbary', 88);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('11', 'Rybaki', '55010172438', 'Stary Dwór', '45-954', null, 89);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('21', 'Środek', '4281454374', 'Stary Dwór', '45-954', 'Wielka', 90);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Wrzosy', '73021985113', 'Stary Dwór', '45-954', 'Wodna', 91);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('35', 'Długie', '60080854922', 'Stary Dwór', '45-954', 'Wrocławska', 92);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('33', 'Łaziska', '92061299369', 'Stary Dwór', '45-954', 'Zagórze', 93);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('45', 'Łęgi', '79122462687', 'Stary Dwór', '45-954', 'Polna ', 94);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('56', 'Mała Wieś', '79120523555', 'Stary Dwór', '45-954', null, 95);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Gościniec', '78042275575', 'Stary Dwór', '45-954', null, 117);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('64', 'Działy', '99061872824', 'Stary Dwór', '45-954', null, 118);
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
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('15', 'Górna Wieś', '52030512941', 'Stary Dwór', '45-954', null, 153);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('58', 'Krzyżówka', '94051445237', 'Stary Dwór', '45-954', 'Polna ', 154);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Michałówka', '77051123262', 'Stary Dwór', '45-954', 'Łąkowa', 155);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('67', 'Młynki', '6262538189', 'Stary Dwór', '45-954', 'Brzozowa', 156);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('28', 'Mokre', '50112549645', 'Stary Dwór', '45-954', 'Kwiatowa ', 157);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('31', 'Polanka', '65061828417', 'Stary Dwór', '45-954', 'Kościelna', 158);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('29', 'Romanów', '85112773712', 'Stary Dwór', '45-954', 'Sosnowa', 159);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('26', 'Trzcianka', '66101437941', 'Stary Dwór', '45-954', 'Zielona', 160);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('52', 'Ustronie', '59030545454', 'Stary Dwór', '45-954', 'Parkowa', 161);
INSERT INTO public.patients (building, city, pesel, post_city, post_code, street, id) VALUES ('19', 'Wieś', '79060886336', 'Stary Dwór', '45-954', null, 162);
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
-- TOC entry 3396 (class 0 OID 17211)
-- Dependencies: 218
-- Data for Name: doctors; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (1, 'Paweł', 'Przybylski', 15, 'lekarz ogólny');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (2, 'Lucyna', 'Wójcik', 15, 'lekarz ogólny');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (3, 'Oktawian', 'Michalak', 15, 'pediatra');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (4, 'Anastazy', 'Borkowski', 15, 'pediatra');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (5, 'Ola', 'Kaźmierczak', 15, 'dermatolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (6, 'Paula', 'Lewandowska', 15, 'dermatolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (7, 'Kazimierz', 'Woźniak', 45, 'kardiolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (8, 'Wioletta', 'Urbańska', 45, 'kardiolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (9, 'Florian', 'Michalak', 30, 'okulista');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (10, 'Amalia', 'Maciejewska', 30, 'okulista');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (11, 'Bartłomiej', 'Sadowski', 15, 'laryngolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (12, 'Artur', 'Duda', 15, 'laryngolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (13, 'Alana', 'Wiśniewska', 20, 'neurolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (14, 'Oskar', 'Mróz', 20, 'neurolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (15, 'Arkadiusz', 'Błaszczyk', 60, 'psycholog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (16, 'Luiza', 'Baran', 60, 'psycholog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (17, 'Eliza', 'Baranowska', 60, 'stomatolog');
INSERT INTO public.doctors (id, name, surname, default_visit_duration, speciality) VALUES (18, 'Eryk', 'Szymański', 60, 'stomatolog');


--
-- TOC entry 3393 (class 0 OID 17197)
-- Dependencies: 215
-- Data for Name: appointments; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:25', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 15, 100, 100, 1);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:26', 'Wizyta kontrolna.', '', '2023-06-26 08:15:00', 15, 19, 19, 1);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:27', 'Omówienie wyników badań krwi.', '', '2023-06-26 08:30:00', 15, 102, 102, 1);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:27', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 15, 103, 103, 2);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:29', 'Wizyta kontrolna.', '', '2023-06-26 08:15:00', 15, 104, 104, 2);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:30', 'Przeziębienie.', '', '2023-06-26 17:30:00', 15, 201, 105, 2);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:31', 'Wizyta kontrolna.', '', '2023-06-28 15:00:00', 15, 106, 106, 3);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:32', 'Wizyta kontrolna.', '', '2023-06-28 15:15:00', 15, 107, 107, 3);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:33', 'Przeziębienie.', '', '2023-06-28 15:30:00', 15, 108, 108, 3);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:34', 'Wizyta kontrolna.', '', '2023-06-27 15:00:00', 15, 201, 109, 4);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:35', 'Wizyta kontrolna.', '', '2023-06-27 15:15:00', 15, 60, 60, 4);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:36', 'Wizyta kontrolna.', '', '2023-06-27 15:30:00', 15, 201, 111, 4);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:37', 'Zmiany skórne.', '', '2023-06-26 17:00:00', 15, 112, 112, 5);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:38', 'Wysypka.', '', '2023-06-26 17:15:00', 15, 113, 113, 5);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:39', 'Trądzik.', '', '2023-06-26 17:30:00', 15, 56, 56, 5);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:40', 'Zmiany skórne.', '', '2023-06-27 09:00:00', 15, 201, 78, 6);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:41', 'Zmiany skórne.', '', '2023-06-27 09:15:00', 15, 116, 116, 6);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:42', 'Zmiany skórne.', '', '2023-06-27 09:30:00', 15, 201, 117, 6);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:43', 'Wizyta kontrolna.', '', '2023-06-26 08:00:00', 45, 118, 118, 7);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:44', 'Badanie EKG.', '', '2023-06-26 08:45:00', 45, 23, 23, 7);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:45', 'Wizyta kontrolna.', '', '2023-06-28 08:00:00', 45, 120, 120, 7);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:46', 'Założenie holtera.', '', '2023-06-27 08:00:00', 45, 121, 121, 8);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:47', 'Omówienie wyników badania holterem.', '', '2023-06-27 08:45:00', 45, 122, 122, 8);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:48', 'Echo serca.', '', '2023-06-29 08:00:00', 45, 201, 123, 8);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:49', 'Badanie wzroku.', '', '2023-06-26 13:30:00', 30, 201, 124, 9);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:50', 'Badanie wzroku.', '', '2023-06-26 14:00:00', 30, 201, 125, 9);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:51', 'Badanie wzroku.', '', '2023-06-26 14:30:00', 30, 126, 126, 9);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:52', 'Badanie wzroku.', '', '2023-06-27 13:30:00', 30, 127, 127, 10);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:53', 'Badanie wzroku.', '', '2023-06-27 14:00:00', 30, 201, 128, 10);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:54', 'Badanie wzroku.', '', '2023-06-27 14:30:00', 30, 129, 129, 10);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:55', 'Badanie słuchu.', '', '2023-06-26 13:30:00', 15, 130, 130, 11);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:56', 'Badanie słuchu.', '', '2023-06-26 13:45:00', 15, 131, 131, 11);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:57', 'Badanie słuchu.', '', '2023-06-26 14:00:00', 15, 132, 132, 11);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:58', 'Badanie słuchu.', '', '2023-06-27 13:30:00', 15, 201, 133, 12);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:02:59', 'Badanie słuchu.', '', '2023-06-27 13:45:00', 15, 134, 134, 12);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:00', 'Badanie słuchu.', '', '2023-06-27 14:00:00', 15, 135, 135, 12);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:01', 'Wizyta kontrolna.', '', '2023-06-26 14:00:00', 20, 136, 136, 13);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:02', 'Konsultacja neurologiczna.', '', '2023-06-26 14:20:00', 20, 137, 137, 13);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:03', 'Wizyta kontrolna.', '', '2023-06-26 14:40:00', 20, 138, 138, 13);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:04', 'Konsultacja neurologiczna.', '', '2023-06-28 14:00:00', 20, 139, 139, 14);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:05', 'Konsultacja neurologiczna.', '', '2023-06-28 14:20:00', 20, 140, 140, 14);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:06', 'Wizyta kontrolna.', '', '2023-06-28 14:40:00', 20, 201, 141, 14);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:07', 'Kontynuacja terapii.', '', '2023-06-26 08:00:00', 60, 142, 142, 15);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:08', 'Kontynuacja terapii.', '', '2023-06-26 09:00:00', 60, 143, 143, 15);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:09', 'Kontynuacja terapii.', '', '2023-06-26 10:00:00', 60, 144, 144, 15);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:10', 'Kontynuacja terapii.', '', '2023-06-27 08:00:00', 60, 145, 145, 16);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:11', 'Kontynuacja terapii.', '', '2023-06-27 09:00:00', 60, 146, 146, 16);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:12', 'Kontynuacja terapii.', '', '2023-06-27 10:00:00', 60, 147, 147, 16);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:13', 'Leczenie kanałowe.', '', '2023-06-26 08:00:00', 60, 201, 148, 17);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:14', 'Wybielanie.', '', '2023-06-26 09:00:00', 60, 149, 149, 17);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:15', 'Korona tymczasowa.', '', '2023-06-26 10:00:00', 60, 201, 150, 17);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:16', 'Wizyta kontrolna.', '', '2023-06-26 10:00:00', 60, 151, 151, 18);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:17', 'Wybielanie.', '', '2023-06-26 11:00:00', 60, 152, 152, 18);
INSERT INTO public.appointments (added_date, notes, tags, date, duration, added_by_user_id, patient_id, doctor_id) VALUES ('2023-04-12 15:03:18', 'Wizyta kontrolna.', '', '2023-06-26 12:00:00', 60, 153, 153, 18);


--
-- TOC entry 3398 (class 0 OID 17217)
-- Dependencies: 220
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.notifications (content, read_date, sent_date, destination_user_id, source_user_id) VALUES ('Umówiono wizytę \nPacjent: Zofia Wojciechowska\nData:  2023-06-26 08:00:00\nCel wizyty: Wizyta kontrolna.', '2023-04-12 16:02:25', '2023-04-12 15:02:25', 1, 100);
INSERT INTO public.notifications (content, read_date, sent_date, destination_user_id, source_user_id) VALUES ('Umówiono wizytę \nPacjent: Dobromir Błaszczyk\nData:  2023-06-26 08:15:00\nCel wizyty: Wizyta kontrolna.', '2023-04-12 16:02:26', '2023-04-12 15:02:26', 1, 19);
INSERT INTO public.notifications (content, read_date, sent_date, destination_user_id, source_user_id) VALUES ('Umówiono wizytę \nPacjent: Julian Witkowski\nData:  2023-06-26 08:30:00\nCel wizyty: Omówienie wyników badań krwi.', '2023-04-12 16:02:27', '2023-04-12 15:02:27', 1, 102);


--
-- TOC entry 3401 (class 0 OID 17230)
-- Dependencies: 223
-- Data for Name: prescriptions; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-10 13:02:25', 'Krople do oczu Kroplex; dawkowanie: codziennie rano i wieczorem ', '', '7275', 9, 96);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-10 08:02:25', 'Tabletki Cardio; dawkowanie: codziennie rano 1 tabletka', '', '4434', 7, 97);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-11 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '4943', 1, 97);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-12 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7466', 1, 97);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-13 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '3979', 2, 97);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-14 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '4163', 2, 97);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-17 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '3095', 3, 84);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-18 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7478', 3, 34);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-19 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7443', 4, 71);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-20 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '6486', 4, 50);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-21 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '7487', 7, 34);
INSERT INTO public.prescriptions (added_date, notes, tags, government_id, added_by_user_id, patient_id) VALUES ('2023-04-24 08:02:25', 'Tabletki Witaminex; dawkowanie: codziennie rano 1 tabletka', '', '2851', 7, 97);


--
-- TOC entry 3403 (class 0 OID 17238)
-- Dependencies: 225
-- Data for Name: referrals; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.referrals (added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES ('2023-04-12 15:02:00', 'Podejrzenie pogorszenia słuchu.', '', 'Pacjentka doznała częściowej utraty słuchu i wymaga aparatu słuchowego.', '2023-04-17 14:00:00', '4221', 'Poradnia laryngologiczna', 1, 56);
INSERT INTO public.referrals (added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES ('2023-04-13 15:02:00', 'Zmiany skórne charakterystyczne dla alergii. Wymagane badania alergologiczne.', '', 'Stwierdzono alergię na kocią sierść.', '2023-04-18 14:00:00', '6013', 'Poradnia alergologiczna', 2, 58);
INSERT INTO public.referrals (added_date, notes, tags, feedback, fulfilment_date, government_id, point_of_interest, added_by_user_id, patient_id) VALUES ('2023-04-13 08:02:00', 'Zmiany skórne charakterystyczne dla alergii. Wymagane badania alergologiczne.', '', NULL, NULL, '4481', 'Poradnia alergologiczna', 2, 33);


--
-- TOC entry 3405 (class 0 OID 17246)
-- Dependencies: 227
-- Data for Name: schedule_entries; Type: TABLE DATA; Schema: public; Owner: admin
--

--INSERT INTO public.schedule_entries (date_begin, date_end, type, user_id) VALUES ('2023-08-07 00:00:00', '2023-08-21 00:00:00', 'urlop', 5);
--INSERT INTO public.schedule_entries (date_begin, date_end, type, user_id) VALUES ('2023-08-21 00:00:00', '2023-09-04 00:00:00', 'urlop', 2);


-- Completed on 2023-04-14 15:42:38

--
-- PostgreSQL database dump complete
--

