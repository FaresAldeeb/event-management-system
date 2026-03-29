# 🎟️ Event Management System (EMS)

A Java desktop application for managing events, ticket bookings, and user roles using **Java Swing** and **MySQL**.

---

## 🧠 Overview

The Event Management System (EMS) is a role-based desktop application that allows users to create, manage, and book events.

The system includes three main roles:

- 👨‍💼 **Admin** → manages organizers, approves events, views bookings  
- 🧑‍💻 **Organizer** → creates events and manages tickets  
- 🎟️ **Guest / Attendee** → browses events and purchases tickets  

---

## 🚀 Features

### 👨‍💼 Admin
- Add new organizers  
- View all organizers  
- Approve or reject events  
- View all bookings  

---

### 🧑‍💻 Organizer
- Create new events  
- View personal events  
- Add ticket types (price & quantity)  
- Manage event tickets  

---

### 🎟️ Guest / Attendee
- Browse approved events  
- View event details  
- Buy tickets  
- Automatically create account if needed  
- Check booking using Booking ID  

---

## 🛠️ Technologies Used

- **Java**
- **Java Swing (GUI)**
- **MySQL**
- **JDBC (Database Connectivity)**
- **IntelliJ IDEA**

---

## 📁 Project Structure
event-management-system/
│── src/
│ ├── EMSLogin.java
│ ├── EMSAdminGUI.java
│ ├── EMSOrganizerGUI.java
│ ├── EMSGuestGUI.java
│
│── database/
│ └── EMSDB.sql
│
│── README.md


---

## 🗄️ Database Setup

### Create Database

Open MySQL and run:

```sql
EMSDB.sql

▶️ How to Run the Project
🔧 Requirements

Make sure you have:

Java JDK (8 or higher)
MySQL Server
IntelliJ IDEA (or any Java IDE)
MySQL JDBC Driver (mysql-connector-j)

⚙️ Step 1: Add JDBC Driver

Download MySQL Connector:

👉 https://dev.mysql.com/downloads/connector/j/

Then in IntelliJ:

File → Project Structure
Libraries → Add JAR
Select the connector file

⚙️ Step 2: Configure Database

Open your Java files and update:
```java
DB_USER = "root"
DB_PASS = "your_password"

Make sure:
MySQL is running
Database name = EMSDB

▶️ Step 3: Run Application

Run this file:
EMSLogin.java

👤 How to Use the System
👨‍💼 Admin Flow
Login as Admin
Add organizers
Approve or reject events
View bookings

🧑‍💻 Organizer Flow
Login as Organizer
Create event
Wait for approval
Add tickets

🎟️ Guest Flow
Continue as Guest
Browse approved events
Load tickets
Buy tickets
Save Booking ID
Check booking status

📌 Important Notes
Events must be approved before guests can see them
Ticket quantity decreases after purchase
Organizer can only manage their own events
Booking ID is required to track purchases

⚠️ Common Issues
❌ Database connection error
Check MySQL is running
Verify username/password
Ensure JDBC driver is added
❌ No user found
Insert users manually in database
❌ Organizer not working
Make sure organizer exists in Users table

🎯 Learning Outcomes
Java GUI development with Swing
Database integration using JDBC
SQL schema design and relationships
Role-based system design
Full system workflow (Admin → Organizer → Guest)

👨‍💻 Author

Fares Aldeeb
AI Student @ UPM (University of Prince Mugrin)

