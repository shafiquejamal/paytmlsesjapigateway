drop table if EXISTS xuseriatsingleusetoken;

create table xuseriatsingleusetoken (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  iat timestamp not null
);
create index on xuseriatsingleusetoken (iat);
create index on xuseriatsingleusetoken (createdat);