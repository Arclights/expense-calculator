CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: cards
CREATE TABLE cards
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT cards_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE cards
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE cards TO expenser;

GRANT ALL ON TABLE cards TO postgres;


-- Table: persons
CREATE TABLE persons
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT persons_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE persons
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE persons TO expenser;

GRANT ALL ON TABLE persons TO postgres;


-- Table: card_owners
CREATE TABLE card_owners
(
    card_id uuid NOT NULL,
    owner_id uuid NOT NULL,
    CONSTRAINT card_owners_pkey PRIMARY KEY (card_id, owner_id),
    CONSTRAINT card FOREIGN KEY (card_id)
        REFERENCES cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT owner FOREIGN KEY (owner_id)
        REFERENCES persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE card_owners
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE card_owners TO expenser;

GRANT ALL ON TABLE card_owners TO postgres;



-- Table: categories
CREATE TABLE categories
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT categories_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE categories
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE categories TO expenser;

GRANT ALL ON TABLE categories TO postgres;



-- Table: monthly_calculations
CREATE TABLE monthly_calculations
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    year integer NOT NULL,
    month integer NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT monthly_calculations_pkey PRIMARY KEY (id),
    CONSTRAINT monthly_calculations_year_month_key UNIQUE (year, month)

)

TABLESPACE pg_default;

ALTER TABLE monthly_calculations
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE monthly_calculations TO expenser;

GRANT ALL ON TABLE monthly_calculations TO postgres;


-- Table: expenses
CREATE TABLE expenses
(
    monthly_calculation_id uuid NOT NULL,
    card_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT expenses_pkey PRIMARY KEY (monthly_calculation_id, card_id),
    CONSTRAINT expenses_card_id_fkey FOREIGN KEY (card_id)
        REFERENCES cards (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT expenses_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE expenses
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE expenses TO expenser;

GRANT ALL ON TABLE expenses TO postgres;


-- Table: incomes
CREATE TABLE incomes
(
    monthly_calculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT incomes_pkey PRIMARY KEY (monthly_calculation_id, person_id),
    CONSTRAINT incomes_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT incomes_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE incomes
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE incomes TO expenser;

GRANT ALL ON TABLE incomes TO postgres;


-- Table: personal_expense_corrections
CREATE TABLE personal_expense_corrections
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    monthly_calculation_id uuid NOT NULL,
    person_id uuid NOT NULL,
    category_id uuid NOT NULL,
    amount double precision NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT personal_expense_corrections_pkey PRIMARY KEY (id),
    CONSTRAINT personal_expense_corrections_category_id_fkey FOREIGN KEY (category_id)
        REFERENCES categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_monthly_calculation_id_fkey FOREIGN KEY (monthly_calculation_id)
        REFERENCES monthly_calculations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_expense_corrections_person_id_fkey FOREIGN KEY (person_id)
        REFERENCES persons (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE personal_expense_corrections
    OWNER to postgres;

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE personal_expense_corrections TO expenser;

GRANT ALL ON TABLE personal_expense_corrections TO postgres;