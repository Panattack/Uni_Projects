import java.io.*;
import java.util.*;
import java.lang.Math;

public class NetBenefit {
    public static void main(String[] args) throws FileNotFoundException{
        IntQueueImpl<Integer> queue = new IntQueueImpl<>();
        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        try {
            String line;
            StringTokenizer lineTokens;
            String token;
            
            boolean eof = false;//end of file,h kenh seira
            int benefit = 0;

            while (!eof){    
                line = buff.readLine();
                if (line==null){
                    eof = true;
                }
                else{
                    lineTokens = new StringTokenizer(line); 
                    token = lineTokens.nextToken();
                        
                    if (token.equals("buy")){ //periptwsi "buy"
                        token = lineTokens.nextToken();
                        int sum = Integer.parseInt(token); //sum einai oi mextoxes
                        token = lineTokens.nextToken();
                        token = lineTokens.nextToken();
                        int price = Integer.parseInt(token); //oi timi tis mias metoxis
                        for (int i = 0; i < sum; i++){
                            queue.put(price);
                        }
                    }
                    else{ //periptwsi "sell"
                        token = lineTokens.nextToken();
                        int sum = Integer.parseInt(token); //sum einai to poses mextoxes tha poulisoume
                        if (sum > queue.size()){  // an den arkoun oi metoxes pros pwlisi
                            throw new Exception();
                        }
                        else{
                            token = lineTokens.nextToken();
                            token = lineTokens.nextToken();
                            int price = Integer.parseInt(token);
                            for (int i = 0; i < sum; i++){
                                benefit += price - queue.get();
                            }
                        }
                    }
                }
            }
            if (benefit < 0)
            {
                System.out.println("There is a damage of "+Math.abs(benefit)+" euros");
            }
            else {
            System.out.println("There is a profit of "+benefit+" euros");
            }
            buff.close();
        }
        catch (IOException ex) {
            System.out.println("Error reading file....");
        }
        catch (NoSuchElementException ex){
            System.out.println("Please remove the unnecessary last space lines from the txt file");
        }
        catch (Exception ex) {
            System.out.println("Attempt to sell more shares than existings");
        }    
    }
}