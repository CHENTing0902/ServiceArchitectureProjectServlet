package fr.insa.soa.servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class BlindServlet
 */
public class BlindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BlindServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = "http://localhost:8080/TestRest/webapi/blind";
        Response resp = retrieve(url);
		System.out.println(resp.getStatusCode());
		System.out.println(resp.getRepresentation());
		parser(resp.getRepresentation());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public Response retrieve(String url) throws IOException {
		Response response = new Response();
		// Instantiate a new Client
		CloseableHttpClient client = HttpClients.createDefault();
		// Instantiate the correct Http Method
		HttpGet get = new HttpGet(url);
		// add headers
		get.addHeader("Accept", "application/xml");
		try {
			// send request
			CloseableHttpResponse reqResp = client.execute(get);
			response.setStatusCode(reqResp.getStatusLine().getStatusCode());
			response.setRepresentation(IOUtils.toString(reqResp.getEntity()
					.getContent(), "UTF-8"));
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			client.close();
		}
		
		return response;
	}

	public void parser(String xml) throws IOException {
		
		File file = new File("blind.xml");
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file);
		writer.write(xml);
		writer.flush();
		writer.close();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document document = db.parse("blind.xml");
//			all the elements of light
			NodeList lightlist = document.getElementsByTagName("blind");
			for (int i = 0; i < lightlist.getLength(); i++) {
//				parse each element of light
				Node light = (Node) lightlist.item(i);
				
//				search all the sub elements for light
				NodeList childList = light.getChildNodes();
				for (int j = 0; j < childList.getLength(); j++) {
					if(childList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						System.out.println(childList.item(j).getNodeName() + ": " + childList.item(j).getTextContent());
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
