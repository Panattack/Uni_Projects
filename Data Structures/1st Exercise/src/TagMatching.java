import java.io.*;

public class TagMatching {
    public static void main (String[] args){
        StringStackImpl<String> stackTags = new StringStackImpl<>();
        String line;
        try {
            boolean eof = false;//end of file,h kenh seira
            boolean ok = true;  
            FileReader reader = new FileReader(args[0]);
            BufferedReader buff = new BufferedReader(reader);
    
            while ((!eof) && ok) {
                line = buff.readLine();
                if (line == null)
                    eof = true;
                else { 
        
                    String[] chars = line.split("");  //o chars periexei ksexwrista enan enan tous xaraktires kathe grammis
                    
                    for (int i = 0; i <= chars.length-1; i++){
                        if (chars[i].equals("<")){  //vriskoume osous einai anamesa sto < kai > kai tous kanoume push stin stoiva
                            
                            int j;
                            for (j = i+1; !(chars[j].equals(">")); j++); // theloume apla na vroume ti thesi pou einai to >
                           
                            String tag="";
                            for (int k = i+1; k <= j-1; k++){ //den theloume na valoume to megalytero kai to mikrotero
                                tag+= chars[k];     
                            }
                            
                            if (tag.contains("/")){
                                tag = tag.replace("/","");
                                if (stackTags.isEmpty()){  //an i stoiva einai adeia kai eisagagoume tag kleisimatos einai sfalma!
                                    ok = false;
                                    break;
                                }
                                else if (tag.equals(stackTags.peek())){ //an i stoiva exei stoixeia kai to tag kleisimato einai idio me to tag tou head tote apla to afarioume
                                    stackTags.pop();
                                }
                                else { //an exoume 2 tag kleisimatos einai kai pali lathos stoixismena
                                    ok = false;
                                    break;
                                }
                            }
                            else{ //periptwsi tag anoigmatos
                                stackTags.push(tag);
                                
                            }
                            
                        }
                    }
                }
            }
          
            reader.close();

            if (ok){
                System.out.println("The html file's tags are placed correctly");
            }
            else{
                System.out.println("An error was found in the tags");
            }
        }
        catch (IOException ex) {
            System.out.println("Error reading file....");
        }
    }
}