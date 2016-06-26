create table xuser (
  id uuid primary key not null,
  authorid uuid not null,
  createdat timestamp not null
);

create table xuserstatus (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  status BOOLEAN --smallint not null check (status >= 0)
);
--create index on xuserstatus (status);

create table xuseremail (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  email varchar(254) not null
);
--create index on xuseremail (email);

create table xuserusername (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  username varchar(100) not null
);
--create index on xuserusername (lower(username));

create table xuserpassword (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  password varchar(100) not null
);
-- create index