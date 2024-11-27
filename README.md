

# SiiT - Student Information and Integrated Tracking

**SiiT** is a comprehensive system designed to help educators and students track academic performance over time. It allows the recording, updating, and viewing of student grades across various subjects and generates detailed reports to monitor academic progress.

---

## Features

- **Grade Tracking**: Track and store grades for multiple subjects across various grading periods (e.g., Midterm, Final).
- **Performance Reports**: Generate reports to view overall performance, average grades, and trends.
- **User Roles**: Differentiate between faculty and student users. Faculty can update grades, while students can view their grades and performance.
- **CRUD Operations**: Implement Create, Read, Update, and Delete operations for grades and performance records.

---

## Project Scope

- **Grade Entry**: Allows faculty to enter and update grades for students in various subjects.
- **Student Performance**: Students can view their grades and overall performance in the system.
- **Data Storage**: All data is stored securely in a relational database (MySQL).
- **Performance Reporting**: Faculty and students can generate reports of performance over various periods.

---

## Technology Stack

- **Frontend**: JavaFX (for GUI)
- **Backend**: Java
- **Database**: MySQL (for storing student data and grades)
- **Build Tool**: Maven (for dependency management and building the project)

---

## Installation

### Prerequisites

- **Java JDK** version 21 or higher
- **MySQL** server running
- **Maven** (for project build)

### Steps to Install

1. **Clone the repository**:

   ```bash
   git clone https://github.com/your-username/SiiT-Grade-Tracker.git
   cd SiiT
   ```

2. **Setup Database**:
   
   Create a MySQL database named `student_grading_system` and execute the `create_tables.sql` script found in the `db` folder to initialize the required tables.

3. **Install Dependencies**:

   Run Maven to install dependencies:

   ```bash
   mvn clean install
   ```

4. **Run the Application**:

   After building, run the application:

   ```bash
   mvn javafx:run
   ```

---

## How to Use

1. **Log In**: 
   - Faculty and students can log in using their unique credentials (this will be simulated in the application for now).
   
2. **For Faculty**:
   - Use the **Grade Entry** section to input and update grades for students.
   
3. **For Students**:
   - Navigate to the **Performance Overview** to view grades, average scores, and trends over time.

---

## Contributing

We welcome contributions to improve this project! If you find a bug or want to add a new feature, feel free to open an issue or submit a pull request.

### How to Contribute

1. Fork the repository.
2. Create a new branch for your changes.
3. Make the necessary changes and commit them.
4. Submit a pull request for review.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgements

- **MySQL**: For database management.
- **JavaFX**: For building the graphical user interface.
- **Maven**: For project management and dependency handling.

---

Feel free to adapt this README as needed for your project’s specific requirements. This structure includes the most important information and guides users on how to set up and contribute to the project. Let me know if you'd like any specific changes!
