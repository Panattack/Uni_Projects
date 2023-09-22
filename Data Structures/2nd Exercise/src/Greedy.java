import java.io.*;
import java.util.StringTokenizer;
import java.util.Scanner;

public class Greedy {

    private static int numberp;
    public static Task[] tasks;
    
    public static void setnumberpr(int n)
    {
        numberp = n;
    }

    public static int getnumberpr()
    {
        return numberp;
    } 

    public static void ReadTasks(String m) throws Exception
    {
        String line;
        try {
            int i = 0;
            boolean eof = false;//end of file,h kenh seira
            boolean ok = true;  
            StringTokenizer lineTokens;
            String token;
            FileReader reader = new FileReader(m);
            BufferedReader buff = new BufferedReader(reader);
            int counter_tasks = 0;
            int number_of_tasks = 0;
            int counter_loops = 0;
            while ((!eof) && ok) {
                line = buff.readLine();
                if (line == null)
                    eof = true;
                else {
                    counter_loops++;
                    lineTokens = new StringTokenizer(line);
                    token = lineTokens.nextToken();
                    int n = Integer.parseInt(token);
                    if (counter_loops == 1)
                    {
                        setnumberpr(n);
                    }
                    else if(counter_loops == 2)
                    {
                        number_of_tasks = n;
                        tasks = new Task[n];
                    }
                    else //counter_loops >2
                    {
                        counter_tasks++;
                        //n == id
                        token = lineTokens.nextToken();
                        //v == ActiveTime
                        int v = Integer.parseInt(token);
                        if (counter_tasks > number_of_tasks)
                        {
                            throw new Exception("The number of the declared tasks is smaller than the given tasks");
                        }
                        //Create Task
                        Task t = new Task(n,v);
                        tasks[i] = t;
                        i++;
                    }
                }
            }
            reader.close();
            if (number_of_tasks < counter_tasks)
            {
                throw new Exception("The number of the declared tasks is bigger than the given tasks");
            }
        }
        catch (IOException ex) {
            System.out.println("Error reading file....");
        }
    }
    public static double greedy()
    {
        MaxPQ<Processor> heap = new MaxPQ<>();//heapsort

        for (int i = 1;i<=getnumberpr();i++)
        {
            //Create Processors
            Processor processor = new Processor(i);
            //insert processor to MaxPQ
            heap.insert(processor);
        }
        for (Task task : tasks)
        {
            Processor pr = heap.getmax();
            pr.insertAtList(task);
            heap.insert(pr);
        }
        Processor pro = new Processor(5);//random Processor.at the end of the loop,it holds the Processor with the highest active time
        for (int i =  heap.size();i>=1;i--)
        {
            pro = heap.getmax();
            if (tasks.length <= 50)
            {
                pro.printProcessor();
            }
        }
        double makespan = pro.getActiveTime();
        return makespan;
    }

    public static void main(String[] args) throws Exception
    {
        ReadTasks(args[0]);
        double makespan = greedy();
        System.out.println("The makespan is: " + makespan);
    }
}