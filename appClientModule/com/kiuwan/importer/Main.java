package com.kiuwan.importer;
import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.kiuwan.importer.beans.KiuwanReport;
import com.kiuwan.importer.parser.FortifyReportParser;
import com.kiuwan.importer.parser.ReportParser;


public class Main {
	
	public enum Types {
		FORTIFY("Fortify");
		
		private final String type;
		
		private Types(final String type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return type;
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		
		if (args.length != 3) {
			System.out.println("Incorrect syntax. <type> <input file> <output file>");
			System.out.println("\tValid types: Fortify");
			return;
		}
		
		String type = args[0];
		String inputFile = args[1];
		String outputFile = args[2];
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		
		ReportParser handler = null;
		
		if (type.equals(Types.FORTIFY.toString())) {
			handler = new FortifyReportParser();
		}
		
		if (handler != null) {
			saxParser.parse(inputFile, handler);
			
			
			
			KiuwanReport kiuwanReport = new KiuwanReport();
			kiuwanReport.setDefects(handler.getDefects());
			
			
			File file = new File(outputFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(KiuwanReport.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	 
			jaxbMarshaller.marshal(kiuwanReport, file);
			
			System.out.println("File " + outputFile + " successfully generated.");
		}
		else {
			System.out.println("Incorrect Type");
		}
	}

}