CREATE OR REPLACE PROCEDURE proc_zero_temp_accs
IS


	CURSOR account_cur IS
	SELECT account_no, account_type_code, account_balance		
	FROM account
	WHERE account_type_code = 'EX' OR account_type_code = 'RE'  ;
	
	account_rec account_cur%rowtype; 
	processed VARCHAR2(100) := 'Zero out';
		
BEGIN
	OPEN account_cur;
	LOOP
		FETCH account_cur INTO account_rec;
		EXIT WHEN account_cur%NOTFOUND;
			BEGIN

				IF account_rec.account_type_code = 'EX' AND account_rec.account_balance !=0
				THEN
					INSERT INTO new_transactions 
					VALUES (wkis_seq.NEXTVAL, SYSDATE, processed, 
					account_rec.account_no, 'C', account_rec.account_balance);
					
					INSERT INTO new_transactions 
					VALUES (wkis_seq.CURRVAL, SYSDATE, processed, 
					'5555', 'D', account_rec.account_balance);
				END IF;
				
				IF account_rec.account_type_code = 'RE' AND account_rec.account_balance !=0
				THEN
					INSERT INTO new_transactions 
					VALUES (wkis_seq.NEXTVAL, SYSDATE, processed, 
					account_rec.account_no, 'D', account_rec.account_balance);
					
					INSERT INTO new_transactions 
					VALUES (wkis_seq.CURRVAL, SYSDATE, processed, 
					'5555', 'C', account_rec.account_balance);
				END IF;
				
				EXCEPTION
					WHEN OTHERS THEN
						DBMS_OUTPUT.PUT_LINE (SQLERRM);
			END;
	END LOOP;
	CLOSE account_cur;
END;
/