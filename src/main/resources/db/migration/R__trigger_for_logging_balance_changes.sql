create trigger balance_changes
    before update on t_account
    for each row execute
    procedure log_balance_changes();