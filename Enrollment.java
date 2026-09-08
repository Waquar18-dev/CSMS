import java.io.Serializable;

public class Enrollment implements Serializable {
    private final String studentId, courseCode, semester;
    public Enrollment(String studentId, String courseCode, String semester) {
        this.studentId = studentId; this.courseCode = courseCode; this.semester = semester;
    }
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public String getSemester() { return semester; }
}
