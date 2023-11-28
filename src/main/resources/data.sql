merge into GENRE (GENRE_ID, GENRE_NAME)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

merge into MPA (MPA_ID, MPA_NAME)
values ( 1, 'G' ),
       ( 2, 'PG' ),
       ( 3, 'PG-13' ),
       ( 4, 'R' ),
       ( 5, 'NC-17' );

--merge into USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
-- (1, 'eax1@mail', 'login1', 'name1', '2023-09-08');

--merge into USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
   -- values (2, 'eax2@mail', 'login2', 'name2', '2023-09-08');

--merge into USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
    --values (3, 'eax3@mail', 'login3', 'name3', '2023-09-08');

--merge into USERS (USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY)
   -- values (4, 'eax4@mail', 'login4', 'name4', '2023-09-08');

--merge into USER_FRIEND (USER_ID, FRIEND_ID)
--values (1, 2 ),
      -- (1, 3),
      -- (1, 4);