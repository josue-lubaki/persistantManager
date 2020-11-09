--
-- PostgreSQL database dump
--

-- Dumped from database version 13.0
-- Dumped by pg_dump version 13.0

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
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cours; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cours (
    coursid integer NOT NULL,
    namecours character varying NOT NULL,
    sigle character varying NOT NULL,
    description text
);


ALTER TABLE public.cours OWNER TO postgres;

--
-- Name: etudiant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.etudiant (
    etudiantid integer NOT NULL,
    fname character varying NOT NULL,
    lname character varying NOT NULL,
    age integer NOT NULL,
    CONSTRAINT check_age CHECK ((age > 16))
);


ALTER TABLE public.etudiant OWNER TO postgres;

--
-- Name: inscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inscription (
    inscriptionid integer NOT NULL,
    etudiantid integer NOT NULL,
    coursid integer NOT NULL,
    dates timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.inscription OWNER TO postgres;

--
-- Data for Name: cours; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cours (coursid, namecours, sigle, description) FROM stdin;
\.


--
-- Data for Name: etudiant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.etudiant (etudiantid, fname, lname, age) FROM stdin;
\.


--
-- Data for Name: inscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inscription (inscriptionid, etudiantid, coursid, dates) FROM stdin;
\.


--
-- Name: cours cours_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cours
    ADD CONSTRAINT cours_pkey PRIMARY KEY (coursid);


--
-- Name: etudiant etudiant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.etudiant
    ADD CONSTRAINT etudiant_pkey PRIMARY KEY (etudiantid);


--
-- Name: inscription inscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_pkey PRIMARY KEY (inscriptionid);


--
-- Name: inscription inscription_coursid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_coursid_fkey FOREIGN KEY (coursid) REFERENCES public.cours(coursid);


--
-- Name: inscription inscription_etudiantid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inscription
    ADD CONSTRAINT inscription_etudiantid_fkey FOREIGN KEY (etudiantid) REFERENCES public.etudiant(etudiantid);


--
-- PostgreSQL database dump complete
--

