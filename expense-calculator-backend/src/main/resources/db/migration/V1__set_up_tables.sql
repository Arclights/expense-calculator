DROP SCHEMA IF EXISTS expense_calculator;

CREATE SCHEMA expense_calculator;

GRANT USAGE ON SCHEMA expense_calculator TO expenser;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: expense_calculator.cards

-- DROP TABLE expense_calculator.cards;

CREATE TABLE expense_calculator.cards
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT cards_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.cards
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.cards TO expenser;

GRANT ALL ON TABLE expense_calculator.cards TO postgres;


-- Table: expense_calculator.persons

-- DROP TABLE expense_calculator.persons;

CREATE TABLE expense_calculator.persons
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT persons_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.persons
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.persons TO expenser;

GRANT ALL ON TABLE expense_calculator.persons TO postgres;


-- Table: expense_calculator.card_owners

-- DROP TABLE expense_calculator.card_owners;

CREATE TABLE expense_calculator.card_owners
(
    card_id uuid NOT NULL,
    owner_id uuid NOT NULL,
    CONSTRAINT card_owners_pkey PRIMARY KEY (card_id, owner_id),
    CONSTRAINT card FOREIGN KEY (card_id)
        REFERENCES expense_calculator.cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT owner FOREIGN KEY (owner_id)
        REFERENCES expense_calculator.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.card_owners
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.card_owners TO expenser;

GRANT ALL ON TABLE expense_calculator.card_owners TO postgres;



-- Table: expense_calculator.categories

-- DROP TABLE expense_calculator.categories;

CREATE TABLE expense_calculator.categories
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT categories_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.categories
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.categories TO expenser;

GRANT ALL ON TABLE expense_calculator.categories TO postgres;



-- Table: expense_calculator.monthly_calculations

-- DROP TABLE expense_calculator.monthly_calculations;

CREATE TABLE expense_calculator.monthly_calculations
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    year integer NOT NULL,
    month integer NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT monthly_calculations_pkey PRIMARY KEY (id),
    CONSTRAINT monthly_calculations_year_month_key UNIQUE (year, month)

)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.monthly_calculations
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.monthly_calculations TO expenser;

GRANT ALL ON TABLE expense_calculator.monthly_calculations TO postgres;


-- Table: expense_calculator.expenses

-- DROP TABLE expense_calculator.expenses;

CREATE TABLE expense_calculator.expenses
(
    monthly_calculation_id uuid NOT NULL,
    card_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT expenses_pkey PRIMARY KEY (monthly_calculation_id, card_id),
    CONSTRAINT expenses_card_id_fkey FOREIGN KEY (card_id)
        REFERENCES expense_calculator.cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT expenses_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES expense_calculator.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.expenses
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.expenses TO expenser;

GRANT ALL ON TABLE expense_calculator.expenses TO postgres;


-- Table: expense_calculator.incomes

-- DROP TABLE expense_calculator.incomes;

CREATE TABLE expense_calculator.incomes
(
    monthly_calculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT incomes_pkey PRIMARY KEY (monthly_calculation_id, person_id),
    CONSTRAINT incomes_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES expense_calculator.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT incomes_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES expense_calculator.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.incomes
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.incomes TO expenser;

GRANT ALL ON TABLE expense_calculator.incomes TO postgres;


-- Table: expense_calculator.personal_expense_corrections

-- DROP TABLE expense_calculator.personal_expense_corrections;

CREATE TABLE expense_calculator.personal_expense_corrections
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    monthly_claculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    category_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT personal_expense_corrections_pkey PRIMARY KEY (id),
    CONSTRAINT personal_expense_corrections_category_id_fkey FOREIGN KEY (category_id)
        REFERENCES expense_calculator.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_monthly_claculation_id_fkey FOREIGN KEY (monthly_claculation_id)
        REFERENCES expense_calculator.monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES expense_calculator.persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE expense_calculator.personal_expense_corrections
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expense_calculator.personal_expense_corrections TO expenser;

GRANT ALL ON TABLE expense_calculator.personal_expense_corrections TO postgres;