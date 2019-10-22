alter table t_user
    add constraint t_pk_user primary key (id),
    add constraint unique_name unique (name),
    alter column name set not null;

alter table t_account
    add constraint t_pk_account primary key (id),
    add constraint fk_user_account_on_delete_cascade
        foreign key (user_id)
        references t_user (id)
        on delete cascade,
    alter column user_id set not null,
    alter column funds set default 0,
    alter column funds set not null;

alter table t_transfer
    add constraint t_pk_transfer primary key (id),
    add constraint fk_source_account
        foreign key (source_account_id)
        references t_account (id)
        on delete cascade,
    add constraint fk_destination_account
        foreign key (destination_account_id)
        references t_account (id)
        on delete cascade,
    add constraint valid_destination_account
        check (source_account_id <> destination_account_id),
    alter column date_time set not null,
    alter column funds set not null,
    alter column source_account_id set not null,
    alter column destination_account_id set not null;

alter table t_account_history
    add constraint t_pk_account_history primary key (id),
    add constraint fk_account
        foreign key (account_id)
        references t_account (id)
        on delete cascade,
    alter column funds set not null,
    alter column date_time set not null;