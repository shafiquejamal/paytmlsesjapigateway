create table xuser (
  id uuid primary key not null,
  username varchar not null,
  email varchar not null,
  password varchar not null,
  isactive boolean not null ,
  created timestamp not null,
  parentid uuid,
  timezone VARCHAR(3) DEFAULT 'UTC');