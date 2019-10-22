alter table users
    add constraint pk_users primary key (id),
    add constraint unique_name unique (name),
    alter column "name" set not null;

alter table accounts
    add constraint pk_accounts primary key (id),
    add constraint fk_users_accounts_on_delete_cascade
        foreign key (user_id)
        references users (id)
        on delete cascade,
    alter column funds set default 0,
    alter column user_id set not null,
    alter column funds set not null;

alter table transfers
    add constraint pk_transfers primary key (id),
    add constraint fk_transfers_accounts_source_on_delete_cascade
        foreign key (source_account_id)
        references accounts (id)
        on delete cascade,
    add constraint fk_transfers_accounts_destination_on_delete_cascade
        foreign key (destination_account_id)
        references accounts (id)
        on delete cascade,
    add constraint valid_destination_account
        check (source_account_id <> destination_account_id),
    alter column transfer_date_time set not null,
    alter column funds set not null,
    alter column source_account_id set not null,
    alter column destination_account_id set not null;