import java.io.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class ReadDocFile
{
    public static void main(String[] args)
    {
    	
        File syllabus = null;
        WordExtractor extractor = null;
        try
        {

            syllabus = new File("resources/syllabus S2018 revised-1.doc");
            FileInputStream in = new FileInputStream(syllabus.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(in);
            extractor = new WordExtractor(document);
            String[] fileData = extractor.getParagraphText();
            for (int i = 0; i < fileData.length; i++)
            {
                if (fileData[i] != null)
                    System.out.println(fileData[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}