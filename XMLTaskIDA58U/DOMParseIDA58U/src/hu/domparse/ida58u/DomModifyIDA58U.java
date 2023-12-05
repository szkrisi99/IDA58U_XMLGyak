package hu.domparse.ida58u;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

public class DomModifyIDA58U {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
    XPathExpressionException, DOMException, ParseException {
		
		File xml = new File("C:\\Egyetem\\XML\\IDA58U_XML_gyak\\XMLTaskIDA58U\\DOMParseIDA58U\\bin\\hu\\domparse\\ida58u\\XMLIDA58U.xml");
		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM document módosítása
        DomModifier.modifyDom(document);

        // DOM document átalakatísa DOM DocumentTraversal formába
        DocumentTraversal traversal = (DocumentTraversal) document;

        //TreeWalker inicializálása
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        //DOM bejárása
        DomTraverser.traverseLevel(walker, "");

	}
	
    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.) Kiss Imre nevenek megváltoztatása Nagy Imrére
            Node tulajdonos = (Node) xpath.evaluate("//Tulajdonos[./Nev='Kiss Imre']",
                    document, XPathConstants.NODE);

            tulajdonos.setTextContent("Nagy Imre");

            // 2.) Minden 3000 forintnál drágább ételnek az ára csökken 10%-al
            NodeList etelek = (NodeList) xpath.evaluate("//Etel[./Ar>3000]/Ar", document, XPathConstants.NODESET);
            System.out.println(etelek);
            for (int i = 0; i < etelek.getLength(); i++) {
                Node etel = etelek.item(i);

                double price = Double.parseDouble(etel.getTextContent());
                etel.setTextContent(Double.toString(price * 0.9));
                
            }
        }
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            //AktuÁlis csomópont
            Node node = walker.getCurrentNode();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // Rekurzívan meghívjuk a bejárást a DOM fában
            for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
                traverseLevel(walker, indent + "    ");
            }

            walker.setCurrentNode(node);
        }

        private static void printElementNode(Node node, String indent) {
            System.out.print(indent + node.getNodeName());

            printElementAttributes(node.getAttributes());
        }

        private static void printElementAttributes(NamedNodeMap attributes) {
            int length = attributes.getLength();

            if (length > 0) {
                System.out.print(" [ ");

                for (int i = 0; i < length; i++) {
                    Node attribute = attributes.item(i);

                    System.out.printf("%s=%s%s", attribute.getNodeName(), attribute.getNodeValue(),
                            i != length - 1 ? ", " : "");
                }

                System.out.println(" ]");
            } else {
                System.out.println();
            }
        }

        private static void printTextNode(Node node, String indent) {
            String content_trimmed = node.getTextContent().trim();

            if (content_trimmed.length() > 0) {
                System.out.print(indent);
                System.out.printf("{ %s }%n", content_trimmed);
            }
        }
    }

}