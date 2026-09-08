# College Student Management System - GUI

A real Java Swing desktop application.

## Features
- Dashboard
- Student add/edit/delete/search/details
- Course management
- Enrollment management
- Attendance records and percentages
- Marks, letter grades and GPA
- Fee records and payments
- Reports
- Local persistent storage

## Requirements
Java JDK 17+.

## Run in PowerShell
From this folder:

    javac (Get-ChildItem -Filter *.java).FullName
    java Main

Or open the folder in VS Code and run Main.java.

The application creates `college_gui_data` automatically and stores its data there.

## Whole tech stack --->
## 🛠️ Tech Stack & Libraries

* **Language:** Java
* **JDK:** Java 17+
* **GUI:** Java Swing & AWT
* **Data Storage:** Local file-based storage using Java Object Serialization
* **Collections:** Java `ArrayList` & `List`
* **File I/O:** Java `java.io` package
* **Formatting:** Java `DecimalFormat`
* **Event Handling:** Java AWT Event package
* **IDE:** VS Code / Any Java IDE
* **Database:** No external database; data is stored in serialized `.dat` files
* **Dependencies:** Java Standard Library only (No third-party libraries)


## FULL arechitechture --->
                 ┌─────────────────┐
                 │     Main.java   │
                 │ Entry Point     │
                 └────────┬────────┘
                          │
                          ▼
                 ┌─────────────────┐
                 │   MainFrame     │
                 │   Swing GUI     │
                 └────────┬────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
   Student.java      Course.java      Enrollment.java
        │                 │                 │
        ├─────────────────┼─────────────────┤
        ▼                 ▼                 ▼
 Attendance.java     Grade.java         Fee.java
        │                 │                 │
        └─────────────────┼─────────────────┘
                          ▼
                   Database.java
                          │
                          ▼
                    .dat files

## Future goals --->
Now i am going to disscusse about my future goals about this project.i am going to add/use JDBC as a database (using mysql) currently it store files localiy using Java Object Serialization and also add dark theme,Student search/filter,Login system,Attendance percentage warning,GPA calculation,Export report to CSV/PDF, Better validation and manymore.....

