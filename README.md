<h1>CSE241 Final Project</h1>
<h3>by Yuming Tian</h3>

<h2>Intro</h2>
This app contains 3 interfaces:
\tuser for customers
\tstaff for Hurts employees
\tadmin for Hurts managers
These three interfaces are all seperated files and need to be run seperately.

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
or simply
```shell
sh run.sh
```

Before you run, please make sure ojdbc7.jar is in the current directory

Run with
```shell
java -jar user.jar      /* customer interface */
java -jar staff.jar     /* Hurt employee interface */
java -jar admin.jar     /* Hurt manager interface */
```

<h2>Customers Interface</h2>
This interface is for Hurt's customers. It allows user to view or edit his or her personal info (full name, address, driver license), manage orders created by this account, and create new orders.

First, user needs to provide a valid customer id to verify their identity. If user doesn't have an account, he or she needs to find a hurt's emplopyee for help. User can only see info related to his or her own account (which is to say, info associate with the customer id provided).

In the main menu, you can either manage personal info or manage orders (include creating orders). See the help message for details.

In the personal info menu, user can view his or her info (name, address, driver license) and edit any field. User can also check all available discount code and user groups he or she enrolled.

In the order menu, user can view all existing orders (created by the current account), view add-on items associated with an order by entering a valid order_id, create an order (the interface would respond with the newly created order id and estimated rental), or check the rental of an order. One thing user should be awared of is that estimated rental might not equals to the real rental, since the real rental also depends on total miles, current gasoline price, drop-off location, etc, which are unknown until user returns the vehicle.

<h2>Employees Interface</h2>
This interface is for Hurt's employees at each rent center.

In the main menu, user can choose either to manage customer accounts or manage orders.

In the manage customers menu, user can create new customer accounts (interface would respond with generated customer_id), edit any existing accounts, and add an account to a user groups.

In the manage orders menu, user can either help a customer to create a new order or complete an order (when customer returns vehicle). Create new orders method is similar to that in customers interface. On the other hand, when returning vehicle, user should record info related to this order, such as total miles traveled, gasoline usage (for example, if customer return with a full tank, we view the gasoline usage as 0 and don't charge the customer), drop-off location, etc. Then, the application could calculate the rental with some extra information: user would enter the current gasoline price, whether the rent period is 'busy period', appropriate drop-off charge, etc. In real life, these can be done automatically (we can have APIs from gasoline company to track the gasoline price, a calendar to verify whether the rent period is among 'busy period' of a year, and a map to measure the distance between drop-off location and rent center if customer didn't return vehicle to the center where he or she rent it). We won't store the rental, since in real life, all data we need to calculate rental is either in our database already or could be obtained through third parties.

<h2>Managers Interface</h2>
This interface is for Hurt's market managers. User can add or edit customer groups, insurance plans, add-on items, vehicles, rent centers, and rental of different types of vehicle. See help message for details.
