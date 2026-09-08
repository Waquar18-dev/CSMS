import java.io.Serializable;

public class Attendance implements Serializable {
    private final String studentId, courseCode;
    private int attended, total;

    public Attendance(String studentId, String courseCode, int attended, int total) {
        this.studentId = studentId; this.courseCode = courseCode;
        this.attended = attended; this.total = total;
    }
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public int getAttended() { return attended; }
    public int getTotal() { return total; }
    public void setAttended(int v) { attended = v; }
    public void setTotal(int v) { total = v; }
    public double percentage() { return total == 0 ? 0 : attended * 100.0 / total; }
}
