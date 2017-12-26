import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String searchPath = "accessoires/huete-und-muetzen/";
        String keyword = "caps";
        String url = "https://www.aboutyou.de/frauen/" + searchPath + keyword;

        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div[class^='categoryTileWrapper_']");

            for (Element element : elements) {

                String brand = element.select("strong[class^='brand_']").first().text();
                String name = element.select("p[class^='name_']").first().text();
                System.out.println(brand + " - " + name);

                String href = element.select("a[class^='anchor_']").first().absUrl("href");
                Document subDoc = Jsoup.connect(href).get();
                String color = subDoc.select("div[class^='attributeWrapper_']").first().text();
                System.out.println("\t" + href);
                System.out.println("\t" + color);
                String number = subDoc.select("div[class^='container_iv4rb4']").first().text();
                System.out.println("\t" + number);
                String delievery = subDoc.select("div[class^='headline_1crhtoo']").text();
                System.out.println("\tdelievery:" + delievery);

                String finalPrice = element.select("div[class^='finalPrice_']").first().text();

                if (element.select("ul").size() > 0) {
                    for (Element listItems : element.select("ul").first().select("li")) {
                        System.out.println("\tprice was: " + listItems.select("span[class^='price_']").first().text());
                    }
                }

                List<String> attributes = new ArrayList<>();
                attributes.add(name);
                attributes.add(brand);
                attributes.add(color);
                attributes.add(number);
                attributes.add(delievery);
                attributes.add(finalPrice);
                System.out.println("\tfinal prece: " + finalPrice);
                saveToXML("output.txt", attributes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToXML(String xml, List<String> parameters) {
        org.w3c.dom.Document dom;
        org.w3c.dom.Element element = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element
            org.w3c.dom.Element rootEle = dom.createElement("offers");

            // create data elements and place them under root

            element = dom.createElement("offer");
            element.appendChild(dom.createTextNode("offer"));

            element = dom.createElement("name");
            element.appendChild(dom.createTextNode(parameters.get(0)));
            rootEle.appendChild(element);
            element = dom.createElement("brand");
            element.appendChild(dom.createTextNode(parameters.get(1)));
            rootEle.appendChild(element);

            element = dom.createElement("color");
            element.appendChild(dom.createTextNode(parameters.get(2)));
            rootEle.appendChild(element);

            element = dom.createElement("number");
            element.appendChild(dom.createTextNode(parameters.get(3)));
            rootEle.appendChild(element);

            element = dom.createElement("delievery");
            element.appendChild(dom.createTextNode(parameters.get(4)));
            rootEle.appendChild(element);

            element = dom.createElement("finalPrice");
            element.appendChild(dom.createTextNode(parameters.get(5)));
            rootEle.appendChild(element);

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }
}
