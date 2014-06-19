CREATE SCHEMA HU;

set schema hu;

CREATE TABLE STAFF
(EMPNUM   VARCHAR(3) NOT NULL,
 EMPNAME  VARCHAR(20),
 GRADE    DECIMAL(4),
 CITY     VARCHAR(15));

CREATE TABLE PROJ
(PNUM     VARCHAR(3) NOT NULL,
 PNAME  VARCHAR(20),
 PTYPE    CHAR(6),
 BUDGET   DECIMAL(9),
 CITY     VARCHAR(15)) ;

INSERT INTO STAFF VALUES ('E1','Alice',12,'Deale');
INSERT INTO STAFF VALUES ('E2','Betty',10,'Vienna');
INSERT INTO STAFF VALUES ('E3','Carmen',13,'Vienna');
INSERT INTO STAFF VALUES ('E4','Don',12,'Deale');
INSERT INTO STAFF VALUES ('E5','Ed',13,'Akron');

INSERT INTO PROJ VALUES  ('P1','MXSS','Design',10000,'Deale');
INSERT INTO PROJ VALUES  ('P2','CALM','Code',30000,'Vienna');
INSERT INTO PROJ VALUES  ('P3','SDP','Test',30000,'Tampa');
INSERT INTO PROJ VALUES  ('P4','SDP','Design',20000,'Deale');
INSERT INTO PROJ VALUES  ('P5','IRM','Test',10000,'Vienna');
INSERT INTO PROJ VALUES  ('P6','PAYR','Design',50000,'Deale');


SELECT PNUM FROM PROJ WHERE PROJ.CITY = (SELECT STAFF.CITY FROM STAFF WHERE EMPNUM > 'E2' );