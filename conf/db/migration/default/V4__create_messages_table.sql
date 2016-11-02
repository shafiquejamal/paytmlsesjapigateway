drop table if EXISTS chatmessage;

create table chatmessage (
  id uuid primary key not null,
  fromxuserid uuid not null REFERENCES xuser (id),
  toxuserid uuid not null REFERENCES xuser (id),
  messagetext varchar(1000) not null,
  createdat timestamp not null,
  sentat timestamp not null
);
create index on chatmessage (sentat);
create index on chatmessage (createdat);

drop table if EXISTS chatmessagevisibility;

create table chatmessagevisibility (
  id uuid primary key not null,
  chatmessageid uuid not null REFERENCES chatmessage (id),
  visibility smallint not null default 3,
  createdat timestamp not null
);
create index on chatmessagevisibility (visibility);

