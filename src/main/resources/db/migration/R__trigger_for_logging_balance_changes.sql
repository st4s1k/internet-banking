drop trigger if exists balance_changes on t_account;
create trigger balance_changes
    after update or insert
    on t_account
    for each row execute
    procedure log_balance_changes();