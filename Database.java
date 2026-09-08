import java.io.*;
import java.util.*;

public class Database {
    private final File dir = new File("college_gui_data");

    public Database() {
        if (!dir.exists()) dir.mkdirs();
    }

    private void save(Object data, String file) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(new File(dir, file)))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not save data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> load(String file) {
        File f = new File(dir, file);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (ArrayList<T>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<Student> students(){return load("students.dat");}
    public ArrayList<Course> courses(){return load("courses.dat");}
    public ArrayList<Enrollment> enrollments(){return load("enrollments.dat");}
    public ArrayList<Attendance> attendance(){return load("attendance.dat");}
    public ArrayList<Grade> grades(){return load("grades.dat");}
    public ArrayList<Fee> fees(){return load("fees.dat");}

    public void saveStudents(List<Student> x){save(new ArrayList<>(x),"students.dat");}
    public void saveCourses(List<Course> x){save(new ArrayList<>(x),"courses.dat");}
    public void saveEnrollments(List<Enrollment> x){save(new ArrayList<>(x),"enrollments.dat");}
    public void saveAttendance(List<Attendance> x){save(new ArrayList<>(x),"attendance.dat");}
    public void saveGrades(List<Grade> x){save(new ArrayList<>(x),"grades.dat");}
    public void saveFees(List<Fee> x){save(new ArrayList<>(x),"fees.dat");}
}
