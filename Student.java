import java.io.Serializable;

public class Student implements Serializable {
    private final String id;
    private String firstName, lastName, email, phone, dob, department, address;
    private int year;

    public Student(String id, String firstName, String lastName, String email,
                   String phone, String dob, String department, int year, String address) {
        this.id = id; this.firstName = firstName; this.lastName = lastName;
        this.email = email; this.phone = phone; this.dob = dob;
        this.department = department; this.year = year; this.address = address;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDob() { return dob; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public String getAddress() { return address; }

    public void setFirstName(String v) { firstName = v; }
    public void setLastName(String v) { lastName = v; }
    public void setEmail(String v) { email = v; }
    public void setPhone(String v) { phone = v; }
    public void setDob(String v) { dob = v; }
    public void setDepartment(String v) { department = v; }
    public void setYear(int v) { year = v; }
    public void setAddress(String v) { address = v; }
}
