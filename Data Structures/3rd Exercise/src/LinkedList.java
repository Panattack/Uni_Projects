public class LinkedList<T>
{
    private Node<T> head;
    private Node<T> tail;

    LinkedList()
    {
        this.head = this.tail = null;
    }

    /**
     * Inserts the data at the front of the list     
     */
    
    public void insertAtFront(T data) {
        Node<T> n = new Node<>(data);

        if (isEmpty()) {
            head = n;
            tail = n;
        } else {
            n.setNext(head);
            head = n;
        }
    }

    void insertAtBack(T item)
    {
        Node<T> n = new Node<T>(item);
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

    public Node<T> getHead()
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
            Node<T> iterator = head;
            while (iterator.getNext() != tail)
                iterator = iterator.getNext();

            iterator.setNext(null);
            tail = iterator;
        }

        return data;
    }

    public void remove(T data)
    {
        if (isEmpty())
            return ;
        else
        {
            Node<T> current = head;
            if (current.getData().equals(data))
            {
                this.head = head.getNext();
                return ;
            }
            while (current.getNext() != null)
            {
                if (current.getNext().getData().equals(data))
                {  
                    if (current.getNext() == tail)
                    {                    
                        tail = current;
                        current.setNext(null);                
                    }                
                    else                
                    {                    
                        current.setNext(current.getNext().getNext());                
                    }
                    return ;
                }
                current = current.getNext();
            }
        }
    }
    public boolean search(T data)
    {
        
        Node <T> current = head;
        while (current != null)
        {
            if (data.equals(current.getData()))
            {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
}