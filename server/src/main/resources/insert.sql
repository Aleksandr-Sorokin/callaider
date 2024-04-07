
INSERT INTO authors (id, last_name, name) values (1, 'Толстой', 'Лев');
INSERT INTO authors (id, last_name, name) values (2, 'Толстой', 'Алексей');
INSERT INTO authors (id, last_name, name) values (3, 'Достоевский', 'Федор');
INSERT INTO authors (id, last_name, name) values (4, 'Пушкин', 'Александр');

INSERT INTO books (id, name) values (1, 'Война и мир');
INSERT INTO books (id, name) values (2, 'Анна Каренина');
INSERT INTO books (id, name) values (3, 'Воскресение');
INSERT INTO books (id, name) values (4, 'Аэлита');
INSERT INTO books (id, name) values (5, 'Хождение по мукам');
INSERT INTO books (id, name) values (6, 'Преступление и наказание');
INSERT INTO books (id, name) values (7, 'Братья Карамазовы');
INSERT INTO books (id, name) values (8, 'Идиот');
INSERT INTO books (id, name) values (9, 'Руслан и Людмила');
INSERT INTO books (id, name) values (10, 'Евгений Онегин');
INSERT INTO books (id, name) values (11, 'Сказка о рыбаке и рыбке');

INSERT INTO book_author (book_id, author_id) values (1, 1);
INSERT INTO book_author (book_id, author_id) values (2, 1);
INSERT INTO book_author (book_id, author_id) values (3, 1);
INSERT INTO book_author (book_id, author_id) values (4, 2);
INSERT INTO book_author (book_id, author_id) values (5, 2);
INSERT INTO book_author (book_id, author_id) values (6, 3);
INSERT INTO book_author (book_id, author_id) values (7, 3);
INSERT INTO book_author (book_id, author_id) values (8, 3);
INSERT INTO book_author (book_id, author_id) values (9, 4);
INSERT INTO book_author (book_id, author_id) values (10, 4);
INSERT INTO book_author (book_id, author_id) values (11, 4);

INSERT INTO users (login, birthdate, first_name, last_name) values ('ivan@mail.ru', '2004-10-19 00:00:00', 'Иван', 'Иванов');
INSERT INTO users (login, birthdate, first_name, last_name) values ('maksim@mail.ru', '2000-11-01 00:00:00', 'Максим', 'Максимов');
INSERT INTO users (login, birthdate, first_name, last_name) values ('kirill@mail.ru', '2001-07-30 00:00:00', 'Кирилл', 'Кириллов');
INSERT INTO users (login, birthdate, first_name, last_name) values ('petr@mail.ru', '2008-09-25 00:00:00', 'Петр', 'Петров');

INSERT INTO user_books (user_login, book_id) values ('ivan@mail.ru', 1);
INSERT INTO user_books (user_login, book_id) values ('ivan@mail.ru', 2);
INSERT INTO user_books (user_login, book_id) values ('ivan@mail.ru', 11);
INSERT INTO user_books (user_login, book_id) values ('maksim@mail.ru', 1);
INSERT INTO user_books (user_login, book_id) values ('maksim@mail.ru', 3);
INSERT INTO user_books (user_login, book_id) values ('kirill@mail.ru', 4);
INSERT INTO user_books (user_login, book_id) values ('kirill@mail.ru', 5);
INSERT INTO user_books (user_login, book_id) values ('kirill@mail.ru', 7);
INSERT INTO user_books (user_login, book_id) values ('petr@mail.ru', 8);
INSERT INTO user_books (user_login, book_id) values ('petr@mail.ru', 9);
INSERT INTO user_books (user_login, book_id) values ('petr@mail.ru', 10);
INSERT INTO user_books (user_login, book_id) values ('petr@mail.ru', 11);

SELECT * from users u
join user_books ub on u.login = ub.user_login where login = 'ivan@mail.ru';
