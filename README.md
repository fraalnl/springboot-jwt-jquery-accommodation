# Getting Started


### Create a MySQL Schema:
Name the schema `rooms_db` in your MySQL database.

### Update Database Credentials:
In `application-mysql.yml`, update the username and password to match your MySQL credentials.

### Initialize Predefined Data for Testing:
For the initial run, set `spring.sql.init.mode` to `always` in `application.yml` to load predefined data from SQL scripts.
After the first run, change it back to `never` to prevent reloading the data on subsequent application starts.

### URL
`localhost:8082`

