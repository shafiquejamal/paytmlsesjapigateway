create table xuserphonenumber (
  id          UUID PRIMARY KEY NOT NULL,
  xuserid     UUID             NOT NULL REFERENCES xuser (id),
  phonenumber VARCHAR(11)      NOT NULL,
  status      SMALLINT         NOT NULL,
  createdat   TIMESTAMP        NOT NULL
);
create index on xuserphonenumber (phonenumber);