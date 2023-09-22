import java.io.PrintStream;
import java.util.NoSuchElementException;

public class StringStackImpl<T> implements StringStack<T>{
    
    private Node<T> head=null;
    private Node<T> tail=null;
    private int counter = 0; //gia ton idio logo me to IntQueueImpl

    @Override
    public boolean isEmpty() {
        return head==null;
    }

    @Override
    public void push(T item) {
        Node<T> n = new Node<>(item);
        if (isEmpty()){
            head = tail = n;
        }
        else{
            n.setNext(head);
            head = n;
        }
        counter++;
    }

    @Override
    public T pop() throws NoSuchElementException {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        T data = head.getData();
        if (head==tail){
            head=tail=null;
        }
        else{
            head = head.getNext();
        }
        counter--;
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
    public void printStack(PrintStream stream) {
        Node<T> node = head; 
        if(isEmpty()){
            stream.println("The stack is Empty");
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