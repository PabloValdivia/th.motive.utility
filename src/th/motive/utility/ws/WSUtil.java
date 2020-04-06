package th.motive.utility.ws;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class WSUtil {
	/**
	 * use clazz as contect to unmarshall, return install of clazz
	 * @param clazz
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static <T> T xmlUnmarshall(Class<T> clazz, String xml) throws Exception {
		StringReader reader = null;
		try {
			// covert text to Java class object
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			reader = new StringReader(xml);

			@SuppressWarnings("unchecked")
			T header = (T)jaxbUnmarshaller.unmarshal(reader);
			return header;	
		}finally {
			if (reader != null)
				reader.close();
		}
		

		
	}

	/**
	 * use obj.getClass as class context to marshall
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String xmlMarshall(Object obj) throws Exception {
		StringWriter writer = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			writer = new StringWriter();

			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(obj, writer);
			String result = writer.toString();
			return result;		
		}finally {
			if (writer != null)
				writer.close();
		}		
	}
}
