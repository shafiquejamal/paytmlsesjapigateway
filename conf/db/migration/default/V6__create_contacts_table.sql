drop table if EXISTS contact;

create table contact (
  id uuid primary key not null,
  xuserid uuid not null REFERENCES xuser (id),
  contactxuserid uuid not null REFERENCES xuser (id),
  createdat timestamp not null
);

drop table if EXISTS contactvisibility;

create table contactvisibility (
  id uuid primary key not null,
  contactid uuid not null REFERENCES contact (id),
  visibility smallint not null default 1,
  createdat timestamp not null
);
create index on contactvisibility (visibility);
create index on contactvisibility (createdat);
