public class LinkedList<T>//list of the processed tasks
{
    private Node head;
    private Node tail;

    LinkedList()
    {
        this.head = this.tail = null;
    }
    /**
     * Inserts the data at the front of the list     
     */
    public void insertAtFront(T data) {
        Node n = new Node(data);
        if (isEmpty())
        {
            head = n;
            tail = n;
        } 
        else 
        {
            n.setNext(head);
            head = n;
        }
    }

    void insertAtBack(T item)//item == Node(data) == Task
    {
        Node n = new Node(item);
        if (head == null)
        {
            head = tail = n;
        }
        else
        {
            tail.setNext(n);
            tail = n;
        }
    }

    public Node getHead()
    {
        return head;
    }

    public boolean isEmpty()
    {
        if(head == null)
            return true;
        return false;
    }

    public T removefromFront()
    {
        if (isEmpty())
            return null;

        T data = ((T)head.getData());

        if (head == tail)
            head = tail = null;
        else
            head = head.getNext();

        return data;
    }

    public T removeFromBack(){
        if (isEmpty())
            return null;

        T data = ((T)tail.getData());

        if (head == tail)
            head = tail = null;
        else {
            Node iterator = head;
            while (iterator.getNext() != tail)
                iterator = iterator.getNext();

            iterator.setNext(null);
            tail = iterator;
        }

        return data;
    }
}