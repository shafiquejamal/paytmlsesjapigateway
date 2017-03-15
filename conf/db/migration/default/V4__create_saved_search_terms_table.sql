DROP TABLE if EXISTS searchterm;

CREATE TABLE searchterm (
   id uuid PRIMARY KEY NOT NULL,
   createdat TIMESTAMP NOT NULL,
   xuserid uuid NOT NULL REFERENCES xuser (id),
   searchtext VARCHAR NOT NULL
);