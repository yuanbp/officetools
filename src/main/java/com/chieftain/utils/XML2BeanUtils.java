package com.chieftain.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XML2BeanUtils {

	private final static String tag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * 项目中正常使用，将XML转换成对象
	 * 
	 * @param cls
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static <T> Object xmltoobject(Class<T> cls, String xml)
			throws Exception {
		JAXBElement<T> obj = (JAXBElement<T>) xmltojava(cls, xml);
		return obj.getValue();
	}

    /**
     * 项目中正常使用，将对象转成xml
     * @param rootObject
     * @return
     * @throws JAXBException
     */
    public static <T> String java2XML(T rootObject) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(rootObject.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8"); // 编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false); // 是否省略xml头信息
        StringWriter writer = new StringWriter();
        marshaller.marshal(rootObject, writer);
        return writer.toString();
    }

    public static <T> String java2XMLNFMT(T rootObject) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(rootObject.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8"); // 编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false); // 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false); // 是否省略xml头信息
        StringWriter writer = new StringWriter();
        marshaller.marshal(rootObject, writer);
        return writer.toString();
    }

	/**
	 * 将对象转换成XML xstream 方式
	 * 
	 * @param obj
	 *            要转换的对象
	 * @param cls
	 * @return
	 */
    public static String simpleclass2xml(Object obj, Class<?>... cls) {
        XStream xStream = new XStream(new DomDriver("utf-8"));
        xStream.processAnnotations(cls);
        xStream.aliasSystemAttribute(null, "class");
        String xml = xStream.toXML(obj);
        return tag + xml;
    }

    /**
     * 将XML转为对象
     *
     * @param xml
     * @param cls
     *            节点实体类，如果xml有多个实体则实例化多少实体
     * @return
     */

    public static Object simplexml2object(String xml, Class<?>... cls) {
        XStream xStream = new XStream(new DomDriver("utf-8"));
        xStream.processAnnotations(cls);
        Object reobj = xStream.fromXML(xml);
        return reobj;
    }

	public static String simpleclass2xml2(Object obj, Class<?>... cls) {
		XStream xStream = new XStream(new DomDriver("utf-8"));
		xStream.processAnnotations(cls);
		xStream.aliasSystemAttribute(null, "class");
		String xml = xStream.toXML(obj);
		xml = xml.replaceAll("__", "_");
		return tag + xml;
	}

	private static <T> JAXBElement<T> xmltojava(Class<T> cls, String xml)
			throws Exception {
		JAXBElement<T> el = null;
		byte[] bytes = xml.getBytes("UTF-8");
		InputStream in = new UnicodeInputStream(new ByteArrayInputStream(bytes), "UTF-8");
		XMLReader xmlReader = SingletonUtil.getXMLReader();
		Source source = new SAXSource(xmlReader, new InputSource(in));
		synchronized ("") {
			JAXBContext ctx = JAXBContext.newInstance(cls);
			Unmarshaller us = ctx.createUnmarshaller();
			el = us.unmarshal(source, cls);
		}
		return el;
	}

	/**
	 * @param obj
	 *            要转换的对象
	 * @param child
	 * @return
	 */
	public static String simpleobject2xml(Object obj, Object... child) {
		XStream xStream = new XStream(new DomDriver("utf-8"));
		xStream.alias(obj.getClass().getSimpleName(), obj.getClass());
		for (int i = 0; i < child.length; i++) {
			Object objs = child[i];
			xStream.alias(objs.getClass().getSimpleName(), objs.getClass());
		}
		String xml = xStream.toXML(obj);
		return tag + xml;
	}

	/**
	 * 写到xml文件中去
	 * 
	 * writeXMLFile
	 * @param obj
	 *            对象
	 * @param absPath
	 *            绝对路径
	 * @param fileName
	 *            文件名
	 * @return boolean
	 */

	public static boolean toXMLFile(Object obj, String absPath, String fileName) {
		String strXml = simpleobject2xml(obj);
		String filePath = absPath + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			    e.printStackTrace();
				return false;
			}
		}// end if
		OutputStream ous = null;
		try {
			ous = new FileOutputStream(file);
			ous.write(strXml.getBytes());
			ous.flush();
		} catch (Exception e) {
		    e.printStackTrace();
			return false;
		} finally {
			if (ous != null) {
                try {
                    ous.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		return true;
	}

	/**
	 * 从xml文件读取报文
	 * 
	 * toBeanFromFile
	 * @param absPath
	 *            绝对路径
	 * @param fileName
	 *            文件名
	 * @param node
	 * @throws Exception
	 * @return reobj
	 */
	public static Object toBeanFromFile(String absPath, String fileName,
			Object... node) throws Exception {
		File file = new File(absPath + fileName);
		Reader reader = new InputStreamReader(new FileInputStream(file));
		XStream xStream = new XStream(new DomDriver());
		for (int i = 0; i < node.length; i++) {
			Object objs = node[i];
			xStream.alias(objs.getClass().getSimpleName(), objs.getClass());
		}
		Object reobj = xStream.fromXML(reader);
		return reobj;
	}

}
