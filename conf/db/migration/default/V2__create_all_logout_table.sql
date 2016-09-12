drop table if EXISTS xuseralllogoutdate;

create table xuseralllogoutdate (
  id uuid primary key not null,
  authorid uuid not null REFERENCES xuser (id),
  createdat timestamp not null,
  xuserid uuid not null REFERENCES xuser (id),
  alllogoutdate timestamp not null
);
create index on xuseralllogoutdate (alllogoutdate);
create index on xuseralllogoutdate (createdat);

