import java.io.PrintStream;
import java.util.NoSuchElementException;

public class IntQueueImpl<T> implements IntQueue<T> {
    private Node<T> head = null;
    private Node<T> tail = null;
    private int counter = 0; // Vazoume counter o opoios auksanetai otan vazoume ena stoixeio stin oura kai meiwnetai otan to afairoume etsi wste o xronos na parameinei O(1)

    @Override
    public boolean isEmpty() {
        return head==null;
    }

    @Override
    public void put(T item) {
        Node<T> a = new Node<>(item);
        if (isEmpty()){
            head = tail = a;
        }
        else{
            tail.setNext(a);
            tail = a;
        }
        counter ++;
        
    }

    @Override
    public T get() throws NoSuchElementException {
        
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        T data = head.getData();
        if (head==tail){
            head = tail=null;
        }
        else{
            head = head.getNext();
        }
        counter --;
        return data;
    }

    @Override
    public T peek() throws NoSuchElementException {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        return head.getData();
    }

    @Override
    public void printQueue(PrintStream stream) {
        Node <T> node = head;
        if(isEmpty()){
            stream.println("The queue is Empty");
        }
        else{
            while(node != null){
            stream.println(node.getData());
            node = node.getNext();
            }
        }
    }

    @Override
    public int size() {
        return counter;
    }
}
