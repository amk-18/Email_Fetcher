# Email Fetcher Project

This Java application fetches unread emails from an email account using the JavaMail API and stores them in a MySQL database. The application is packaged as a JAR, and the properties file (for email and database configuration) is passed as a command-line argument.

## Steps to Run

1. Compile the Java code:
    ```bash
    javac -d out src/EmailFetcher.java
    ```

2. Create the JAR file:
    ```bash
    jar cvfm EmailFetcher.jar manifest.txt -C out/ .
    ```

3. Run the JAR file with the properties file as an argument:
    ```bash
    java -jar EmailFetcher.jar /path/to/config.properties
    ```

## Database

You need to create a MySQL database and table to store the email details:

```sql
CREATE DATABASE email_db;

USE email_db;

CREATE TABLE emails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(255),
    subject VARCHAR(255),
    body TEXT,
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
