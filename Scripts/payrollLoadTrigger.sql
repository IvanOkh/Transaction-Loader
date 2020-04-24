CREATE OR REPLACE TRIGGER wkis_payroll_trg
	BEFORE INSERT ON payroll_load
	FOR EACH ROW
	
DECLARE	
	success CHAR(1) :='G';
	errors CHAR(1) := 'B';
	payable NUMBER := 2050;
    expense NUMBER := 4045;
	debit CHAR(1) :='D';
	credit CHAR(1) :='C';
	processed VARCHAR2(100) := 'Payroll processed';	

BEGIN	
	INSERT INTO new_transactions 
	VALUES (wkis_seq.NEXTVAL, :NEW.payroll_date, processed, 
				payable, credit, :NEW.amount);
					
	INSERT INTO new_transactions
	VALUES (wkis_seq.CURRVAL, :NEW.payroll_date, processed, 
				expense, debit, :NEW.amount);
				
	:NEW.status := success;
	
	EXCEPTION WHEN OTHERS THEN
		:NEW.status := errors;
END;
/			
