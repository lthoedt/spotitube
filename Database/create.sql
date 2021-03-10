
DROP DATABASE IF EXISTS  spotitube;

CREATE DATABASE spotitube;

use spotitube;


/* ========================================= */
/* ---------- Users ------------------------ */
/* ========================================= */
CREATE TABLE Users (
    id          int             primary key AUTO_INCREMENT,
    username    varchar(30)     not null    ,
    password    varchar(50)     not null    ,
    token       varchar(20)     not null
);

/* ========================================= */
/* ---------- Playlists -------------------- */
/* ========================================= */
CREATE TABLE PlaylistMappers (
    id          int     primary key AUTO_INCREMENT ,
    playlist_id int     not null    ,
    user_id     int     not null    ,
    owner       BIT     not null
);

CREATE TABLE Playlists (
    id          int             primary key AUTO_INCREMENT,
    name        varchar(30)     not null
);

/* ========================================= */
/* ---------- Tracks ----------------------- */
/* ========================================= */
CREATE TABLE TrackMappers (
    id                  int     primary key AUTO_INCREMENT ,
    track_id            int     not null    ,
    playlist_id         int     not null    ,
    offline_available   BIT     DEFAULT 0
);

CREATE TABLE Tracks (
    id                  int             primary key AUTO_INCREMENT ,
    performer           varchar(100)    not null    ,
    title               varchar(100)    not null    ,
    url                 varchar(500)    null        ,
    duration            int             DEFAULT 0   
);

CREATE TABLE Songs (
    id          int     primary key AUTO_INCREMENT ,
    track_id    int     not null ,
    album_id    int     not null
);

CREATE TABLE Albums (
    id          int             primary key AUTO_INCREMENT ,
    name        varchar(50)     not null
);

CREATE TABLE Videos (
    id                  int             primary key AUTO_INCREMENT ,
    track_id            int             not null ,
    publication_date    DATE            DEFAULT (CURRENT_DATE)  ,
    description         text            null                    ,
    playcount           int             DEFAULT 0
);

/* ========================================= */
/* ========================================= */
/* ---------- ALTERS ----------------------- */
/* ========================================= */
/* ========================================= */

/* ========================================= */
/* ---------- Playlists -------------------- */
/* ========================================= */
ALTER TABLE PlaylistMappers
    ADD CONSTRAINT FK_playlist_playlist_id   FOREIGN KEY (playlist_id)   REFERENCES Playlists(id)     ON UPDATE CASCADE ON DELETE CASCADE ,
    ADD CONSTRAINT FK_user_id                FOREIGN KEY (user_id)       REFERENCES Users(id)         ON UPDATE CASCADE ON DELETE CASCADE
;

/* ========================================= */
/* ---------- Tracks ----------------------- */
/* ========================================= */
ALTER TABLE TrackMappers
    ADD CONSTRAINT FK_track_id               FOREIGN KEY (track_id)       REFERENCES Tracks(id)       ON UPDATE CASCADE ON DELETE CASCADE ,
    ADD CONSTRAINT FK_track_playlist_id      FOREIGN KEY (playlist_id)    REFERENCES Playlists(id)    ON UPDATE CASCADE ON DELETE CASCADE 
;

-- ALTER TABLE Tracks
--     ADD CONSTRAINT FK_specialization_songs   FOREIGN KEY (specialization) REFERENCES Songs(id)        ON UPDATE CASCADE ON DELETE CASCADE ,
--     ADD CONSTRAINT FK_specialization_videos  FOREIGN KEY (specialization) REFERENCES Videos(id)       ON UPDATE CASCADE ON DELETE CASCADE 
-- ;

ALTER TABLE Songs
    ADD CONSTRAINT FK_specialization_songs FOREIGN KEY (track_id) REFERENCES Tracks(id)                  ON UPDATE CASCADE ON DELETE CASCADE
;

ALTER TABLE Videos
    ADD CONSTRAINT FK_specialization_videos FOREIGN KEY (track_id) REFERENCES Tracks(id)                 ON UPDATE CASCADE ON DELETE CASCADE
;
/* ========================================= */
/* ---------- Songs ------------------------ */
/* ========================================= */
ALTER TABLE Songs
    ADD CONSTRAINT FK_album_id               FOREIGN KEY (album_id)       REFERENCES Albums(id)       ON UPDATE CASCADE ON DELETE CASCADE
;

/* ========================================= */
/* ========================================= */
/* ---------- INSERTS ---------------------- */
/* ========================================= */
/* ========================================= */
INSERT INTO Users (username, password, token) VALUES ("henk", "henk", "1425-2565-5487");

INSERT INTO Tracks ( id, performer, title, url, duration ) VALUES ( 1, "Stevie Ray Vaughan And Double Trouble", "Texas Flood (from Live at the El Mocambo", "https://music.youtube.com/watch?v=KC5H9P4F5Uk&feature=share", 571 );
INSERT INTO Videos ( track_id, playcount ) VALUES ( 1, 7585665 );

INSERT INTO Tracks ( id, performer, title, url, duration ) VALUES ( 2, "Stevie Ray Vaughan And Double Trouble", "Lenny", "https://music.youtube.com/watch?v=dfdJ1rh4zNc&feature=share", 298 );
INSERT INTO Albums ( id, name ) VALUES (1, "Texas Flood");
INSERT INTO Songs ( track_id, album_id ) VALUES ( 2, 1 );