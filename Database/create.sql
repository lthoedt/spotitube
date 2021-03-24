
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
    id          int             primary key AUTO_INCREMENT ,
    playlist_id varchar(34)     not null    ,
    user_id     int             not null    ,
    owner       BIT             not null
);

CREATE TABLE Playlists (
    id          varchar(34)     primary key ,
    name        varchar(30)     not null
);

/* ========================================= */
/* ---------- Tracks ----------------------- */
/* ========================================= */
CREATE TABLE TrackMappers (
    id                  int             primary key AUTO_INCREMENT ,
    track_id            varchar(34)     not null    ,
    playlist_id         varchar(34)     not null    ,
    offline_available   BIT             DEFAULT 0
);

CREATE TABLE Tracks (
    id                  varchar(34)     primary key ,
    performer           varchar(100)    not null    ,
    title               varchar(100)    not null    ,
    url                 varchar(500)    null        ,
    duration            int             DEFAULT 0   
);

CREATE TABLE Songs (
    id          int             primary key AUTO_INCREMENT ,
    track_id    varchar(34)     not null ,
    album_id    varchar(34)     not null
);

CREATE TABLE Albums (
    id          varchar(34)     primary key ,
    name        varchar(50)     not null
);

CREATE TABLE Videos (
    id                  int             primary key AUTO_INCREMENT ,
    track_id            varchar(34)     not null ,
    publication_date    DATE            DEFAULT (CURRENT_DATE)     ,
    description         text            null                       ,
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
INSERT INTO Users (id, username, password, token) VALUES (1, "henk", "henk", "1425-2565-5487");

-- Playlist
INSERT INTO Playlists ( id, name ) VALUES ( "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0", "SRV" );
INSERT INTO PlaylistMappers ( playlist_id, user_id, owner ) VALUES ( "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0", 1, true );

-- Track 1
INSERT INTO Tracks ( id, performer, title, url, duration ) VALUES ( "LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u", "Stevie Ray Vaughan And Double Trouble", "Texas Flood (from Live at the El Mocambo", "https://music.youtube.com/watch?v=KC5H9P4F5Uk&feature=share", 571 );
INSERT INTO Videos ( track_id, playcount, publication_date ) VALUES ( "LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u", 7585665, '1982-6-23' );

INSERT INTO TrackMappers ( id, track_id, playlist_id, offline_available ) VALUES ( 1, "LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u", "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0", true );
--

-- Track 2
INSERT INTO Tracks ( id, performer, title, url, duration ) VALUES ( "N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyBK", "Stevie Ray Vaughan And Double Trouble", "Lenny", "https://music.youtube.com/watch?v=dfdJ1rh4zNc&feature=share", 298 );
INSERT INTO Albums ( id, name ) VALUES (1, "Texas Flood");
INSERT INTO Songs ( track_id, album_id ) VALUES ( "N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyBK", 1 );

INSERT INTO TrackMappers ( id, track_id, playlist_id, offline_available ) VALUES ( 2, "N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyBK", "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0", false );
--

-- Track 3
INSERT INTO Tracks ( id, performer, title, url, duration ) VALUES ( "PkkDX_9Ri5VmBdvGaqxAJz-u6KaOfiUdje", "Stevie Ray Vaughan And Double Trouble", "Collins Shuffle (Live at Montreux Casino, Montreux, Switzerland - July 1982)", "https://music.youtube.com/watch?v=4b7Gf6md1GE&feature=share", 291 );
INSERT INTO Albums ( id, name ) VALUES ("1l9veJE1ZBoZBPgzGOuCdqPEI5mNyqpIka", "Live At Montreux 1982 & 1985 • 2000");
INSERT INTO Songs ( track_id, album_id ) VALUES ( "PkkDX_9Ri5VmBdvGaqxAJz-u6KaOfiUdje", "1l9veJE1ZBoZBPgzGOuCdqPEI5mNyqpIka" )