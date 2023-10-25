package hu.saxBC6X4X;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.File;
import java.io.IOException;
public class SaxBC6X4X {

	public static void main(String[] args) throws IOException, SAXException {
		try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SaxHandler handler = new SaxHandler();
            saxParser.parse(new File("IDA58U_kurzusfelvetel.xml"), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
		if(isValid("IDA58U_kurzusfelvetel.xml","IDA58U_kurzusfelvetel.xsd")){
			System.out.println("XSD Validation successful");
		}else{
			System.out.println("Unsuccessful validation");
		}
		
	}
	private static Validator initValidator(String xsdPath) throws SAXException {
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    Source schemaFile = new StreamSource(xsdPath);
	    Schema schema = factory.newSchema(schemaFile);
	    return schema.newValidator();
	}

	
	public static boolean isValid(String xmlName,String xsdname) throws IOException, SAXException {
	    Validator validator = initValidator(xsdname);
	    try {
	        validator.validate(new StreamSource(xmlName));
	        return true;
	    } catch (SAXException e) {
	        return false;
	    }
	}
}


class SaxHandler extends DefaultHandler {
  private int spaces = 0;

  private String formatAttributes(Attributes attributes) {
      int attrLength = attributes.getLength();
      if(attrLength == 0) return "";
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < attrLength; i++) {
          sb.append(attributes.getLocalName(i) + "=" + attributes.getValue(i));
          if(i < attrLength - 1) sb.append(", ");
      }
      return ", {"+sb.toString()+"}";
  }



  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
      spaces++;
      for (int i = 0; i < spaces; i++) System.out.print("  ");
      System.out.println(qName + formatAttributes(attributes) + " start");
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
	  for (int i = 0; i < spaces; i++) System.out.print("  ");
      spaces--;
      System.out.println(qName + " end");
  }

  @Override
  public void characters(char ch[], int start, int length) {
      String chars = new String(ch, start, length).trim();
      if(chars.isEmpty()) return;
      for (int i = 0; i < spaces+1; i++) System.out.print("  ");
      System.out.println(chars);
  }
}