package com.genggb.tools.xsd;

import java.io.*;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;


public class XMLValidateUtils {
	static String xsd1001 = "C:\\Users\\aa\\Desktop\\xsd\\_1001_结构_执行款缴纳.xsd";
	static String xsd1002 = "C:\\Users\\aa\\Desktop\\xsd\\_1002_结构_不明款到账.xsd";
	static String xsd1003 = "C:\\Users\\aa\\Desktop\\xsd\\_1003_结构_不明款认领.xsd";
	static String xsd1004 = "C:\\Users\\aa\\Desktop\\xsd\\_1004_结构_延缓发放.xsd";
	static String xsd1005 = "C:\\Users\\aa\\Desktop\\xsd\\_1005_结构_案款冻结.xsd";
	static String xsd1006 = "C:\\Users\\aa\\Desktop\\xsd\\_1006_结构_案款发放.xsd";
	static String xsd1007 = "C:\\Users\\aa\\Desktop\\xsd\\_1007_结构_案款发放明细.xsd";
	static String xsd1008 = "C:\\Users\\aa\\Desktop\\xsd\\_1008_结构_提存款到账.xsd";
	static String xsd1009 = "C:\\Users\\aa\\Desktop\\xsd\\_1009_结构_提存款返还.xsd";
	static String xsd1010 = "C:\\Users\\aa\\Desktop\\xsd\\_1010_结构_案款分配.xsd";
	static String xsd1011 = "C:\\Users\\aa\\Desktop\\xsd\\_1011_结构_案款分配明细.xsd";
	static String xsd1012 = "C:\\Users\\aa\\Desktop\\xsd\\_1012_结构_案款账户存量.xsd";
 
	/**
	 * @param xmlFile  xml字符串
	 * @param xsdFilePath  xsd 文件的全路径
	 * @return
	 */
	public static String validateXMLWithXSD(String xmlFile,String inxsdFilePath, String xsdFilePath) {
		try {
			Reader xmlReader = new BufferedReader(new StringReader(xmlFile));
			Reader inxsdReader = new BufferedReader(new FileReader(inxsdFilePath));
			Reader xsdReader = new BufferedReader(new FileReader(xsdFilePath));
			Source xmlSource = new StreamSource(xmlReader);
			Source inxsdSource = new StreamSource(inxsdReader);
			Source xsdSource = new StreamSource(xsdReader);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new Source[]{inxsdSource,xsdSource});
			XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(xmlSource);
			Validator validator = schema.newValidator();
			validator.validate(new StAXSource(reader));
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void validateXMLByXSD(File xmlFile, File xsdFile) {
        try {
            //创建默认的XML错误处理器
            XMLErrorHandler errorHandler = new XMLErrorHandler();
            //获取基于 SAX 的解析器的实例
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //解析器在解析时验证 XML 内容。
            factory.setValidating(true);
            //指定由此代码生成的解析器将提供对 XML 名称空间的支持。
            factory.setNamespaceAware(true);
            //使用当前配置的工厂参数创建 SAXParser 的一个新实例。
            SAXParser parser = factory.newSAXParser();
            //创建一个读取工具
            SAXReader xmlReader = new SAXReader();
            //获取要校验xml文档实例
            Document xmlDocument = (Document) xmlReader.read(xmlFile);
            //设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在 [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",xsdFile);
            //创建一个SAXValidator校验工具，并设置校验工具的属性
            SAXValidator validator = new SAXValidator(parser.getXMLReader());
            //设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
            validator.setErrorHandler(errorHandler);
            //校验
            validator.validate(xmlDocument);

            // 错误输出方式1
            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
            //如果错误信息不为空，说明校验失败，打印错误信息
            if (errorHandler.getErrors().hasContent()) {
                System.out.println("XML文件通过XSD文件校验失败！");
                writer.write(errorHandler.getErrors());
            } else {
                System.out.println("XML文件通过XSD文件校验成功！");
            }
            
            // 错误输出方式2
            System.out.println();
            StringBuilder errorMsg = new StringBuilder();
			if (errorHandler.getErrors().hasContent()) {
				List<Element> elements = errorHandler.getErrors().elements();
				for (Element element : elements) {
					String line = String.valueOf(Integer.parseInt(element.attributeValue("line")) - 1);
					String text = element.getText();
					errorMsg.append("(第 " + line + "行出现错误) " + text+"\r\n");
				}
				errorMsg.append("XML文件通过XSD文件校验失败！");
				System.out.println(errorMsg.toString());
			}else {
				System.out.println("XML文件通过XSD文件校验成功！");
			}
        } catch (Exception ex) {
            System.out.println("XML文件: " + xmlFile.getName() + " 通过XSD文件:" + xsdFile.getName() + "检验失败。/n原因： " + ex.getMessage());
            ex.printStackTrace();
        }
    }
	
	public static void main(String[] args) throws Exception {
		File fold = new File("D:\\SAFKREPORT\\report\\20191123\\");
		readFiles(fold);
	}
	
	public static void readFiles(File dir) {
        if(dir.isDirectory()) {
            File next[]=dir.listFiles();
            for (int i = 0; i < next.length; i++) {
            	String filepath = next[i].getAbsolutePath();
                if(next[i].isDirectory()) {
                	readFiles(next[i]);
                }else{
                	if(filepath.endsWith(".xml")){
                		System.out.println("=============================================");
                		System.out.println("======================开始校验："+filepath);
                		if(filepath.indexOf("1001")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1001));
                		}else if(filepath.indexOf("1002")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1002));
                		}else if(filepath.indexOf("1002")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1002));
                		}else if(filepath.indexOf("1003")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1003));
                		}else if(filepath.indexOf("1004")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1004));
                		}else if(filepath.indexOf("1005")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1005));
                		}else if(filepath.indexOf("1006")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1006));
                		}else if(filepath.indexOf("1007")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1007));
                		}else if(filepath.indexOf("1008")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1008));
                		}else if(filepath.indexOf("1009")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1009));
                		}else if(filepath.indexOf("1010")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1010));
                		}else if(filepath.indexOf("1011")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1011));
                		}else if(filepath.indexOf("1012")>0){
                			validateXMLByXSD(new File(filepath),new File(xsd1012));
                		}
                		System.out.println("======================校验结束："+filepath);
                		System.out.println("=============================================");
                		System.out.println();
                		System.out.println();
                	}
                }
            }
        }
    }
	
	
}
