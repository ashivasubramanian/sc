/**
 * This package contains a single singleton class (the DataAccess class) that provides
 * methods to access and modify XML files.
 */
package game_engine.data_access;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The only class of the DataAccess package, the DataAccess class provides methods to
 * manipulate XML documents. This class is a singleton class and hence an instance of
 * this class may only be obtained using the <code>getInstance()</code> method.
 */
public final class DataAccess
{
	/**
	 * Contains an instance of this class. This is the instance that is returned
	 * when an instance of the class is requested.
	 */
	private static DataAccess Instance = new DataAccess();

	/**
	 * Constructor made private to disable default constructor provided by Java. Does
	 * nothing.
	 */
	private DataAccess(){}

	/**
	 * Returns an instance of this class.
	 *
	 * @return an instance of <code>DataAccess</code> class.
	 */
	public static DataAccess getInstance()
	{
		return Instance;
	}

	/**
	 * Extracts the required data from the specified XML file.
	 * You can extract data in four ways:
	 * <ul>
	 *  <li>By specifying only <code>sElement</code>, you get the full data set.
	 *  </li>
	 *  <li>By specifying <code>sElement</code> using the syntax <code>
	 *  element[@attribute=value]</code>, you get a <code>Vector</code> of all 
	 *  those <code>Element</code>s that match the condition.</li>
	 *  <li>By specifying <code>sElement</code> using the syntax <code>
	 *  element.contains(@attribute,value)</code>, you get a <code>Vector</code>
	 *  of all those <code>Element</code>s that contain <code>value</code>.</li>
	 * </ul>
	 *
	 * @param streamToXMLFile The XML file from which data has to be extracted.
	 * @param sElement The element of the XML from which data has to be extracted
	 *
	 * @return A <code>Vector</code> that contains the required data.
	 */
	public Vector<Element> extractData(InputStream streamToXMLFile,
			String sElement) throws IOException, 
			ParserConfigurationException, SAXException	{
		Vector<Element> objVector;
		Document objDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(streamToXMLFile);
		int iCounter = 0;
		objVector = new Vector<>();
		String element;
		if(sElement.indexOf("[") > 0)
			element = sElement.substring(0, sElement.indexOf("["));
		else if(sElement.indexOf(".") > 0)
			element = sElement.substring(0, sElement.indexOf("."));
		else
			element = sElement;
		while( iCounter < objDocument.getElementsByTagName(element).getLength())
		{
			objVector.add( ((Element)objDocument.getElementsByTagName(element).item(iCounter)));
			iCounter++;
		}
		if(sElement.indexOf("[") > 0)
		{
			String attribute = sElement.substring(sElement.indexOf("@") + 1, sElement.indexOf("="));
			String value = sElement.substring(sElement.indexOf("=") + 1, sElement.indexOf("]"));
			iCounter = 1;
			Vector<Element> objData = new Vector<>();
			while(iCounter <= objVector.size())
			{
				Element objElement = objVector.get(iCounter-1);
				if(objElement.getAttribute(attribute).equals(value))
					objData.add(objElement);
				iCounter++;
			}
			return objData;
		}
		if(sElement.indexOf(".") > 0)
		{
			String attribute = sElement.substring(sElement.indexOf("@") + 1, sElement.indexOf(","));
			String value = sElement.substring(sElement.indexOf(",") + 1, sElement.indexOf(")"));
			iCounter = 1;
			Vector<Element> objData = new Vector<>();
			while(iCounter < objVector.size())
			{
				Element objElement = objVector.get(iCounter-1);
				if(objElement.getAttribute(attribute).indexOf(value) >= 0)
					objData.add(objElement);
				iCounter++;
			}
			return objData;
		}
		return objVector;
	}
	
    public Vector<Element> extractData(String srelativePathToFileName, String element)
	    throws IOException, ParserConfigurationException, SAXException {
	FileInputStream file = new FileInputStream(srelativePathToFileName);
	return extractData(file, element);
    }

	/**
	 * Inserts the specified data into the specified XML file.
	 * The method opens the specified XML file, looks for the last occurrence of the specified element, and adds the new element after the last occurrence.
	 * <br>
	 * For example, if your XML document contains the following XML tree,
	 * <br><center>
	 * &lt;users&gt;&lt;user/&gt;&lt;/users&gt;,
	 * </center>
	 * then calling this method with sElement "&lt;users&gt;&lt;user/&gt;&lt;/users&gt;"
	 * will result in &lt;users&gt;&lt;user/&gt;&lt;user/&gt;&lt;/users&gt;.
	 *
	 * @param sRelativePathToXMLFile The XML file into which data is to be inserted.
	 * @param sElement The element of the XML into which data is to be inserted
	 * @param sAttributes The attribute(s) of sElement
	 * @param sValues The value(s) to the attributes
	 *
	 * @return Returns <code>true</code> if the operation was a success
	 */
	public boolean insertData(String sRelativePathToXMLFile, String sElement, String[] sAttributes, String[] sValues)
	{
		File objFile = new File(sRelativePathToXMLFile);
		try
		{
			Document objDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(objFile);
			Element objElement = objDocument.createElement(sElement);
			for( int i = 0; i < sAttributes.length; i++)
			{
				objElement.setAttribute(sAttributes[i],sValues[i]);
			}
			if(objDocument.getElementsByTagName(sElement).getLength() > 0)
			{
				objDocument.getElementsByTagName(sElement).item(objDocument.getElementsByTagName(sElement).getLength()-1).getParentNode().appendChild(objElement);
			}
			else
			{
				objDocument.getDocumentElement().appendChild(objElement);
			}
			Transformer objTransformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
			DOMSource objSource = new DOMSource(objDocument);
			StreamResult objResult = new StreamResult( new FileOutputStream(objFile));
			objTransformer.transform(objSource,objResult);
			objResult.getOutputStream().flush();
			return true;
		}
		catch(ParserConfigurationException objParserConfigurationException)
		{
			System.out.println( "ParserConfigurationException: " + objParserConfigurationException.getMessage());
		}
		catch(IOException objIOException)
		{
			System.out.println( "IOException: " + objIOException.getMessage());
		}
		catch(SAXException objSAXException)
		{
			System.out.println( "SAXException: " + objSAXException.getMessage());
		}
		catch(TransformerConfigurationException objTransformerConfigurationException)
		{
			System.out.println("TransformerConfigurationException: "  + objTransformerConfigurationException.getMessage());
		}
		catch(TransformerException objTransformerException)
		{
			System.out.println("TransformerException: " + objTransformerException.getMessage());
		}
		return false;
	}

	/**
	 * Creates the specified file in the specified path. Currently, this is only used to create
	 * the missing <code>Users.xml</code> file in the user's home folder.
	 * It is expected that <code>fullyQualifiedFileName</code> will be the fully
	 * qualified path for the file. If there are any components of the path that are missing,
	 * such as folders not yet created, then this method will create those too.
	 * 
	 * @param fullyQualifiedFileName the full path of the file to be created
	 */
	public void createMissingUsersFile(String fullyQualifiedFileName) {
		File file = new File(fullyQualifiedFileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException ioe) {
			System.out.println("Problem with creating the Users XML " + ioe.getMessage());
		}

		try {
		    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		    Element usersRootElement = document.createElement("users");
		    document.appendChild(usersRootElement);

		    Transformer xmlToFileTransformer = TransformerFactory.newInstance().newTransformer();
		    xmlToFileTransformer.transform(new DOMSource(document), new StreamResult(file));
		} catch (ParserConfigurationException parserEx) {
			System.out.println("Exception in creating XML " + parserEx.getMessage());
		} catch (TransformerFactoryConfigurationError | TransformerException transEx) {
			System.out.println("Exception in transforming XML " + transEx.getMessage());
		}
	}
}