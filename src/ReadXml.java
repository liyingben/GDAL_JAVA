import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;

public class ReadXml {
     
	public  String getNodeValue(String xmlPath,String nodeRegex){
		File file=new File(xmlPath);
		if(!file.exists()){
			return null;
		}
		
		Document doc=null;
		SAXReader sax=new SAXReader();
		try {
			doc=sax.read(file);			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result=doc.selectSingleNode(nodeRegex).getText();
		
		return result;
	}
}
