# HMF OncoAct Database

## Create Database

The following commands will create a user with write permissions, a user with read permissions and a database.  

```
mysql> ​CREATE USER 'writer'@'localhost' IDENTIFIED WITH mysql_native_password BY 'writer_password'; 
Query OK, 0 rows affected (0.00 sec)
mysql> ​CREATE USER 'reader'@'localhost' IDENTIFIED WITH mysql_native_password BY 'reader_password'; 
Query OK, 0 rows affected (0.00 sec)
mysql> CREATE DATABASE patientdb; 
Query OK, 1 row affected (0.00 sec)
mysql> GRANT ALL on patientdb.* TO 'writer'@'localhost'; 
Query OK, 0 rows affected (0.00 sec)
mysql> GRANT SELECT on patientdb.* TO 'reader'@'localhost'; 
Query OK, 0 rows affected (0.00 sec)
```

## Create Tables
If creating a database from scratch, execute the [database.sql](/src/main/resources/database.sql) script from the command with the following. 
Note that you will be prompted for a password:

```
mysql -u writer -p < database.sql
```

## Data loaders

Data will be deleted before new records are inserted. The loaders do not support updating records.
