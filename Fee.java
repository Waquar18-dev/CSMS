import java.io.Serializable;

public class Fee implements Serializable {
    private final String id, studentId, description;
    private final double amount;
    private double paid;

    public Fee(String id, String studentId, String description, double amount) {
        this.id=id; this.studentId=studentId; this.description=description; this.amount=amount;
    }
    public String getId(){return id;}
    public String getStudentId(){return studentId;}
    public String getDescription(){return description;}
    public double getAmount(){return amount;}
    public double getPaid(){return paid;}
    public double getBalance(){return amount-paid;}
    public void pay(double value){
        if(value<=0 || value>getBalance()) throw new IllegalArgumentException("Invalid payment amount.");
        paid += value;
    }
}
