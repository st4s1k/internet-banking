create table t_account_history (
    id bigserial,
    date_time timestamp,
    account_id bigint,
    funds numeric
);