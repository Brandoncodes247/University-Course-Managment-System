package universitycoursemanagementsystem.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class AnalyticsDAO {

    // Fetch average grades by unit
    public Map<Integer, Double> getUnitAverageGrades() {
        Map<Integer, Double> unitAverages = new HashMap<>();
        String query = "SELECT AVG(course_marks) AS average, unit_id FROM grades GROUP BY unit_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                unitAverages.put(rs.getInt("unit_id"), rs.getDouble("average"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return unitAverages;
    }

    // Fetch average grades by student
    public Map<Integer, Double> getStudentAverageGrades() {
        Map<Integer, Double> studentAverages = new HashMap<>();
        String query = "SELECT AVG(course_marks) AS average, student_id FROM grades GROUP BY student_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                studentAverages.put(rs.getInt("student_id"), rs.getDouble("average"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return studentAverages;
    }

    // Fetch average grades by student and unit
    public Map<String, Double> getStudentAverageGradesByUnit(int unitID) {
        Map<String, Double> studentAverages = new HashMap<>();
        String query = "SELECT AVG(course_marks) AS average, student_id, unit_id FROM grades WHERE unit_id = ? GROUP BY student_id, unit_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, unitID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String key = "Student " + rs.getInt("student_id") + " - Unit " + rs.getInt("unit_id");
                studentAverages.put(key, rs.getDouble("average"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return studentAverages;
    }

    // Fetch number of students per course
    public Map<String, Integer> getNumberOfStudentsPerCourse() {
        Map<String, Integer> studentsPerCourse = new HashMap<>();
        String query = "SELECT course_name, COUNT(student_id) AS student_count FROM students JOIN courses ON students.course_id = courses.course_id GROUP BY course_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                studentsPerCourse.put(rs.getString("course_name"), rs.getInt("student_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return studentsPerCourse;
    }

    // Fetch average grades by course
    public Map<String, Double> getStudentAverageGradesByCourse(int courseID) {
        Map<String, Double> studentAverages = new HashMap<>();
        String query = "SELECT AVG(course_marks) AS average, student_id, unit_id FROM grades WHERE unit_id IN (SELECT unit_id FROM units WHERE course_id = ?) GROUP BY student_id, unit_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String key = "Student " + rs.getInt("student_id") + " - Unit " + rs.getInt("unit_id");
                studentAverages.put(key, rs.getDouble("average"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return studentAverages;
    }

    public Map<String,Double> getStudentGradeByUnit(int StudentID){
        Map<String,Double> studentAverages = new HashMap<>();
        String query = "SELECT AVG(course_marks) AS average, unit_id FROM grades WHERE student_id = ? GROUP BY unit_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, StudentID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                studentAverages.put("Unit " + rs.getInt("unit_id"), rs.getDouble("average"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging
        }
        return studentAverages;
    }

    public DefaultTableModel getStudentAvarageGradeInCourse(int studentID){
       
        String query = "SELECT student_id, AVG(course_marks) AS average, " +
                "CASE WHEN AVG(course_marks) >= 70 THEN 'A' " +
                "WHEN AVG(course_marks) >= 60 THEN 'B' " +
                "WHEN AVG(course_marks) >= 50 THEN 'C' " +
                "WHEN AVG(course_marks) >= 40 THEN 'D' " +
                "ELSE 'F' END AS grade " +
                "FROM grades WHERE student_id = ? GROUP BY student_id";
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Student ID","Average","Grade"});
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                model.addRow(new Object[]{rs.getInt("student_id"),rs.getDouble("average"),rs.getString("grade")});
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return model;
    }
}