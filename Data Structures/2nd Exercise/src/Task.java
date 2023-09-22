public class Task {
    
    private int id;

    private double time;

    Task(int id,double time)
    {
        this.id = id;
        this.time = time;
    }

    public int getID()
    {
        return  id;
    }

    public double getTime()
    {
        return time;
    }
}
