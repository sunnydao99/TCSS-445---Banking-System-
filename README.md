# TCSS-445---Banking-System-
# Banking Application

The Banking Application is a Java program that allows customers to perform various banking transactions such as viewing account balance, making deposits, withdrawing funds, and transferring money. The application interacts with a SQL database to store and retrieve customer and transaction information.

## Prerequisites

Before running the application, make sure you have the following installed:

- Java Development Kit (JDK) 8 or above
- MySQL Server
- JDBC driver for MySQL (usually provided as a JAR file)

## Installation

1. Clone the repository to your local machine:

git clone https://github.com/your-username/banking-application.git


2. Open the project in your preferred Java IDE.

3. Import the JDBC driver for MySQL into your project. This step may vary depending on your IDE, but generally, you can add the JAR file to the project's build path or dependencies.

4. Configure the database connection settings. Open the `BankingApp.java` file and locate the following lines:

```java
private static final String DB_URL = "jdbc:sqlserver://localhost:1434;database=Banking;integratedSecurity=true;trustServerCertificate=true;"
Make sure to adjust the database URL if necessary.

Create the necessary database tables. Execute the SQL scripts provided in the database directory to create the required tables and insert sample data.
Usage
Compile and run the BankingApp.java file.

The application will start and display the main menu:

Welcome to the Banking Application!
1. Customer
2. Employee
3. Exit
Enter your choice:
Enter your choice by typing the corresponding number and pressing Enter.

Follow the prompts and provide the required information to perform various banking transactions such as viewing account balance, making deposits, withdrawing funds, transferring money, etc.

Enjoy using the Banking Application!

Contributing
Contributions are welcome! If you find any issues or have suggestions for improvement, please submit a pull request or open an issue on the GitHub repository.

License
This project is licensed under the MIT License. See the LICENSE file for more information.


Feel free to modify this README file according to your specific project details and requirements. Provide instructions that are relevant to your project setup and ensure that the installation and usage steps are clear and concise.
