create table employee (
    id bigint not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    baseSalary double,
    dependents int,
    useNonTaxableMinimum bit,
    primary key (id)
);

create table constant (
    name varchar(255) not null,
    val double not null,
    starDate date,
    endDate date
);

