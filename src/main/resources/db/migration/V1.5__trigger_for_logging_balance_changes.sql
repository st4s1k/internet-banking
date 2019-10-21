create trigger balance_changes
    before update on accounts
    for each row execute
    procedure log_balance_changes();