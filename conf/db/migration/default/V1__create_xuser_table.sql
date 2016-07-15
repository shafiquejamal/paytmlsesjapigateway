drop table if EXISTS xuserpassword;
drop table if EXISTS xuserusername;
drop table if EXISTS xuseremail;
drop table if EXISTS xuserstatus;
drop table if EXISTS xuser;

create table xuser (
  id uuid primary key not null,
  authorid uuid not null,
  createdat timestamp not null
);
create index on xuser (createdat);

create table xuserstatus (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  status smallint not null check (status >= 0)
);
create index on xuserstatus (status);
create index on xuserstatus (createdat);

create table xuseremail (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  email varchar(254) not null
);
create index on xuseremail (email);
create index on xuseremail (createdat);

create table xuserusername (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  username varchar(100) not null
);
create index on xuserusername (lower(username));
create index on xuserusername (createdat);

create table xuserpassword (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  password varchar(100) not null
);
create index on xuserpassword (createdat);