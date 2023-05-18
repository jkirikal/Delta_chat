# OOP_ryhmatoo
Katariina, Artemi ja Johani rühmatöö aines "Objektorienteeritud programmeerimine"


Kuidas programmi jooksutada:
  1. Jooksuta "Sever" programmi
  2. Jooksuta "Client1", "Client2" ja "Client3" programme
  3. Kui kõik 3 programmi jooksevad, saab kliendi terminali sõnumeid kirjutada, mis jõuavad teise kliendi terminali ja vastupidi.
 

Database setup: create a database called "deltachat"
restore the backup file to your new database. The backup file can be found in this repo.
Execute the following commands to create new users and give permissions for the users to work on the database.
 
CREATE USER auth WITH PASSWORD 'deltachatauth';

CREATE USER messages WITH PASSWORD 'deltachatmessages';

GRANT CONNECT ON DATABASE deltachat TO
messages;

GRANT CONNECT ON DATABASE deltachat TO
auth;

GRANT USAGE ON SCHEMA public TO auth;

GRANT USAGE ON SCHEMA public TO messages;

GRANT all ON table users TO auth;

GRANT all
ON ALL TABLES IN SCHEMA public 
TO messages;

GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO auth;
