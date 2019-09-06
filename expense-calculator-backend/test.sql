--
-- PostgreSQL database cluster dump
--

-- Started on 2019-09-05 22:42:41

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE expenser;
ALTER ROLE expenser WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;
CREATE ROLE postgres;
ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'md553f48b7c4b76a86ce72276c5755f217d';






\connect template1

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5 (Debian 11.5-1.pgdg90+1)
-- Dumped by pg_dump version 11.5

-- Started on 2019-09-05 22:42:41

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

-- Completed on 2019-09-05 22:42:41

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5 (Debian 11.5-1.pgdg90+1)
-- Dumped by pg_dump version 11.5

-- Started on 2019-09-05 22:42:41

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
-- TOC entry 2927 (class 1262 OID 16384)
-- Name: expense_calculator; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE expense_calculator WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


\connect expense_calculator

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

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 198 (class 1259 OID 16403)
-- Name: card_owners; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.card_owners (
    card_id uuid NOT NULL,
    owner_id uuid NOT NULL
);


--
-- TOC entry 196 (class 1259 OID 16387)
-- Name: cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cards (
    id uuid NOT NULL,
    name text NOT NULL,
    comment text
);


--
-- TOC entry 199 (class 1259 OID 16418)
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    id uuid NOT NULL,
    name text NOT NULL,
    comment text
);


--
-- TOC entry 201 (class 1259 OID 16436)
-- Name: expenses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.expenses (
    monthly_calculation_id uuid NOT NULL,
    card_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text
);


--
-- TOC entry 202 (class 1259 OID 16454)
-- Name: incomes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.incomes (
    monthly_calculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text
);


--
-- TOC entry 200 (class 1259 OID 16426)
-- Name: monthly_calculations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.monthly_calculations (
    id uuid NOT NULL,
    year integer NOT NULL,
    month integer NOT NULL,
    comment text
);


--
-- TOC entry 203 (class 1259 OID 16472)
-- Name: personal_expense_corrections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.personal_expense_corrections (
    id uuid NOT NULL,
    monthly_claculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    category_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text
);


--
-- TOC entry 197 (class 1259 OID 16395)
-- Name: persons; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.persons (
    id uuid NOT NULL,
    name text NOT NULL
);


--
-- TOC entry 2779 (class 2606 OID 16407)
-- Name: card_owners card_owners_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.card_owners
    ADD CONSTRAINT card_owners_pkey PRIMARY KEY (card_id, owner_id);


--
-- TOC entry 2775 (class 2606 OID 16394)
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (id);


--
-- TOC entry 2781 (class 2606 OID 16425)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- TOC entry 2787 (class 2606 OID 16443)
-- Name: expenses expenses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expenses
    ADD CONSTRAINT expenses_pkey PRIMARY KEY (monthly_calculation_id, card_id);


--
-- TOC entry 2789 (class 2606 OID 16461)
-- Name: incomes incomes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incomes
    ADD CONSTRAINT incomes_pkey PRIMARY KEY (monthly_calculation_id, person_id);


--
-- TOC entry 2783 (class 2606 OID 16433)
-- Name: monthly_calculations monthly_calculations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monthly_calculations
    ADD CONSTRAINT monthly_calculations_pkey PRIMARY KEY (id);


--
-- TOC entry 2785 (class 2606 OID 16435)
-- Name: monthly_calculations monthly_calculations_year_month_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monthly_calculations
    ADD CONSTRAINT monthly_calculations_year_month_key UNIQUE (year, month);


--
-- TOC entry 2791 (class 2606 OID 16479)
-- Name: personal_expense_corrections personal_expense_corrections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_expense_corrections
    ADD CONSTRAINT personal_expense_corrections_pkey PRIMARY KEY (id);


--
-- TOC entry 2777 (class 2606 OID 16402)
-- Name: persons persons_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.persons
    ADD CONSTRAINT persons_pkey PRIMARY KEY (id);


--
-- TOC entry 2792 (class 2606 OID 16408)
-- Name: card_owners card; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.card_owners
    ADD CONSTRAINT card FOREIGN KEY (card_id) REFERENCES public.cards(id);


--
-- TOC entry 2794 (class 2606 OID 16444)
-- Name: expenses expenses_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expenses
    ADD CONSTRAINT expenses_card_id_fkey FOREIGN KEY (card_id) REFERENCES public.cards(id);


--
-- TOC entry 2795 (class 2606 OID 16449)
-- Name: expenses expenses_monthly_calculation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expenses
    ADD CONSTRAINT expenses_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id) REFERENCES public.monthly_calculations(id);


--
-- TOC entry 2796 (class 2606 OID 16462)
-- Name: incomes incomes_monthly_calculation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incomes
    ADD CONSTRAINT incomes_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id) REFERENCES public.monthly_calculations(id);


--
-- TOC entry 2797 (class 2606 OID 16467)
-- Name: incomes incomes_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incomes
    ADD CONSTRAINT incomes_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.persons(id);


--
-- TOC entry 2793 (class 2606 OID 16413)
-- Name: card_owners owner; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.card_owners
    ADD CONSTRAINT owner FOREIGN KEY (owner_id) REFERENCES public.persons(id);


--
-- TOC entry 2798 (class 2606 OID 16480)
-- Name: personal_expense_corrections personal_expense_corrections_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_expense_corrections
    ADD CONSTRAINT personal_expense_corrections_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- TOC entry 2799 (class 2606 OID 16485)
-- Name: personal_expense_corrections personal_expense_corrections_monthly_claculation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_expense_corrections
    ADD CONSTRAINT personal_expense_corrections_monthly_claculation_id_fkey FOREIGN KEY (monthly_claculation_id) REFERENCES public.monthly_calculations(id);


--
-- TOC entry 2800 (class 2606 OID 16490)
-- Name: personal_expense_corrections personal_expense_corrections_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.personal_expense_corrections
    ADD CONSTRAINT personal_expense_corrections_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.persons(id);


-- Completed on 2019-09-05 22:42:42

--
-- PostgreSQL database dump complete
--

\connect postgres

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5 (Debian 11.5-1.pgdg90+1)
-- Dumped by pg_dump version 11.5

-- Started on 2019-09-05 22:42:42

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

-- Completed on 2019-09-05 22:42:42

--
-- PostgreSQL database dump complete
--

-- Completed on 2019-09-05 22:42:42

--
-- PostgreSQL database cluster dump complete
--

