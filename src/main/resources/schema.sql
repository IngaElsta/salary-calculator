create table employee (
    id bigint not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    base_Salary decimal(10, 2) not null,
    dependants int,
    use_Non_Taxable_Minimum bit,
    primary key (id)
);

create table constant (
    id bigint not null AUTO_INCREMENT,
    name varchar(255) not null,
    val decimal(10, 4) not null,
    start_Date date not null,
    end_Date date,
    primary key (id),
    constraint nameDate unique (name, start_Date)
);

