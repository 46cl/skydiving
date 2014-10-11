CREATE TABLE skyblogs (
    id SERIAL,
    host varchar
);

ALTER TABLE skyblogs ADD CONSTRAINT unique_host UNIQUE (host);

CREATE TABLE quotes (
    id SERIAL,
    quote varchar,
    author varchar
);

ALTER TABLE quotes ADD CONSTRAINT unique_quote UNIQUE (quote);