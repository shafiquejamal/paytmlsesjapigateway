drop table if EXISTS chatmessagevisibility;

drop table if EXISTS chatmessagesendervisibility;

create table chatmessagesendervisibility (
  id uuid primary key not null,
  chatmessageid uuid not null REFERENCES chatmessage (id),
  visibility smallint not null default 1,
  createdat timestamp not null
);
create index on chatmessagesendervisibility (visibility);

drop table if EXISTS chatmessagereceivervisibility;

create table chatmessagereceivervisibility (
  id uuid primary key not null,
  chatmessageid uuid not null REFERENCES chatmessage (id),
  visibility smallint not null default 1,
  createdat timestamp not null
);
create index on chatmessagereceivervisibility (visibility);