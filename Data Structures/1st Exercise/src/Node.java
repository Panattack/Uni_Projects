public class Node<T> {
   
    private T data;
    private Node<T> next;

    /**
     * Constructor. Sets data
     *
     * @param data the data stored
     * @return
     */
    Node(T data) {
        this.data = data;
        this.next=null;
    }

    /**
     * Returns this node's data
     *
     * @return the reference to node's data
     */
    public T getData() {
        // return data stored in this node
        return data;
    }

    /**
     * Get reference to next node
     *
     * @return the next node
     */
    public Node<T> getNext() {
        // get next node
        return this.next;
    }

    /**
     * Set reference to next node
     *
     * @param next reference
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }
}

