CREATE TABLE pracownicy (
    id_pracownika NUMBER PRIMARY KEY,
    imie          VARCHAR2(50),
    nazwisko      VARCHAR2(50),
    stanowisko    VARCHAR2(50),
    pensja        NUMBER
);

CREATE TABLE klienci (
    id_klienta NUMBER PRIMARY KEY,
    imie       VARCHAR2(50),
    nazwisko   VARCHAR2(50),
    telefon    VARCHAR2(20),
    adres      VARCHAR2(100)
);

CREATE TABLE uslugi (
    id_uslugi NUMBER PRIMARY KEY,
    nazwa     VARCHAR2(100),
    cena      NUMBER
);

CREATE TABLE pogrzeby (
    id_pogrzebu    NUMBER PRIMARY KEY,
    id_klienta     NUMBER REFERENCES klienci(id_klienta),
    id_pracownika  NUMBER REFERENCES pracownicy(id_pracownika),
    id_uslugi      NUMBER REFERENCES uslugi(id_uslugi),
    data_pogrzebu  DATE,
    status         VARCHAR2(20)
);

CREATE SEQUENCE pogrzeby_seq START WITH 1;

BEGIN
    -- PRACOWNICY
    INSERT INTO pracownicy VALUES (1,'Jan','Kowalski','Kierownik',6000);
    INSERT INTO pracownicy VALUES (2,'Anna','Nowak','Doradca',3500);
    INSERT INTO pracownicy VALUES (3,'Piotr','Mazur','Kierowca',3200);
    INSERT INTO pracownicy VALUES (4,'Ewa','Lis','Doradca',3400);
    INSERT INTO pracownicy VALUES (5,'Tomasz','Baran','Technik',3300);
    INSERT INTO pracownicy VALUES (6,'Katarzyna','Wójcik','Doradca',3600);
    INSERT INTO pracownicy VALUES (7,'Marek','Duda','Kierowca',3100);
    INSERT INTO pracownicy VALUES (8,'Paulina','Król','Recepcja',3000);
    INSERT INTO pracownicy VALUES (9,'Adam','Zając','Technik',3400);
    INSERT INTO pracownicy VALUES (10,'Monika','Pawlak','Doradca',3700);

    -- KLIENCI
    INSERT INTO klienci VALUES (1,'Maria','Wiśniewska','111111111','Warszawa');
    INSERT INTO klienci VALUES (2,'Jan','Kaczmarek','222222222','Kraków');
    INSERT INTO klienci VALUES (3,'Piotr','Lewandowski','333333333','Gdańsk');
    INSERT INTO klienci VALUES (4,'Anna','Dąbrowska','444444444','Łódź');
    INSERT INTO klienci VALUES (5,'Tomasz','Zieliński','555555555','Poznań');
    INSERT INTO klienci VALUES (6,'Katarzyna','Woźniak','666666666','Wrocław');
    INSERT INTO klienci VALUES (7,'Michał','Kamiński','777777777','Szczecin');
    INSERT INTO klienci VALUES (8,'Agnieszka','Kozłowska','888888888','Lublin');
    INSERT INTO klienci VALUES (9,'Paweł','Jankowski','999999999','Radom');
    INSERT INTO klienci VALUES (10,'Ewa','Piotrowska','101010101','Opole');

    -- USLUGI
    INSERT INTO uslugi VALUES (1,'Pogrzeb tradycyjny',4000);
    INSERT INTO uslugi VALUES (2,'Pogrzeb kremacyjny',3500);
    INSERT INTO uslugi VALUES (3,'Transport zwłok',1500);
    INSERT INTO uslugi VALUES (4,'Oprawa muzyczna',800);
    INSERT INTO uslugi VALUES (5,'Kwiaty',600);
    INSERT INTO uslugi VALUES (6,'Nagrobek',5000);
    INSERT INTO uslugi VALUES (7,'Ceremonia świecka',2000);
    INSERT INTO uslugi VALUES (8,'Ceremonia religijna',2500);
    INSERT INTO uslugi VALUES (9,'Sprzątanie grobu',400);
    INSERT INTO uslugi VALUES (10,'Klepsydry',300);

    -- POGRZEBY
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,1,2,1,SYSDATE+5,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,2,3,2,SYSDATE+3,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,3,4,3,SYSDATE+10,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,4,5,4,SYSDATE+7,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,5,6,5,SYSDATE+1,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,6,7,6,SYSDATE+12,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,7,8,7,SYSDATE+9,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,8,9,8,SYSDATE+4,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,9,10,9,SYSDATE+6,'planowany');
    INSERT INTO pogrzeby VALUES (pogrzeby_seq.NEXTVAL,10,1,10,SYSDATE+8,'planowany');
    
    COMMIT;
END;
/

CREATE OR REPLACE PACKAGE slodki_sen_pkg AS
    
    FUNCTION cena_pogrzebu(p_id NUMBER) RETURN NUMBER;
    
   
    PROCEDURE dodaj_pogrzeb(
        p_klient NUMBER,
        p_pracownik NUMBER,
        p_usluga NUMBER,
        p_data DATE
    );
END slodki_sen_pkg;
/

CREATE OR REPLACE PACKAGE BODY slodki_sen_pkg AS

    FUNCTION cena_pogrzebu(p_id NUMBER) RETURN NUMBER IS
        v_cena NUMBER;
    BEGIN
        SELECT u.cena INTO v_cena
        FROM pogrzeby p JOIN uslugi u ON p.id_uslugi = u.id_uslugi
        WHERE p.id_pogrzebu = p_id;
        
        RETURN v_cena;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'Błąd: Nie znaleziono pogrzebu o ID: ' || p_id);
    END cena_pogrzebu;

    PROCEDURE dodaj_pogrzeb(
        p_klient NUMBER,
        p_pracownik NUMBER,
        p_usluga NUMBER,
        p_data DATE
    ) IS
        ex_data_w_przeszlosci EXCEPTION;
    BEGIN
        IF p_data < TRUNC(SYSDATE) THEN
            RAISE ex_data_w_przeszlosci;
        END IF;

        INSERT INTO pogrzeby (id_pogrzebu, id_klienta, id_pracownika, id_uslugi, data_pogrzebu, status)
        VALUES (pogrzeby_seq.NEXTVAL, p_klient, p_pracownik, p_usluga, p_data, 'planowany');

    EXCEPTION
        WHEN ex_data_w_przeszlosci THEN
            DBMS_OUTPUT.PUT_LINE('Błąd użytkownika: Próba zaplanowania pogrzebu w przeszłości!');
    END dodaj_pogrzeb;
    
END slodki_sen_pkg;
/

DECLARE
    TYPE ceny_dodatkowe_typ IS VARRAY(5) OF NUMBER;
    v_dodatki ceny_dodatkowe_typ := ceny_dodatkowe_typ(150.50, 200.00, 50.00);
    v_suma NUMBER := 0;
BEGIN
    FOR i IN 1..v_dodatki.COUNT LOOP
        v_suma := v_suma + v_dodatki(i);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Suma kosztów dodatkowych z kolekcji: ' || v_suma || ' PLN');
END;
/

CREATE OR REPLACE TRIGGER trg_pogrzeb_audit
BEFORE INSERT ON pogrzeby
FOR EACH ROW
BEGIN
    IF :NEW.status IS NULL THEN
        :NEW.status := 'nowy';
    END IF;
    
    IF :NEW.data_pogrzebu < SYSDATE - 30 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Data pogrzebu jest zbyt odległa w przeszłości.');
    END IF;
END;
/

DECLARE
    v_sql_select VARCHAR2(200);
    v_sql_create VARCHAR2(200);
    v_nazwisko   VARCHAR2(100);
    v_count      NUMBER;
BEGIN
    
    v_sql_select := 'SELECT nazwisko FROM pracownicy WHERE id_pracownika = :id';
    EXECUTE IMMEDIATE v_sql_select INTO v_nazwisko USING 2;
    DBMS_OUTPUT.PUT_LINE('Dynamiczny SQL (Pracownik 2): ' || v_nazwisko);

    SELECT count(*) INTO v_count FROM user_tables WHERE table_name = 'LOGI_SYSTEMOWE';
    
    IF v_count = 0 THEN
        v_sql_create := 'CREATE TABLE logi_systemowe (id NUMBER GENERATED ALWAYS AS IDENTITY, info VARCHAR2(200), data_logu DATE)';
        EXECUTE IMMEDIATE v_sql_create;
        DBMS_OUTPUT.PUT_LINE('Dynamiczny SQL: Utworzono tabelę logi_systemowe.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Dynamiczny SQL: Tabela logi_systemowe już istnieje.');
    END IF;
END;
/