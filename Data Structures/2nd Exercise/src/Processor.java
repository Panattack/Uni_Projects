import java.lang.Comparable;

public class Processor implements Comparable <Processor>{

    private int id;//a one of it's kind for ecery Processor
    private double sumtime = 0;
    
    private LinkedList<Task> processedTasks = new LinkedList<>();//at the start,it has no elements

    public Processor(int id)
    {
        this.id = id;
    }

    public LinkedList<Task> getList()
    {
        return this.processedTasks;
    }

    public double getActiveTime()
    {
        return sumtime;
    }

    public int compareTo(Processor p)
    {
        if (this.getActiveTime() < p.getActiveTime())
            return 1;
        else if (this.getActiveTime() > p.getActiveTime())
            return -1;
        else//equal
        {
            if(this.id < p.id)
            {
                return 1;
            }
            return -1;
        }
    }

    public void insertAtList(Task t)
    {
        processedTasks.insertAtBack(t);
        sumtime += t.getTime();
    }

    public void printProcessor()
    {
        System.out.print("id " + this.id + ",  " + "load=" + getActiveTime() + ": ");
        for (Node n = processedTasks.getHead();n != null;n = n.getNext())
        {
            System.out.print(((Task)n.getData()).getTime() + " ");
        }
        System.out.println(" ");
    }
}