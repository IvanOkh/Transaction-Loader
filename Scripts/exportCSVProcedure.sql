CREATE OR REPLACE PROCEDURE proc_exportCSV
	(alias IN VARCHAR2, fileName IN VARCHAR2)
AS
CURSOR cur_tran IS
	SELECT * FROM new_transactions;
	v_file  UTL_FILE.FILE_TYPE;
BEGIN
	v_file := UTL_FILE.FOPEN(alias, fileName, 'w');
	
	FOR rec_tran IN cur_tran LOOP
		UTL_FILE.PUT_LINE(v_file, rec_tran.transaction_no ||','|| rec_tran.transaction_date ||','|| 
							rec_tran.description ||','|| rec_tran.account_no ||','|| 
							rec_tran.transaction_type ||','|| rec_tran.transaction_amount);
	END LOOP;
	UTL_FILE.FCLOSE(v_file);
	
END proc_exportCSV;
/