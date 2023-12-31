create table if not exists USERS
(
    USER_ID  INTEGER GENERATED BY DEFAULT AS IDENTITY,
    EMAIL    CHARACTER VARYING(50) not null,
    LOGIN    CHARACTER VARYING(50) not null,
    NAME     CHARACTER VARYING(50) default 'noname',
    BIRTHDAY DATE,
    constraint "USERS_pk"
    primary key (USER_ID),
    constraint UC_Person UNIQUE (EMAIL,LOGIN )
    );

create table if not exists USER_FRIEND
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint "USER_FRIENDS_pk"
    primary key (USER_ID, FRIEND_ID),
    constraint "USER_FRIENDS_USERS_USER_ID_fk"
    foreign key (USER_ID) references USERS
    on delete cascade,
    constraint "USER_FRIENDS_USERS_USER_ID_fk2"
    foreign key (FRIEND_ID) references USERS
    on delete cascade
    );

create table if not exists MPA
(
    MPA_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY,
    MPA_NAME CHARACTER VARYING(50) not null,
    constraint "MPA_pk"
    primary key (MPA_ID)
    );

create table if not exists FILMS
(
    FILM_ID      INTEGER GENERATED BY DEFAULT AS IDENTITY,
    FILM_NAME    CHARACTER VARYING(50)  not null,
    DESCRIPTION  CHARACTER VARYING(250) not null,
    MPA_ID       INTEGER,
    RELEASE_DATE DATE,
    DURATION     INTEGER                not null,
    RATE         INTEGER default 0,
    constraint _PK
    primary key (FILM_ID),
    constraint "FILMS_MPA_MPA_ID_fk"
    foreign key (MPA_ID) references MPA,
    check ("DURATION" > 0)
    );

create table if not exists LIKE_FILM
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint "LIKE_FILM_pk"
    primary key (FILM_ID, USER_ID),
    constraint "LIKE_FILM_FILMS_FILM_ID_fk"
    foreign key (FILM_ID) references FILMS
    on delete cascade,
    constraint "LIKE_FILM_USERS_USER_ID_fk"
    foreign key (USER_ID) references USERS
    on delete cascade
    );

create table if not exists GENRE
(
    GENRE_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY,
    GENRE_NAME VARCHAR(50) not null,
    constraint "GENRE_pk"
    primary key (GENRE_ID)
    );

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    constraint "FILM_GENRE_pk"
    primary key (FILM_ID, GENRE_ID),
    constraint "FILM_GENRE_FILMS_FILM_ID_fk"
    foreign key (FILM_ID) references FILMS
    on delete cascade,
    constraint "FILM_GENRE_GENRE_GENRE_ID_fk"
    foreign key (GENRE_ID) references GENRE
    on delete cascade
    );