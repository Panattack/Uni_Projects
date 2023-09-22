import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;
/************************************************************************************************************
 * Words.txt includes a file that we wrote and Words2.txt the file that was given in the project
 * before you load the txt, you may add the stopwords you want. If you remove a stopword you have to load the file 
 * again in the same or another BST tree  
 * Here we have a main and we execute all the methods.
 */

public class BST implements WordCounter
{
    public static void main (String[] args)
    {
        
        BST t = new BST(); 
        t.addStopWord("The");   //exists
        t.addStopWord("aaaaaa");    //doesnt exist
        System.out.println("The txt is: \n");
        t.load("Words.txt");
        System.out.println("\n");
        System.out.println("StopWord:\t\"THE\"\texists in the txt.");
        System.out.println("StopWord:\t\"aaaaaa\"\tdoes not exist in the txt.");
        System.out.println("\n");
        System.out.println("---------------------------------------------------------------------------------");
        t.printTreeAlphabetically(System.out);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Adding a stopword and loading a new file in the same BST...");
        System.out.println("StopWord:\t\"YOU\"\texists in the txt.");
        System.out.println("\n");
        t.addStopWord("you");   //exists
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("The txt is: \n");
        t.load("Words2.txt");
        System.out.println("\n");
        System.out.println("The BST is: \n");
        System.out.println("---------------------------------------------------------------------------------");
        t.printTreeAlphabetically(System.out);
        System.out.println("\n");
        System.out.println("----------------------------Creating a new BST------------------------------------");
        BST t1 = new BST();
        t1.addStopWord("even");
        t1.removeStopWord("even");
        t1.load("Words.txt");

        System.out.println("---------------------------------------------------------------------------------");

        t1.printTreeAlphabetically(System.out);
        
        System.out.println("\n"+"Distinct words:\t"+t1.getDistinctWords()+"\n");
        System.out.println("Total Words:\t"+t1.getTotalWords()+"\n");
        System.out.println("Mean Frequency:\t"+t1.getMeanFrequency()+"\n");
        System.out.println("Maximum Frequency:\t"+t1.getMaximumFrequency()+"\n");
        t1.remove("shame");  //exists
        t1.remove("blabalbalba");    //doesnt exist
        System.out.println("Searching w:\t"+t1.search("w")+"\n");  //doesnt exist
        System.out.println("Searching even:\t"+t1.search("even")+"\n");   //exists
        System.out.println("word \"a\" has frequency \t"+t1.getFrequency("a"));
        System.out.println("---------------------------------------------------------------------------------");
        t1.printTreeByFrequency(System.out);
    }
    private TreeNode head = null; //root of the tree
    private LinkedList<String> stopWords = new LinkedList<>(); // list of stopwords
    private  WordFreq[] array;

    private class TreeNode implements Comparable<String> 
    {
        WordFreq item = new WordFreq();

        TreeNode left; // pointer to left subtree
        TreeNode right; // pointer to right subtree
        int subtreeSize; //number of nodes in subtree starting at this node
        TreeNode parent;

        TreeNode(String w)
        {
            item.setKey(w);
        }

        public WordFreq getItem() 
        {
            return item;
        }

        public TreeNode getLeft() {
            return left;
        }

        public void setLeft(TreeNode left) {
            this.left = left;
        }
        public TreeNode getRight() {
            return right;
        }

        public TreeNode getParent()
        {
            return parent;
        }

        public void setRight(TreeNode right) {
            this.right = right;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }
        public void setSubtreeSize(int subtreeSize) {
            this.subtreeSize = subtreeSize;
        }
        public int getSubtreeSize() {
            return subtreeSize;
        }
        @Override
        public int compareTo(String o) {
            if (this.item.key().compareTo(o)>0)
            {
                return 1;   //the string of this object is bigger than the given string
            }
            else if(this.item.key().compareTo(o)<0)
            {
                return -1;  //the string of this object is smaller than the given string
            }
            return 0;   //equals the two string values
        }
        public void setItem(WordFreq item) {
            this.item = item;
        }
    };

    @Override
    public void insert(String w)
    {
        if(head==null)
        {
            head = new TreeNode(w);
            head.setParent(null);
        }

        TreeNode current = head;

        while(true)
        {
            if(current.getItem().key().equals(w)) //case of existing word
            {
                current.getItem().addNumberOfTimes(); //If the word exists, we increase its frequency.
                return;
            }

            if (current.compareTo(w) > 0)
            {   //Left SubTree

                if (current.getLeft()==null)
                {
                    TreeNode newNode = new TreeNode(w);
                    current.setLeft(newNode);
                    newNode.setParent(current);
                    current.getLeft().getItem().addNumberOfTimes(); 
                    
                    //Updating subtreeSize for every passed node.
                    TreeNode curr = head;
                    while(!(curr.getItem().key().equals(current.getLeft().getItem().key())))
                    {
                        curr.subtreeSize++;
                        if(curr.compareTo(current.getLeft().getItem().key()) > 0)
                        {
                            curr=curr.getLeft();
                        }
                        else
                        {
                            curr=curr.getRight();
                        }
                    }
                    return;
                }
                else
                {
                    current = current.getLeft();
                }
            }
            else 
            {
                if (current.getRight()==null)
                {
                    TreeNode newNode = new TreeNode(w);
                    current.setRight(newNode); //Create Node 
                    newNode.setParent(current);
                    current.getRight().getItem().addNumberOfTimes(); //add word's counter
                                                            
                    //Updating subtreeSize for every passed node.
                    TreeNode curr = head;
                    while(!(curr.getItem().key().equals(current.getRight().getItem().key())))
                    {
                        curr.subtreeSize++;
                        if(curr.compareTo(current.getRight().getItem().key()) > 0)
                        {
                            curr=curr.getLeft();
                        }
                        else
                        {
                            curr=curr.getRight();
                        }
                    }
                    return;
                }
                else
                {
                    current = current.getRight();
                }
            }   
        }
    }

    @Override
    public WordFreq search(String w) 
    {
        w=w.toUpperCase();
        TreeNode current = head;
        while (true) 
        {
            if (current == null)
                return null;

            if (current.getItem().key().equals(w))
            {
                double frequency = getMeanFrequency();
                if(current.getItem().getNumOfTimes() > frequency)
                {
                    remove(current.getItem().key());
                    insertAtRoot(head, current.getItem(), null);
                }
                return current.getItem();
            }

            if (current.getItem().key().compareTo(w) < 0)
                current = current.getRight();
            else
                current = current.getLeft();
        }
    }
    
    private TreeNode insertAtRoot(TreeNode root, WordFreq item, TreeNode parent)
    {
        if (root == null) 
        {
            TreeNode node = new TreeNode(item.key());
            node.setItem(item);
            node.setParent(parent);
            node.setSubtreeSize(0);
            return node;
        }
    	int result = item.key().compareTo(root.getItem().key()); 
    	if(result<0)
        {
            root.setSubtreeSize(root.getSubtreeSize()+1);
            TreeNode leftSubTreeRoot = this.insertAtRoot(root.getLeft(), item, root);   
            root.setLeft(leftSubTreeRoot);
            root = this.rotateRight(root);
        }
        else 
        {
            root.setSubtreeSize(root.getSubtreeSize()+1);
            TreeNode rightSubTreeSize = this.insertAtRoot(root.getRight(), item, root);
            root.setRight(rightSubTreeSize);
            root = this.rotateLeft(root);
        }
        //after insertion at the BST's root, return the updated BST
        return root;
    }
    
    private TreeNode rotateRight(TreeNode pivot) 
    {
        TreeNode parent = pivot.getParent();
        TreeNode child = pivot.getLeft();
        
        //Updating SubTreeSize...       
        int n = pivot.getSubtreeSize();     //if pivot is root, child's subtreesize = pivot's subtreeSize.
        child.setSubtreeSize(n);        
        if (pivot.getLeft().getRight()!=null && pivot.getRight() != null) //if there is right child of the initial child
        {
            pivot.setSubtreeSize(pivot.getLeft().getRight().getSubtreeSize()+1+pivot.getRight().getSubtreeSize()+1);
        }
        else if(pivot.getLeft().getRight()==null && pivot.getRight() != null)
        {
            pivot.setSubtreeSize(pivot.getRight().getSubtreeSize()+1);
        }
        else if(pivot.getLeft().getRight()!=null && pivot.getRight() == null)
        {
            pivot.setSubtreeSize(pivot.getLeft().getRight().getSubtreeSize()+1);
        }
        else if (pivot.getRight() == null && pivot.getLeft().getRight()==null)
        {
            pivot.setSubtreeSize(0); 
        }
        //until here...
        
        if (parent == null)
        {
            head = child;
        }
        else if(parent.getLeft() == pivot) 
        {   
            parent.setLeft(child);
        }
        else
        {
            parent.setRight(child);
        }
        
        child.setParent(pivot.getParent()); 
        pivot.setParent(child);
        pivot.setLeft(child.getRight());

        if(child.getRight() != null)
        {
            child.getRight().setParent(pivot);
        }
        
        child.setRight(pivot);  
        return child;    
    }
    
    private TreeNode rotateLeft(TreeNode pivot)
    {
        TreeNode parent = pivot.getParent();
        TreeNode child = pivot.getRight(); 
        
        //Updating SubTreeSize...
        int n = pivot.getSubtreeSize(); 
        child.setSubtreeSize(n);
        if (pivot.getRight().getLeft()!=null && pivot.getLeft() != null) 
        {
            pivot.setSubtreeSize(pivot.getRight().getLeft().getSubtreeSize()+1+pivot.getLeft().getSubtreeSize()+1);
        }
        else if(pivot.getLeft() == null && pivot.getRight().getLeft() != null)
        {
            pivot.setSubtreeSize(pivot.getRight().getLeft().getSubtreeSize()+1);
        }
        else if(pivot.getLeft() != null && pivot.getRight().getLeft() == null)
        {
            pivot.setSubtreeSize(pivot.getLeft().getSubtreeSize()+1);
        }
        else if (pivot.getLeft() == null && pivot.getRight().getLeft() == null)
        {
            pivot.setSubtreeSize(0);   //case of leave
        }  
        //until here...

        if (parent == null)
        {
            head = child;
        }
        else if(parent.getLeft()==pivot) 
        {   
            parent.setLeft(child);
        }
        else
        {
            parent.setRight(child);
        }
        child.setParent(pivot.getParent());
        pivot.setParent(child);
        pivot.setRight(child.getLeft());
        if(child.getLeft() != null)
        {
            child.getLeft().setParent(pivot);
        }
        child.setLeft(pivot);
        return child;
    }
    @Override
    public void remove(String w) 
    {
        w = w.toUpperCase();
        
        TreeNode current = head;
        TreeNode parent = null;
        boolean flag2kids = false;

        //Searching if the word exists
        while (true) 
        {
            if (current == null)
                return;

            if (current.getItem().key().equals(w))
                break;
            if (current.compareTo(w) < 0)
                current = current.getRight();
            else
                current = current.getLeft();
        }

        current = head;
        parent = null;

        while (true) 
        {
            if (current.getItem().key().equals(w))
                break;

            parent = current;

            //updating subtreesize..
            parent.setSubtreeSize(parent.getSubtreeSize()-1);

            if (current.compareTo(w) < 0)
                current = current.getRight();
            else
                current = current.getLeft();
        }

        // node to replace with
        TreeNode replace = null;

        // only right child exists
        if (current.getLeft() == null)
        {
            replace = current.getRight();
        }
        else if (current.getRight() == null)
        {
            replace = current.getLeft();
        }
        else 
        {
            flag2kids = true;
            // find left most child of current right subtree!
            TreeNode parentfindCurrent = current;
            TreeNode findCurrent = current.getRight();
            while (true)
            {
                if (findCurrent.getLeft() != null)
                {
                    parentfindCurrent = findCurrent;
                    findCurrent.setSubtreeSize(findCurrent.getSubtreeSize()-1);
                    findCurrent = findCurrent.getLeft();
                }
                else
                    break;
            }
            TreeNode replaced = null;

            // only right
            if (findCurrent.getRight() == null)
                replaced = findCurrent.getLeft();
            
            if (parentfindCurrent.getLeft() == findCurrent)
            {
                parentfindCurrent.setLeft(replaced);
            }

            //Updating subtreesize 
            findCurrent.setSubtreeSize(current.getSubtreeSize()-1);
            
            findCurrent.setLeft(current.getLeft());
            findCurrent.setRight(current.getRight());
            findCurrent.setParent(current.getParent());
            
            //deleting current
            current.setLeft(null);
            current.setRight(null);
            current.setParent(null);
            
            //parent's child must be findCurrent
            if (parent != null)
            {
                if (parent.getLeft() == current)
                {
                    parent.setLeft(findCurrent);
                }
                else
                {
                    parent.setRight(findCurrent);
                }
            }
            else //periptvsh pou h riza exei dyo paidia kai 8eloume na uesoume ton mikrotero apo dejia me to head == root 
            {
                head = findCurrent;
            }
        }

        // replace parent's reference
        if (parent == null && !flag2kids) //den eixe dyo paidia,htan kai riza
        { //root
            head = replace;
        }
        else if (!flag2kids && parent != null)//edw kanei apla replace an o diagrafomenos komvos den exei dyo paidia
        {
            if (parent.getLeft() == current)
            {
                parent.setLeft(replace);
            }
            if (parent.getRight() == current)
            {
                parent.setRight(replace);
            }
        }
    }

    @Override
    public void load(String filename)
    {
        try
        {
            BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            StringTokenizer lineTokens;
            String token;
            String line;
            boolean eof = false;    //end of file
            if(this.head!=null)     //if the BST is not empty we remove any added stopword.
            {
                if (this.stopWords != null)
                {
                    Node<String> current = stopWords.getHead();
                    while (current != null)//removing the stopwords from BST
                    {
                        if(this.search((String)current.getData()) != null)  //downcasting, if the stopword exists in the BST, then we remove it
                            this.remove((String)current.getData());
                        current = current.getNext();
                    }
                }
            }
            

            String[] acceptedChars = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "\'"};
            while (!eof) 
            {
                line = buff.readLine();

                if (line == null)
                    eof = true;
                else 
                {   
                    System.out.println(line);
                    line = line.toUpperCase(); 
                    lineTokens = new StringTokenizer(line);
                    int size = lineTokens.countTokens();
                    for(int i =0; i<size;i++)   
                    {
                        token = lineTokens.nextToken(); 
                        String treeWord = checkWords(acceptedChars, token); // treeWord is the new valid word
                        //System.out.println( treeWord);//this.stopWords.search(treeWord));
                        if ( treeWord != null && !treeWord.equals("") && !this.stopWords.search(treeWord))   
                        {
                            this.insert(treeWord);
                        }
                    }  
                }
            }
            buff.close();
        }
        catch (IOException ex) 
        {
            System.out.println("Error reading file....");
        }
    }

    private static String checkWords(String[] array, String v)
    {   /*returns the new word if the word is valid, null otherwise. */
 
        int size = v.length();
        
        String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        if(contains(numbers, v))
        {
            return null;
        }
        for(int i = 0; i< size; i++) 
        {
            if(!contains(array, String.valueOf(v.charAt(i))))
            {
                v = v.replace(String.valueOf(v.charAt(i)), " ") ;
            }
            else    //case of first accepted char
            {
                break;
            }
        }
       
        for(int i = size-1; i >= 0; i--)
        {
            if(!contains(array, String.valueOf(v.charAt(i))))
            {
               v = v.replace(String.valueOf(v.charAt(i)), " ") ;
            }
            else    //case of last accepted char
            {
                break;
            }
        }
        
        String str = v.replace(" ", "");
        
        size = str.length();
        for(int i = 0; i < size; i++)
        {
            if(!contains(array, String.valueOf(str.charAt(i))))
            {
                return null;
            }
        }
        return str;
    }


    private static boolean contains(String[] array, String v)
    {
        for(String str : array) 
        {
            for(int i = 0; i<v.length();i++)
            {
                if (str.equals(String.valueOf(v.charAt(i))))
                {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public int getTotalWords() 
    { 
        return this.traverse(head, 0);
        
    }
    private int traverse(TreeNode node, int sum) 
    {
        //returns the Number of the words in the BST

        TreeNodeQueueImpl<TreeNode> queue = new TreeNodeQueueImpl<>();
        queue.put(node);
        while (!queue.isEmpty()) 
        {
            node = queue.get();
            sum += node.getItem().getNumOfTimes();
            if (node.getLeft() != null)
            {
                queue.put(node.getLeft());
            }
            if (node.getRight() != null)
            {
                queue.put(node.getRight()); 
            }
        }
        return sum;
    }
    @Override
    public int getDistinctWords() 
    {     //returns the number of different words
        return this.head.subtreeSize + 1;
    }
    @Override
    public int getFrequency(String w) 
    {
        w=w.toUpperCase();
        TreeNode current = head;

        while (true) 
        {
            if (current == null)
                return 0;

            if (current.getItem().key().equals(w))
            {
                return current.getItem().getNumOfTimes();
            }

            if (current.getItem().key().compareTo(w) < 0)
                current = current.getRight();
            else
                current = current.getLeft();
        }
    }
    @Override
    public WordFreq getMaximumFrequency() 
    {
        baseQuicksort();    
        return array[array.length-1];
    }

    @Override
    public double getMeanFrequency() {
        int sum =0;
        int n = head.subtreeSize + 1;
        sum = traverse(head, 0); 

        return ((float)sum/n);
    }
   
    

    @Override
    public void addStopWord(String w) {
        w=w.toUpperCase();
        this.stopWords.insertAtBack(w);
    }

    @Override
    public void removeStopWord(String w) {
        w=w.toUpperCase();
        this.stopWords.remove(w);
    }

    @Override
    public void printTreeAlphabetically(PrintStream stream) {
        inOrder(head, stream);
    }

    private void inOrder(TreeNode n,  PrintStream stream) {
        if (n == null) return;
        inOrder(n.getLeft(), stream);

        String str = n.getSubtreeSize()>=10?"\t":"\t\t"; //for tabs
        
        stream.println("The word is: \t"+n.getItem().key()+"\t SubTreeSize: "+n.subtreeSize + str+" Number of times: " + n.getItem().getNumOfTimes());
        
        inOrder(n.getRight(), stream);
    }

    @Override
    public void printTreeByFrequency(PrintStream stream) 
    {   
        baseQuicksort();

        for (int i = 0; i < array.length; i++)
        {
            stream.println("The word is:\t"+array[i].key()+"\tand it's frequency is:\t"+array[i].getNumOfTimes());
        }
       
    }
    private void baseQuicksort()
    {
        array = new WordFreq[head.subtreeSize+1];
        TreeNode node;
        int k =0;
        
        TreeNodeQueueImpl<TreeNode> queue = new TreeNodeQueueImpl<>();
        queue.put(head);
        while (!queue.isEmpty()) 
        {     
            node = queue.get();
            array[k] = node.getItem();
            k++;

            if (node.getLeft() != null)
            {
                queue.put(node.getLeft());
            }
            if (node.getRight() != null) 
            {
                queue.put(node.getRight()); 
            }
        }
        array = quickSort(array, 0, array.length - 1);
    }

    private WordFreq[] quickSort(WordFreq[] array, int left, int right)
    {
        if(right <= left)
        {
            return null;
        }
        int i = partition(array,left,right);
        quickSort(array, left, i - 1);
        quickSort(array, i+1, right);
        return array;
    }
    private int partition(WordFreq a[], int p, int r)
    { 
        int i = p-1, j = r; WordFreq v = a[r];
        for (;;)
        { 
            while (less(a[++i], v)) ;
            while (less(v, a[--j]))
                if (j == p) break;
            if (i >= j) break;
            exch(a, i, j);
        }
        exch(a, i, r);
        return i; 
    }
    private void exch(WordFreq[] array, int i, int j)
    {
        WordFreq temp = array[j];
        array[j] = array[i];
        array[i] = temp;
    }
    private boolean less(WordFreq p,WordFreq s)
    {
        if (p.getNumOfTimes() < s.getNumOfTimes())
            return true;
        else
        {
            return false;
        }
    }
}