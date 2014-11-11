
package queryautocomplete;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FeedingSysteam extends QueryAutoComplete
{
    private int Max;
    public FeedingSysteam(int max) 
    {
        super(max);
        Max=max;
    }
    
    public void setQueryLog(String location) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(location));
        
        String line;
        while ((line = reader.readLine()) != null) 
        {
            updateString(line);
        }
    }
    public void setInFile(String str) throws IOException
    {
        str=str+'\n';
        String newfile="UserInput.txt";
        File file = new File(newfile);
        FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
        try (BufferedWriter bw = new BufferedWriter(fw)) 
        {
            bw.write(str);
            bw.flush();
            bw.close();
        }
    }
}
