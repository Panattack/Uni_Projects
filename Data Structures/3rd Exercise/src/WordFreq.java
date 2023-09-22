public class WordFreq
{
    
    private String word;
    private int numberOftimes;
    
    public String key()
    {
        return word;
    }
    public void setKey(String word)
    {
        this.word = word; 
    }
    public void addNumberOfTimes()
    {
        this.numberOftimes = this.numberOftimes+1;
    }
    public int getNumOfTimes()
    {
        return numberOftimes;
    }
    
    public String toString()
    {
        return "The word is: \t" + this.word + "\tand it's frequency is:\t" + this.numberOftimes;
    }
}