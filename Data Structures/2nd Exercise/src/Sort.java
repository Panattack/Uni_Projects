public class Sort {
    public Sort() {
    }
    static void exch(Task[] tasks, int i, int j)
    {
        Task temp = tasks[j];
        tasks[j] = tasks[i];
        tasks[i] = temp;
    }
    static boolean greater(Task p,Task s)
    {
        if (p.getTime() > s.getTime())
            return true;
        else
        {
            if (p.getTime() == s.getTime())
            {
                if (p.getID() > s.getID())
                    return true;
                else
                    return false;
            }
            return false;
        }
    }
    static Task[] quicksort(Task[] task,int left,int right)
    {
        if(right <= left)
        {
            return null;
        }
        int i = partition(task,left,right);
        quicksort(task, left, i - 1);
        quicksort(task, i+1, right);
        return task;
    }
    static int partition(Task a[], int p, int r)
    { 
        int i = p-1, j = r; Task v = a[r];
        for (;;)
        { 
            while (greater(a[++i], v)) ;//for descending
            while (greater(v, a[--j]))
                if (j == p) break;
            if (i >= j) break;
            exch(a, i, j);
        }
        exch(a, i, r);
        return i; 
    } /*test for quicksort
    public static void main(String[] args)
    {
        Task t2 = new Task(5,40);
        Task t4 = new Task(6,30);
        Task t3 = new Task(7,20);
        Task t1 = new Task(8,100);
        Task t5 = new Task(9,100);
        Task t6 = new Task(10,60);
        Task t7 = new Task(11,0);
        Task[] arr = {t1,t2,t3,t4,t5,t6,t7};
        quicksort(arr, 0,6);
        for (Task i:arr)
        {
            System.out.print(i.getID()+ " " + i.getTime() + '\n');
        }
    }*/
}