import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class Comparisons {
    public static void main(String[] args) throws Exception
    {
        double[][] makespanarray = new double[30][2];//an array that deposits the makespan of every file
        double[][] averagemakespanarray = new double[3][2];//an array that deposits the average makespan of every N (N=100,250,500)
        //the average makespan is for every n and for every 10 files 
        writeData();//we write the files
        int k = 0;//an index that shows the line of the makespanarray that the makespan will be deposited
        for (int i=1;i<=3;i++)//for every N
        {
            double sum1 = 0;//unsorted
            double sum2 = 0;//sorted
            for(int j=1;j<=10;j++)//we run 10 files every time
            {
                String text = String.valueOf(i).concat("_DataCheck_").concat(String.valueOf(j))+".txt";//we create the string name of the file that we want to inspect
                Greedy.ReadTasks(text);//creating a static Task array in the class Greedy
                double makespan1 = Greedy.greedy();//the makespan of the unsorted tasks
                makespanarray[k][0] = makespan1;//deposit the makespan1 to k-line,0-column
                sum1 += makespan1;//calculate the sum1 for the later average
                Greedy.tasks = Sort.quicksort(Greedy.tasks, 0, Greedy.tasks.length - 1);//sort the tasks list of the Greedy class.
                double makespan2 = Greedy.greedy();//the makespan of the unsorted tasks
                makespanarray[k][1] = makespan2;//deposit the makespan1 to k-line,0-column
                sum2 += makespan2;//calculate the sum2 for the later average
                k++;
            }
            averagemakespanarray[i-1][0] = sum1/10;
            averagemakespanarray[i-1][1] = sum2/10; 
        }
        for (int i = 0;i<3;i++)
        {
            if (i == 0)
            {
                System.out.print("The average makespan for the 1st algorithm for N == 100 is : " + averagemakespanarray[i][0]);
                System.out.println();
                System.out.print("The average makespan for the 2nd algorithm for N == 100 is : " + averagemakespanarray[i][1]);
                System.out.println();
            }
                    
            if (i == 1)
            {
                System.out.print("The average makespan for the 1st algorithm for N == 250 is : " + averagemakespanarray[i][0]);
                System.out.println();
                System.out.print("The average makespan for the 2nd algorithm for N == 250 is : " + averagemakespanarray[i][1]);
                System.out.println();
            }
            if (i == 2)
            {
                System.out.print("The average makespan for the 1st algorithm for N == 500 is : " + averagemakespanarray[i][0]);
                System.out.println();
                System.out.print("The average makespan for the 2nd algorithm for N == 500 is : " + averagemakespanarray[i][1]);
                System.out.println();
            }
        }
    }
    
    public static void writeData() throws Exception
    {   Random random = new Random();
        try
        {
            for(int i=1; i <= 3; i++)//N == 100,250,500
            {
                for (int j = 1; j<=10; j++)
                {
                    String name = String.valueOf(i).concat("_DataCheck_").concat(String.valueOf(j))+".txt";
                    //To understand which file we have. That is, here i = 1 refers to the file it contains
                    // 100 tasks, i = 2  250 and i = 3 500. j is each of the 10 files.
                    BufferedWriter buffer = new BufferedWriter(new FileWriter(name));
                    if (i==1)
                    {
                        buffer.write("10"+"\n");    //SquareRoot(100)=10
                        buffer.write("100"+"\n");   
                        for (int k=1;k<=100;k++)
                        {
                            int time = random.nextInt(79); //Assume that each process has a maximum of 78 seconds time.
                            buffer.write(k+" "+time); //k = id
                            if(k<100)
                                buffer.write("\n");
                        }
                    }

                    else if (i == 2)
                    {
                        buffer.write("15"+"\n");    //floor(SquareRoot(250))=15
                        buffer.write("250"+"\n");
                        for (int k=1;k<=250;k++)
                        {
                            int time = random.nextInt(79); 
                            buffer.write(k+" "+time); //k = id
                            if(k<250)
                                buffer.write("\n");
                        }
                    }
                    else{
                        buffer.write("22"+"\n");    //floor(SquareRoot(500))=22
                        buffer.write("500"+"\n");
                        for (int k=1;k<=500;k++)
                        {
                            int time = random.nextInt(79); 
                            buffer.write(k+" "+time); //k = id
                            if(k<500)
                                buffer.write("\n");
                        }
                    }
                    //close output stream
                    buffer.close();  
                }
            }
        }
        catch(Exception e){ 
            System.out.println("Error writing file !!!");
        }
    }
}
