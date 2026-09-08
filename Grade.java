import java.io.Serializable;

public class Grade implements Serializable {
    private final String studentId, courseCode;
    private double marks;

    public Grade(String studentId, String courseCode, double marks) {
        this.studentId = studentId; this.courseCode = courseCode; this.marks = marks;
    }
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public double getMarks() { return marks; }
    public void setMarks(double v) { marks = v; }

    public String letter() {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        if (marks >= 40) return "E";
        return "F";
    }
    public double points() {
        return switch (letter()) {
            case "A+", "A" -> 4.0;
            case "B" -> 3.0;
            case "C" -> 2.0;
            case "D" -> 1.0;
            case "E" -> 0.5;
            default -> 0.0;
        };
    }
}
