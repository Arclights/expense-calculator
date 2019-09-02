-- Table: public.cards

-- DROP TABLE public.cards;

CREATE TABLE public.cards
(
    id uuid NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT cards_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.cards
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.cards TO expenser;

GRANT ALL ON TABLE public.cards TO postgres;


-- Table: public.persons

-- DROP TABLE public.persons;

CREATE TABLE public.persons
(
    id uuid NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT persons_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.persons
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.persons TO expenser;

GRANT ALL ON TABLE public.persons TO postgres;


-- Table: public.card_owners

-- DROP TABLE public.card_owners;

CREATE TABLE public.card_owners
(
    card_id uuid NOT NULL,
    owner_id uuid NOT NULL,
    CONSTRAINT card_owners_pkey PRIMARY KEY (card_id, owner_id),
    CONSTRAINT card FOREIGN KEY (card_id)
        REFERENCES public.cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT owner FOREIGN KEY (owner_id)
        REFERENCES public.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE public.card_owners
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.card_owners TO expenser;

GRANT ALL ON TABLE public.card_owners TO postgres;



-- Table: public.categories

-- DROP TABLE public.categories;

CREATE TABLE public.categories
(
    id uuid NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT categories_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.categories
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.categories TO expenser;

GRANT ALL ON TABLE public.categories TO postgres;



-- Table: public.monthly_calculations

-- DROP TABLE public.monthly_calculations;

CREATE TABLE public.monthly_calculations
(
    id uuid NOT NULL,
    year integer NOT NULL,
    month integer NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT monthly_calculations_pkey PRIMARY KEY (id),
    CONSTRAINT monthly_calculations_year_month_key UNIQUE (year, month)

)

TABLESPACE pg_default;

ALTER TABLE public.monthly_calculations
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.monthly_calculations TO expenser;

GRANT ALL ON TABLE public.monthly_calculations TO postgres;


-- Table: public.expenses

-- DROP TABLE public.expenses;

CREATE TABLE public.expenses
(
    monthly_calculation_id uuid NOT NULL,
    card_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT expenses_pkey PRIMARY KEY (monthly_calculation_id, card_id),
    CONSTRAINT expenses_card_id_fkey FOREIGN KEY (card_id)
        REFERENCES public.cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT expenses_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES public.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE public.expenses
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.expenses TO expenser;

GRANT ALL ON TABLE public.expenses TO postgres;


-- Table: public.incomes

-- DROP TABLE public.incomes;

CREATE TABLE public.incomes
(
    monthly_calculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT incomes_pkey PRIMARY KEY (monthly_calculation_id, person_id),
    CONSTRAINT incomes_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES public.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT incomes_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES public.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE public.incomes
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.incomes TO expenser;

GRANT ALL ON TABLE public.incomes TO postgres;


-- Table: public.personal_expense_corrections

-- DROP TABLE public.personal_expense_corrections;

CREATE TABLE public.personal_expense_corrections
(
    id uuid NOT NULL,
    monthly_claculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    category_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT personal_expense_corrections_pkey PRIMARY KEY (id),
    CONSTRAINT personal_expense_corrections_category_id_fkey FOREIGN KEY (category_id)
        REFERENCES public.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_monthly_claculation_id_fkey FOREIGN KEY (monthly_claculation_id)
        REFERENCES public.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES public.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE public.personal_expense_corrections
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.personal_expense_corrections TO expenser;

GRANT ALL ON TABLE public.personal_expense_corrections TO postgres;