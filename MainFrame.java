import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final Database db = new Database();
    private final ArrayList<Student> students = db.students();
    private final ArrayList<Course> courses = db.courses();
    private final ArrayList<Enrollment> enrollments = db.enrollments();
    private final ArrayList<Attendance> attendance = db.attendance();
    private final ArrayList<Grade> grades = db.grades();
    private final ArrayList<Fee> fees = db.fees();

    private final JTabbedPane tabs = new JTabbedPane();
    private final DecimalFormat money = new DecimalFormat("$#,##0.00");
    private final DecimalFormat number = new DecimalFormat("0.00");

    public MainFrame() {
        setTitle("College Student Management System");
        setSize(1180, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { saveAll(); dispose(); }
        });

        buildUI();
        seed();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel("College Student Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Students • Courses • Attendance • Grades • Fees");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel labels = new JPanel();
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(title); labels.add(subtitle);
        header.add(labels, BorderLayout.WEST);

        JButton save = new JButton("Save Data");
        JButton refresh = new JButton("Refresh");
        save.addActionListener(e -> { saveAll(); info("Data saved successfully."); });
        refresh.addActionListener(e -> refreshAll());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh); actions.add(save);
        header.add(actions, BorderLayout.EAST);

        tabs.addTab("Dashboard", dashboard());
        tabs.addTab("Students", studentsPanel());
        tabs.addTab("Courses", coursesPanel());
        tabs.addTab("Enrollment", enrollmentPanel());
        tabs.addTab("Attendance", attendancePanel());
        tabs.addTab("Grades", gradesPanel());
        tabs.addTab("Fees", feesPanel());
        tabs.addTab("Reports", reportsPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel dashboard() {
        JPanel p = new JPanel(new BorderLayout(15,15));
        p.setBorder(new EmptyBorder(25,25,25,25));
        JPanel cards = new JPanel(new GridLayout(1,5,15,15));
        cards.add(card("Students", () -> students.size()));
        cards.add(card("Courses", () -> courses.size()));
        cards.add(card("Enrollments", () -> enrollments.size()));
        cards.add(card("Grades", () -> grades.size()));
        cards.add(card("Fee Balance", () -> {
            double x=0; for(Fee f:fees)x+=f.getBalance(); return money.format(x);
        }));
        p.add(cards, BorderLayout.NORTH);

        JTextArea help = new JTextArea(
            "Welcome to the College Student Management System.\n\n" +
            "Use the tabs above to manage your college data.\n\n" +
            "• Students: Add, edit, delete and search students.\n" +
            "• Courses: Manage courses and credits.\n" +
            "• Enrollment: Assign students to courses.\n" +
            "• Attendance: Record classes attended and calculate percentages.\n" +
            "• Grades: Enter marks and calculate GPA.\n" +
            "• Fees: Create charges and record payments.\n" +
            "• Reports: Generate useful academic and financial summaries.\n\n" +
            "Data is stored locally in the college_gui_data folder."
        );
        help.setEditable(false); help.setFont(new Font("SansSerif",Font.PLAIN,16));
        help.setBorder(new EmptyBorder(20,20,20,20));
        p.add(help, BorderLayout.CENTER);
        return p;
    }

    private JPanel card(String title, Supplier supplier) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(18,15,18,15)));
        JLabel a = new JLabel(title, SwingConstants.CENTER);
        JLabel b = new JLabel(String.valueOf(supplier.get()), SwingConstants.CENTER);
        a.setFont(new Font("SansSerif",Font.BOLD,14));
        b.setFont(new Font("SansSerif",Font.BOLD,25));
        p.add(a,BorderLayout.NORTH); p.add(b,BorderLayout.CENTER);
        return p;
    }

    interface Supplier { Object get(); }

    private JPanel studentsPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10)); p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"ID","Name","Email","Phone","Department","Year"};
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable table=new JTable(m); table.setRowHeight(25);
        JTextField search=new JTextField();
        JButton add=new JButton("Add Student"), edit=new JButton("Edit"), del=new JButton("Delete");
        JButton view=new JButton("View Details");
        JPanel top=new JPanel(new BorderLayout(8,0)); top.add(new JLabel("Search: "),BorderLayout.WEST); top.add(search);
        JPanel buttons=new JPanel(); buttons.add(add); buttons.add(edit); buttons.add(del); buttons.add(view);
        top.add(buttons,BorderLayout.EAST); p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(table));

        Runnable reload=()->{
            m.setRowCount(0); String q=search.getText().toLowerCase();
            for(Student s:students) if((s.getId()+" "+s.getFullName()+" "+s.getEmail()+" "+s.getDepartment()).toLowerCase().contains(q))
                m.addRow(new Object[]{s.getId(),s.getFullName(),s.getEmail(),s.getPhone(),s.getDepartment(),s.getYear()});
        };
        search.getDocument().addDocumentListener(new SimpleDocumentListener(reload));
        add.addActionListener(e->studentDialog(null,reload));
        edit.addActionListener(e->{Student s=selectedStudent(table);if(s!=null)studentDialog(s,reload);});
        del.addActionListener(e->{Student s=selectedStudent(table);if(s!=null&&confirm("Delete "+s.getFullName()+"?")){students.remove(s);saveAll();reload.run();}});
        view.addActionListener(e->{Student s=selectedStudent(table);if(s!=null)showStudent(s);});
        reload.run(); return p;
    }

    private Student selectedStudent(JTable t) {
        int r=t.getSelectedRow(); if(r<0){warn("Select a student first.");return null;}
        return findStudent(String.valueOf(t.getValueAt(r,0)));
    }

    private void studentDialog(Student existing, Runnable refresh) {
        JTextField id=new JTextField(existing==null?"":existing.getId());
        JTextField first=new JTextField(existing==null?"":existing.getFirstName());
        JTextField last=new JTextField(existing==null?"":existing.getLastName());
        JTextField email=new JTextField(existing==null?"":existing.getEmail());
        JTextField phone=new JTextField(existing==null?"":existing.getPhone());
        JTextField dob=new JTextField(existing==null?"":existing.getDob());
        JTextField dept=new JTextField(existing==null?"":existing.getDepartment());
        JSpinner year=new JSpinner(new SpinnerNumberModel(existing==null?1:existing.getYear(),1,4,1));
        JTextField address=new JTextField(existing==null?"":existing.getAddress());
        JPanel form=formGrid();
        addField(form,"Student ID",id); addField(form,"First name",first); addField(form,"Last name",last);
        addField(form,"Email",email); addField(form,"Phone",phone); addField(form,"Date of birth",dob);
        addField(form,"Department",dept); addField(form,"Year",year); addField(form,"Address",address);
        id.setEnabled(existing==null);
        int result=JOptionPane.showConfirmDialog(this,form,existing==null?"Add Student":"Edit Student",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(result!=JOptionPane.OK_OPTION)return;
        if(first.getText().isBlank()||last.getText().isBlank()||email.getText().isBlank()||dept.getText().isBlank()){
            warn("First name, last name, email and department are required."); return;
        }
        if(existing==null){
            if(id.getText().isBlank()||findStudent(id.getText())!=null){warn("Student ID is empty or already exists.");return;}
            students.add(new Student(id.getText().trim(),first.getText().trim(),last.getText().trim(),
                    email.getText().trim(),phone.getText().trim(),dob.getText().trim(),dept.getText().trim(),
                    (Integer)year.getValue(),address.getText().trim()));
        }else{
            existing.setFirstName(first.getText().trim()); existing.setLastName(last.getText().trim());
            existing.setEmail(email.getText().trim()); existing.setPhone(phone.getText().trim());
            existing.setDob(dob.getText().trim()); existing.setDepartment(dept.getText().trim());
            existing.setYear((Integer)year.getValue()); existing.setAddress(address.getText().trim());
        }
        saveAll(); refresh.run();
    }

    private void showStudent(Student s) {
        StringBuilder x=new StringBuilder();
        x.append("ID: ").append(s.getId()).append("\nName: ").append(s.getFullName())
         .append("\nEmail: ").append(s.getEmail()).append("\nPhone: ").append(s.getPhone())
         .append("\nDOB: ").append(s.getDob()).append("\nDepartment: ").append(s.getDepartment())
         .append("\nYear: ").append(s.getYear()).append("\nAddress: ").append(s.getAddress());
        JOptionPane.showMessageDialog(this,x.toString(),"Student Details",JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel coursesPanel() {
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"Code","Course Name","Credits","Department"};
        DefaultTableModel m=model(cols); JTable table=new JTable(m);table.setRowHeight(25);
        JButton add=new JButton("Add Course"),edit=new JButton("Edit"),del=new JButton("Delete");
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));top.add(add);top.add(edit);top.add(del);
        p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(table));
        Runnable reload=()->{m.setRowCount(0);for(Course c:courses)m.addRow(new Object[]{c.getCode(),c.getName(),c.getCredits(),c.getDepartment()});};
        add.addActionListener(e->courseDialog(null,reload)); edit.addActionListener(e->{Course c=selectedCourse(table);if(c!=null)courseDialog(c,reload);});
        del.addActionListener(e->{Course c=selectedCourse(table);if(c!=null&&confirm("Delete "+c.getCode()+"?")){courses.remove(c);enrollments.removeIf(x->x.getCourseCode().equalsIgnoreCase(c.getCode()));saveAll();reload.run();}});
        reload.run();return p;
    }

    private Course selectedCourse(JTable t){int r=t.getSelectedRow();if(r<0){warn("Select a course.");return null;}return findCourse(String.valueOf(t.getValueAt(r,0)));}

    private void courseDialog(Course existing,Runnable refresh){
        JTextField code=new JTextField(existing==null?"":existing.getCode()),name=new JTextField(existing==null?"":existing.getName());
        JSpinner credits=new JSpinner(new SpinnerNumberModel(existing==null?3:existing.getCredits(),1,8,1));
        JTextField dept=new JTextField(existing==null?"":existing.getDepartment()); JPanel f=formGrid();
        addField(f,"Course code",code);addField(f,"Course name",name);addField(f,"Credits",credits);addField(f,"Department",dept);code.setEnabled(existing==null);
        if(JOptionPane.showConfirmDialog(this,f,existing==null?"Add Course":"Edit Course",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            if(name.getText().isBlank()||dept.getText().isBlank()){warn("Course name and department are required.");return;}
            if(existing==null){if(code.getText().isBlank()||findCourse(code.getText())!=null){warn("Code is empty or already exists.");return;}
                courses.add(new Course(code.getText().trim().toUpperCase(),name.getText().trim(),(Integer)credits.getValue(),dept.getText().trim()));
            }else{existing.setName(name.getText().trim());existing.setCredits((Integer)credits.getValue());existing.setDepartment(dept.getText().trim());}
            saveAll();refresh.run();
        }
    }

    private JPanel enrollmentPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"Student ID","Student Name","Course","Course Name","Semester"};DefaultTableModel m=model(cols);JTable t=new JTable(m);
        JButton add=new JButton("Enroll Student"),drop=new JButton("Drop Enrollment");
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));top.add(add);top.add(drop);p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(t));
        Runnable reload=()->{m.setRowCount(0);for(Enrollment e:enrollments){Student s=findStudent(e.getStudentId());Course c=findCourse(e.getCourseCode());
            m.addRow(new Object[]{e.getStudentId(),s==null?"Unknown":s.getFullName(),e.getCourseCode(),c==null?"Unknown":c.getName(),e.getSemester()});}};
        add.addActionListener(e->enrollDialog(reload));drop.addActionListener(e->{int r=t.getSelectedRow();if(r<0){warn("Select an enrollment.");return;}
            String sid=String.valueOf(t.getValueAt(r,0)),code=String.valueOf(t.getValueAt(r,2));
            enrollments.removeIf(x->x.getStudentId().equalsIgnoreCase(sid)&&x.getCourseCode().equalsIgnoreCase(code));saveAll();reload.run();});
        reload.run();return p;
    }

    private void enrollDialog(Runnable refresh){
        JComboBox<String> s=new JComboBox<>();for(Student x:students)s.addItem(x.getId()+" - "+x.getFullName());
        JComboBox<String> c=new JComboBox<>();for(Course x:courses)c.addItem(x.getCode()+" - "+x.getName());
        JTextField sem=new JTextField("Fall 2026");JPanel f=formGrid();addField(f,"Student",s);addField(f,"Course",c);addField(f,"Semester",sem);
        if(students.isEmpty()||courses.isEmpty()){warn("Add at least one student and one course first.");return;}
        if(JOptionPane.showConfirmDialog(this,f,"Enroll Student",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            String sid=((String)s.getSelectedItem()).split(" - ")[0],code=((String)c.getSelectedItem()).split(" - ")[0];
            if(enrollments.stream().anyMatch(x->x.getStudentId().equalsIgnoreCase(sid)&&x.getCourseCode().equalsIgnoreCase(code))){warn("Already enrolled.");return;}
            enrollments.add(new Enrollment(sid,code,sem.getText().trim()));saveAll();refresh.run();
        }
    }

    private JPanel attendancePanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"Student","Student Name","Course","Course Name","Attended","Total","Percentage"};DefaultTableModel m=model(cols);JTable t=new JTable(m);
        JButton record=new JButton("Record / Update Attendance");p.add(record,BorderLayout.NORTH);p.add(new JScrollPane(t));
        Runnable reload=()->{m.setRowCount(0);for(Attendance a:attendance){Student s=findStudent(a.getStudentId());Course c=findCourse(a.getCourseCode());
            m.addRow(new Object[]{a.getStudentId(),s==null?"Unknown":s.getFullName(),a.getCourseCode(),c==null?"Unknown":c.getName(),a.getAttended(),a.getTotal(),number.format(a.percentage())+"%"});}};
        record.addActionListener(e->attendanceDialog(reload));reload.run();return p;
    }

    private void attendanceDialog(Runnable refresh){
        if(enrollments.isEmpty()){warn("Enroll a student in a course first.");return;}
        JComboBox<String> combo=new JComboBox<>();for(Enrollment e:enrollments)combo.addItem(e.getStudentId()+" | "+e.getCourseCode());
        JSpinner total=new JSpinner(new SpinnerNumberModel(10,0,1000,1)),att=new JSpinner(new SpinnerNumberModel(8,0,1000,1));
        JPanel f=formGrid();addField(f,"Student | Course",combo);addField(f,"Total classes",total);addField(f,"Classes attended",att);
        if(JOptionPane.showConfirmDialog(this,f,"Attendance",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            String[] parts=((String)combo.getSelectedItem()).split(" \\| ");int a=(Integer)att.getValue(),tt=(Integer)total.getValue();
            if(a>tt){warn("Attended classes cannot exceed total.");return;}
            Attendance old=findAttendance(parts[0],parts[1]);if(old==null)attendance.add(new Attendance(parts[0],parts[1],a,tt));else{old.setAttended(a);old.setTotal(tt);}
            saveAll();refresh.run();
        }
    }

    private JPanel gradesPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"Student","Student Name","Course","Course Name","Marks","Grade"};DefaultTableModel m=model(cols);JTable t=new JTable(m);
        JButton enter=new JButton("Enter / Update Marks"),gpa=new JButton("Show GPA");JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));top.add(enter);top.add(gpa);p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(t));
        Runnable reload=()->{m.setRowCount(0);for(Grade g:grades){Student s=findStudent(g.getStudentId());Course c=findCourse(g.getCourseCode());
            m.addRow(new Object[]{g.getStudentId(),s==null?"Unknown":s.getFullName(),g.getCourseCode(),c==null?"Unknown":c.getName(),number.format(g.getMarks()),g.letter()});}};
        enter.addActionListener(e->gradeDialog(reload));gpa.addActionListener(e->{String sid=ask("Student ID");if(sid!=null){Student s=findStudent(sid);if(s==null)warn("Student not found.");else info(s.getFullName()+" GPA: "+number.format(gpa(sid)));}});
        reload.run();return p;
    }

    private void gradeDialog(Runnable refresh){
        if(enrollments.isEmpty()){warn("Enroll a student first.");return;}
        JComboBox<String> combo=new JComboBox<>();for(Enrollment e:enrollments)combo.addItem(e.getStudentId()+" | "+e.getCourseCode());
        JSpinner marks=new JSpinner(new SpinnerNumberModel(75.0,0.0,100.0,0.5));JPanel f=formGrid();addField(f,"Student | Course",combo);addField(f,"Marks (0-100)",marks);
        if(JOptionPane.showConfirmDialog(this,f,"Enter Marks",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            String[] x=((String)combo.getSelectedItem()).split(" \\| ");double v=(Double)marks.getValue();Grade old=findGrade(x[0],x[1]);
            if(old==null)grades.add(new Grade(x[0],x[1],v));else old.setMarks(v);saveAll();refresh.run();
        }
    }

    private JPanel feesPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        String[] cols={"Fee ID","Student","Description","Amount","Paid","Balance"};DefaultTableModel m=model(cols);JTable t=new JTable(m);
        JButton add=new JButton("Add Fee"),pay=new JButton("Make Payment"),refresh=new JButton("Refresh");JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));top.add(add);top.add(pay);top.add(refresh);p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(t));
        Runnable reload=()->{m.setRowCount(0);for(Fee f:fees){Student s=findStudent(f.getStudentId());m.addRow(new Object[]{f.getId(),s==null?"Unknown":s.getFullName(),f.getDescription(),money.format(f.getAmount()),money.format(f.getPaid()),money.format(f.getBalance())});}};
        add.addActionListener(e->feeDialog(reload));pay.addActionListener(e->{int r=t.getSelectedRow();if(r<0){warn("Select a fee record.");return;}Fee f=findFee(String.valueOf(t.getValueAt(r,0)));
            String v=ask("Payment amount (balance "+money.format(f.getBalance())+")");if(v!=null)try{f.pay(Double.parseDouble(v));saveAll();reload.run();}catch(Exception ex){warn(ex.getMessage());}});
        refresh.addActionListener(e->reload.run());reload.run();return p;
    }

    private void feeDialog(Runnable refresh){
        if(students.isEmpty()){warn("Add a student first.");return;}
        JComboBox<String> s=new JComboBox<>();for(Student x:students)s.addItem(x.getId()+" - "+x.getFullName());
        JTextField desc=new JTextField();JTextField amount=new JTextField();JPanel f=formGrid();addField(f,"Student",s);addField(f,"Description",desc);addField(f,"Amount",amount);
        if(JOptionPane.showConfirmDialog(this,f,"Add Fee",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)try{
            double a=Double.parseDouble(amount.getText());if(a<=0)throw new Exception("Amount must be positive.");
            String sid=((String)s.getSelectedItem()).split(" - ")[0];fees.add(new Fee("F"+System.currentTimeMillis()%100000,sid,desc.getText(),a));saveAll();refresh.run();
        }catch(Exception ex){warn("Invalid amount.");}
    }

    private JPanel reportsPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(new EmptyBorder(10,10,10,10));
        JPanel buttons=new JPanel(new GridLayout(2,3,10,10));
        JButton full=new JButton("Student Full Report"),all=new JButton("All Students Summary"),course=new JButton("Course Performance");
        JButton low=new JButton("Low Attendance"),out=new JButton("Outstanding Fees"),stats=new JButton("System Statistics");
        buttons.add(full);buttons.add(all);buttons.add(course);buttons.add(low);buttons.add(out);buttons.add(stats);p.add(buttons,BorderLayout.NORTH);
        JTextArea output=new JTextArea();output.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));output.setEditable(false);p.add(new JScrollPane(output));
        full.addActionListener(e->{String id=ask("Student ID");if(id!=null){Student s=findStudent(id);if(s==null)warn("Student not found.");else output.setText(fullReport(s));}});
        all.addActionListener(e->output.setText(allReport()));course.addActionListener(e->{String code=ask("Course code");if(code!=null)output.setText(courseReport(code));});
        low.addActionListener(e->output.setText(lowAttendance()));out.addActionListener(e->output.setText(outstanding()));stats.addActionListener(e->output.setText(statistics()));
        return p;
    }

    private String fullReport(Student s){
        StringBuilder x=new StringBuilder("STUDENT FULL REPORT\n"+"=".repeat(60)+"\n");
        x.append("ID: ").append(s.getId()).append("\nName: ").append(s.getFullName()).append("\nEmail: ").append(s.getEmail())
         .append("\nDepartment: ").append(s.getDepartment()).append("\nYear: ").append(s.getYear()).append("\n\nCOURSES\n");
        for(Enrollment e:enrollments)if(e.getStudentId().equalsIgnoreCase(s.getId()))x.append(e.getCourseCode()).append(" - ").append(e.getSemester()).append("\n");
        x.append("\nGRADES\n");for(Grade g:grades)if(g.getStudentId().equalsIgnoreCase(s.getId()))x.append(g.getCourseCode()).append("  ").append(number.format(g.getMarks())).append("  ").append(g.letter()).append("\n");
        x.append("\nGPA: ").append(number.format(gpa(s.getId()))).append("\nAverage attendance: ").append(number.format(attAvg(s.getId()))).append("%\nFee balance: ").append(money.format(balance(s.getId())));
        return x.toString();
    }

    private String allReport(){
        StringBuilder x=new StringBuilder("ALL STUDENTS SUMMARY\n"+"=".repeat(75)+"\n");
        x.append(String.format("%-8s %-25s %-8s %-10s %-12s %-12s%n","ID","Name","Year","GPA","Attendance","Balance"));
        for(Student s:students)x.append(String.format("%-8s %-25s %-8d %-10s %-12s %-12s%n",s.getId(),s.getFullName(),s.getYear(),number.format(gpa(s.getId())),number.format(attAvg(s.getId()))+"%",money.format(balance(s.getId()))));
        return x.toString();
    }

    private String courseReport(String code){
        Course c=findCourse(code);if(c==null)return "Course not found.";
        StringBuilder x=new StringBuilder("COURSE PERFORMANCE: "+c.getName()+"\n"+"=".repeat(60)+"\n");double sum=0;int n=0;
        for(Grade g:grades)if(g.getCourseCode().equalsIgnoreCase(code)){Student s=findStudent(g.getStudentId());x.append(String.format("%-8s %-25s %6.2f %s%n",g.getStudentId(),s==null?"Unknown":s.getFullName(),g.getMarks(),g.letter()));sum+=g.getMarks();n++;}
        x.append("\nStudents graded: ").append(n).append("\nAverage: ").append(n==0?"0":number.format(sum/n));return x.toString();
    }

    private String lowAttendance(){StringBuilder x=new StringBuilder("STUDENTS BELOW 75% ATTENDANCE\n"+"=".repeat(50)+"\n");for(Student s:students)if(attAvg(s.getId())>0&&attAvg(s.getId())<75)x.append(s.getId()).append("  ").append(s.getFullName()).append("  ").append(number.format(attAvg(s.getId()))).append("%\n");return x.toString();}
    private String outstanding(){StringBuilder x=new StringBuilder("OUTSTANDING FEES\n"+"=".repeat(50)+"\n");for(Student s:students)if(balance(s.getId())>0)x.append(s.getId()).append("  ").append(s.getFullName()).append("  ").append(money.format(balance(s.getId()))).append("\n");return x.toString();}
    private String statistics(){double charged=0,paid=0;for(Fee f:fees){charged+=f.getAmount();paid+=f.getPaid();}return "SYSTEM STATISTICS\n"+"=".repeat(45)+"\nStudents: "+students.size()+"\nCourses: "+courses.size()+"\nEnrollments: "+enrollments.size()+"\nGrades: "+grades.size()+"\nAttendance records: "+attendance.size()+"\nFee records: "+fees.size()+"\nTotal charged: "+money.format(charged)+"\nTotal paid: "+money.format(paid)+"\nOutstanding: "+money.format(charged-paid);}

    private double gpa(String sid){double p=0;int credits=0;for(Grade g:grades)if(g.getStudentId().equalsIgnoreCase(sid)){Course c=findCourse(g.getCourseCode());if(c!=null){p+=g.points()*c.getCredits();credits+=c.getCredits();}}return credits==0?0:p/credits;}
    private double attAvg(String sid){double x=0;int n=0;for(Attendance a:attendance)if(a.getStudentId().equalsIgnoreCase(sid)){x+=a.percentage();n++;}return n==0?0:x/n;}
    private double balance(String sid){double x=0;for(Fee f:fees)if(f.getStudentId().equalsIgnoreCase(sid))x+=f.getBalance();return x;}

    private DefaultTableModel model(String[] cols){return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};}
    private JPanel formGrid(){JPanel p=new JPanel(new GridLayout(0,2,8,8));p.setBorder(new EmptyBorder(8,8,8,8));return p;}
    private void addField(JPanel p,String label,Component c){p.add(new JLabel(label));p.add(c);}
    private String ask(String prompt){return JOptionPane.showInputDialog(this,prompt);}
    private boolean confirm(String s){return JOptionPane.showConfirmDialog(this,s,"Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    private void info(String s){JOptionPane.showMessageDialog(this,s,"Information",JOptionPane.INFORMATION_MESSAGE);}
    private void warn(String s){JOptionPane.showMessageDialog(this,s,"Warning",JOptionPane.WARNING_MESSAGE);}
    private Student findStudent(String id){for(Student s:students)if(s.getId().equalsIgnoreCase(id.trim()))return s;return null;}
    private Course findCourse(String id){for(Course c:courses)if(c.getCode().equalsIgnoreCase(id.trim()))return c;return null;}
    private Attendance findAttendance(String s,String c){for(Attendance a:attendance)if(a.getStudentId().equalsIgnoreCase(s)&&a.getCourseCode().equalsIgnoreCase(c))return a;return null;}
    private Grade findGrade(String s,String c){for(Grade g:grades)if(g.getStudentId().equalsIgnoreCase(s)&&g.getCourseCode().equalsIgnoreCase(c))return g;return null;}
    private Fee findFee(String id){for(Fee f:fees)if(f.getId().equalsIgnoreCase(id))return f;return null;}

    private void saveAll(){db.saveStudents(students);db.saveCourses(courses);db.saveEnrollments(enrollments);db.saveAttendance(attendance);db.saveGrades(grades);db.saveFees(fees);}
    private void refreshAll(){getContentPane().removeAll();buildUI();revalidate();repaint();}
    private void seed(){
        if(courses.isEmpty()){
            courses.add(new Course("CS101","Programming Fundamentals",4,"Computer Science"));
            courses.add(new Course("CS102","Database Systems",4,"Computer Science"));
            courses.add(new Course("MA101","Discrete Mathematics",3,"Mathematics"));
            saveAll();
        }
    }

    static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable r; SimpleDocumentListener(Runnable r){this.r=r;}
        public void insertUpdate(javax.swing.event.DocumentEvent e){r.run();}
        public void removeUpdate(javax.swing.event.DocumentEvent e){r.run();}
        public void changedUpdate(javax.swing.event.DocumentEvent e){r.run();}
    }
}
