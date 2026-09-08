import java.io.Serializable;

public class Course implements Serializable {
    private final String code;
    private String name, department;
    private int credits;

    public Course(String code, String name, int credits, String department) {
        this.code = code; this.name = name; this.credits = credits; this.department = department;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public void setName(String v) { name = v; }
    public void setCredits(int v) { credits = v; }
    public void setDepartment(String v) { department = v; }
}
