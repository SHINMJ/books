use mysql;

create user 'booksuser'@'%' identified by 'books!234';

grant all privileges on *.* to 'booksuser'@'%';

flush privileges;

CREATE SCHEMA books COLLATE = utf8_general_ci;