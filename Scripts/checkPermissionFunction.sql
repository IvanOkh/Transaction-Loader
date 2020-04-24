CREATE OR REPLACE FUNCTION func_A2_permission
	RETURN CHAR
IS
	hasPermission CHAR(1);
	privilegeName VARCHAR2(20);
BEGIN
	SELECT privilege
	INTO privilegeName
	FROM USER_TAB_PRIVS
	WHERE privilege = 'EXECUTE'
	AND table_name = 'UTL_FILE';

	hasPermission := 'Y';
	RETURN hasPermission;
	
	EXCEPTION WHEN NO_DATA_FOUND THEN
		hasPermission := 'N';
		RETURN hasPermission;
END;
/