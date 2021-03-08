
DROP DATABASE IF EXISTS  Spotitube;

CREATE DATABASE Spotitube;

use Spotitube;


/* ========================================= */
/* ---------- Users ------------------------ */
/* ========================================= */
CREATE TABLE Users (
    id          int             primary key ,
    username    varchar(30)     not null    ,
    password    varchar(50)     not null    
);

/* ========================================= */
/* ---------- Playlists -------------------- */
/* ========================================= */
CREATE TABLE PlaylistMappers (
    id          int     primary key ,
    playlist_id int     not null    ,
    user_id     int     not null    ,
    owner       BIT     not null
);

CREATE TABLE Playlists (
    id          int             primary key,
    name        varchar(30)     not null
);

/* ========================================= */
/* ---------- Tracks ----------------------- */
/* ========================================= */
CREATE TABLE TrackMappers (
    id                  int     primary key ,
    track_id            int     not null    ,
    playlist_id         int     not null    ,
    offline_available   BIT     DEFAULT 0
);

CREATE TABLE Tracks (
    id                  int             primary key ,
    performer           varchar(30)     not null    ,
    title               varchar(30)     not null    ,
    url                 varchar(500)    null        ,
    duration            int             DEFAULT 0   ,
    specialization      int             not null
);

CREATE TABLE Songs (
    id          int     primary key ,
    album_id    int     not null
);

CREATE TABLE Albums (
    id          int             primary key ,
    name        varchar(50)     not null
);

CREATE TABLE Videos (
    id                  int             primary key             ,
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

ALTER TABLE Tracks
    ADD CONSTRAINT FK_specialization_songs   FOREIGN KEY (specialization) REFERENCES Songs(id)        ON UPDATE CASCADE ON DELETE CASCADE ,
    ADD CONSTRAINT FK_specialization_videos  FOREIGN KEY (specialization) REFERENCES Videos(id)       ON UPDATE CASCADE ON DELETE CASCADE 
;

/* ========================================= */
/* ---------- Songs ------------------------ */
/* ========================================= */
ALTER TABLE Songs
    ADD CONSTRAINT FK_album_id               FOREIGN KEY (album_id)       REFERENCES Albums(id)       ON UPDATE CASCADE ON DELETE CASCADE
;