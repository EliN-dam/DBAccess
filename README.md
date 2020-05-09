# DBAccess

A NetBeans proyect with an examples of connection to different DBMS


Databases
---------
This is the list of databases od each DBMS needed for this project.

### MySQL - Sakila
Your can dowsload the database in the following link:
https://dev.mysql.com/doc/sakila/en/sakila-installation.html

### SQLServer - Empresas Informaticas
You can found the script to create this database in the folder *db/EmpresasInformaticasCrear.sql*.

### PostgreSQL - Chinaook
Your can dowsload the database in the following link:
https://github.com/lerocha/chinook-database

### SQLite - Northwind
The dababase is included in the project, in the folder *db/Northwind.db*, you can also download the database in the following link bellow:
https://github.com/jpwhite3/northwind-SQLite3


Configuration
-------------
For the connection to the databases, there are in the *config* folder the files with the required parameters for the configuration of access to the different database management systems.

You must fill in the following parameters:
* **url** - The address of the database server.
* **port** - The database server port, if you use the default port you can leave this value blank.
* **db** - Name of the database to which the connection will be made..
* **login** - User name (remember to configure access permissions
* **password** - User password.
