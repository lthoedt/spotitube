DROP DATABASE spotitube IF EXISTS;
CREATE DATABASE spotitube;
:use spotitube;

CREATE (user:Users { username: "henk", password: "henk", token: "1425-2565-5487" });

MATCH ( user:Users )
WHERE user.token = "1425-2565-5487"
CREATE (user) -[:owns]-> ( pl:Playlists { id: "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0", name: "SRV" } );

// -- video
MATCH ( pl:Playlists )
WHERE pl.id = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0"
CREATE ( t:Tracks { id: "LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u", performer: "Stevie Ray Vaughan And Double Trouble", title: "Texas Flood (from Live at the El Mocambo", url: "https://music.youtube.com/watch?v=KC5H9P4F5Uk&feature=share", duration: 571, playcount: 7585665, description: "", publication_date: '1982-6-23' } ) -[:in {offline_available: false}]-> (pl);

// -- song
MATCH ( pl:Playlists )
WHERE pl.id = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0"
CREATE ( t:Tracks { id: "N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyBK", performer: "Stevie Ray Vaughan And Double Trouble", title: "Lenny", url: "https://music.youtube.com/watch?v=dfdJ1rh4zNc&feature=share", duration: 298, playcount: null, description: "", publication_date: null } ) -[:in {offline_available: true}]-> (pl);

// -- album
MATCH (t:Tracks)
WHERE t.id="N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyBK"
CREATE ( album:Albums { id: "N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyAS", name: "Texas Flood" } ) -[:contains]-> (t);

// -- song
MATCH (album:Albums)
WHERE album.id="N1wdUywfFlbKWjBB6ayDJfv_nCIaUcOyAS"
CREATE (album) -[:contains]-> ( t:Tracks { id: "PkkDX_9Ri5VmBdvGaqxAJz-u6KaOfiUdje", performer: "Stevie Ray Vaughan And Double Trouble", title: "Collins Shuffle (Live at Montreux Casino, Montreux, Switzerland - July 1982)", url: "https://music.youtube.com/watch?v=4b7Gf6md1GE&feature=share", duration: 291, playcount: null, description: "", publication_date: null } );
