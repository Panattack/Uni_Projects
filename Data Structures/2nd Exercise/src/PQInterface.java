public interface PQInterface<T> {

    public boolean isEmpty(); //check if the queue is empty

    public int size(); //return the number of active elements in the queue

    public void insert(T x); // insert the object x to the queue

    public T max(); /*return without removing the object with the highest priority */

    public T getmax();/* remove and return the object with the highest priority*/

}
