insert into constant (name, val, start_Date, end_Date)
values ('NON_TAXABLE_MINIMUM', 350, '2022-01-01', '2022-06-30');

insert into constant (name, val, start_Date, end_Date)
values ('NON_TAXABLE_MINIMUM', 350, '2022-07-01', null);

insert into constant (name, val, start_Date, end_date)
values ('NON_TAXABLE_LOWER', 500, '2022-01-01', null);

insert into constant (name, val, start_Date, end_date)
values ('NON_TAXABLE_UPPER', 1800, '2022-01-01', null);

insert into constant (name, val, start_Date, end_date)
values ('NON_TAXABLE_DEPENDANT', 250, '2022-01-01', null);

insert into constant (name, val, start_Date, end_date)
values ('INCOME_TAX', 20,  '2022-01-01', null);

insert into constant (name, val, start_Date, end_date)
values ('SOCIAL_TAX', 10.5, '2022-01-01', null);

-- add separate table later for income tax and maybe social tax?
-- over 20'004 to 78'100
--insert into constants (name, val, start_Date, end_date)
--values ('INCOME_TAX', 23, null, null);
--
-- over 78'100
--insert into constants (name, val, start_Date, end_date)
--values ('INCOME_TAX', 31, null, null);

