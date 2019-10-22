create table t_transfer (
    id bigserial,
    date_time timestamp,
    funds numeric,
    source_account_id bigint,
    destination_account_id bigint
);