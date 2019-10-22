create or replace function log_balance_changes()
    returns trigger as
$body$
begin
   if new.funds <> old.funds then
       insert into t_account_history(date_time, account_id, funds)
       values(now(), new.id, new.funds);
   end if;
   return new;
end;
$body$

-- Source: https://stackoverflow.com/questions/39607334/postgres-trigger-creation-error-no-language-specified-sql-state-42p13
-- Says the function is implemented in the plpgsql language
-- VOLATILE says the function has side effects
LANGUAGE plpgsql VOLATILE
 -- Estimated execution cost of the function
COST 100;