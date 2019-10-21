create table transfers (
    id bigserial,
    transfer_date_time timestamp,
    funds numeric,
    source_account_id bigint,
    destination_account_id bigint
);