public class MaxPQ<T extends Comparable<T>> implements PQInterface<T> 
{
    private T[] heap; // the heap to store data in
    private int size = 0;
    private Comparable<T> comparable;
    private static final int DEFAULT_CAPACITY = 4; // default capacity

    MaxPQ()
    {
        this.heap = (T[]) new Comparable[DEFAULT_CAPACITY + 1];
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() { 
        return size;
    }

    @Override
    public void insert(T x) {  
        if (size >= 0.75*heap.length)
        {
            resize();
        }
        size++;
        heap[size] = x;
        swim(size);
    }

    //grow()
    private void resize() {
        T[] newHeap = (T[]) new Comparable[2*heap.length];

        // copy array (notice: in the priority queue, elements are located in the array slots with positions in [1, size])
        for (int i = 0; i <= size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    /**
     * Helper function to swap two elements in the heap
     *
     * @param i the first element's index
     * @param j the second element's index
     */
    private void swap(int i, int j) 
    {
        T tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void swim(int i) 
    {
        if (i==1)//heap[1]
        {
            return ;
        }
        while(i>1 && heap[i].compareTo(heap[i/2])>0)//compare with the father
        {
            swap(i/2,i);
            i=i/2;//i = parent
        }
    }

    @Override
    public T max() { //peek
        if (isEmpty())
            return null;
        return heap[1];//einai megistostrefhs
    }

    @Override
    public T getmax() {   
        T max = max();//returns heap[1]
        swap(1,size);
        size--;//delete the last element of the array(previous max)
        sink(1);
        return max;
    }

    private void sink(int i) 
    {    
        // determine left, right child
        int left = 2 * i;//8esh 2 me periexomeno to 5
        int right = left + 1;//8esh 3 me periexomeno to 10

        // if 2*i > size, node i is a leaf return
        if (left > size)
            return;

        // while haven't reached the leafs
        while (left <= size) {
        // Determine the largest child of node i
            int max = left;
            if (right <= size) {
                if (heap[left].compareTo(heap[right]) < 0)//find the most significant
                    max = right;
            }

            // If the heap condition holds, stop. Else swap and go on.
            // child smaller than parent
            if (heap[i].compareTo(heap[max]) > 0)
                return;
            else {
                swap(i, max);
                i = max;
                left = i * 2;
                right = left + 1;
            }
        }
    }
}