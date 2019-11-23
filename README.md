<h1>CSE241 Final Project</h1>
<h3>by Yuming Tian</h3>

<h2>Intro</h2>
This app contains 3 interfaces:
\tuser for customers
\tstaff for Hurts employees
\tadmin for Hurts managers

Compile with
```shell
/* user interface */
javac user.java
javac Database.java
jar cfmv user.jar user.txt user.class Database.class

/* staff interface */
javac staff.java
javac Database.java
jar cfmv staff.jar staff.txt staff.class Database.class

/* admin interface */
javac admin.java
javac Database.java
jar cfmv admin.jar admin.txt admin.class Database.class
```

Before you run, please make sure ojdbc7.jar is in the current directory

Run with
```shell
java -jar user.jar
java -jar staff.jar
java -jar admin.jar
```

<h2>Customers</h2>
First, you need to provide a valid customer id. Then you can edit your profile (name, address, driver license).
You can also create orders (book online) and estimate rental.

<h2>Employees</h2>
Employees can help customers to create accounts (insert into customers table), create orders for customers, 
and charge customers with their order information.

<h2>Managers</h2>
Managers are able to chnage the price of items, rental rate, edit user groups, manage viecles and rent centers.
