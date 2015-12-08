import com.bcgogo.utils.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/12
 * Time: 13:41.
 */
public class XmlTest {

    public static String readFile(String fileName) {
        String output = "";
        File file = new File(fileName);
        if(file.exists()){
            if(file.isFile()){
                try{
                    BufferedReader input = new BufferedReader (new FileReader(file));
                    StringBuffer buffer = new StringBuffer();
                    String text;
                    while((text = input.readLine()) != null)
                        buffer.append(text +"/n");
                    output = buffer.toString();
                }
                catch(IOException ioException){
                    System.err.println("File Error!");
                }
            }
            else if(file.isDirectory()){
                String[] dir = file.list();
                output += "Directory contents:/n";

                for(int i=0; i<dir.length; i++){
                    output += dir[i] +"/n";
                }
            }
        }
        else{
            System.err.println("Does not exist!");
        }
        return output;
    }
    public static void main (String args[]) throws IOException {
//        XNLTest xnlTest = new XNLTest();
//        URL base = xnlTest.getClass().getResource("");
//        System.out.println(System.getProperty("user.dir"));
////        D:\2S\api\src\test\resources\product.xml
//        String str = readFile( System.getProperty("user.dir")+"/api/src/test/resources/product.xml");
////        String result = XMLParser.getRootElement(str, "num");
//        System.out.print(str);

        try{
            File f=new File(System.getProperty("user.dir")+"/api/src/test/resources/product.xml");
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(f);
            NodeList nl = doc.getElementsByTagName("value");
            for (int i=0;i<nl.getLength();i++){
                System.out.println("车牌号码:" + doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue());
                System.out.println("车主地址:" + doc.getElementsByTagName("num").item(i).getFirstChild().getNodeValue());
                }
            }catch(Exception e){
            e.printStackTrace();
        }
    }
}
