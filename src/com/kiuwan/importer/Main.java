package com.kiuwan.importer;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.kiuwan.importer.beans.KiuwanReport;
import com.kiuwan.importer.beans.Violation;
import com.kiuwan.importer.parser.BrakemanReportParser;
import com.kiuwan.importer.parser.FortifyReportParser;
import com.kiuwan.importer.parser.FxCopReportParser;
import com.kiuwan.importer.parser.ReportParser;
import com.kiuwan.importer.parser.RuboCopReportParser;
import com.kiuwan.importer.parser.InferReportParser;


public class Main {
	
	
	
	public enum Types {
		FORTIFY("Fortify"),
		FXCOP("FxCop"),
		RUBOCOP("RuboCop"),
		BRAKEMAN("Brakeman"),
		INFER("Infer");
		
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
		
		if (args.length < 3) {
			System.out.println("Incorrect syntax. <type> <input file> <output file> -language=<language> -base-folder=<analizedFolder> -hash-code=true|false");
			System.out.println("\tValid types: Fortify, FxCop, RuboCop, BrakeMan, Infer");
			return;
		}
		
		String type = args[0];
		String inputFile = args[1];
		String outputFile = args[2];
		String analyzedFolder = "";
		String language = "";
		Boolean hashCode = false;
		

		for(int i = 3; i < args.length; i++) {
			if (args[i].startsWith("-base-folder=")) {
				analyzedFolder = args[i].replace("-base-folder=", "");
			}
			else if (args[i].startsWith("-language=")) {
				language = args[i].replace("-language=", "");
			}
			else if (args[i].startsWith("-hash-code=")) {
				String param = args[i].replace("-hash-code=", "");
				try {
					hashCode = Boolean.parseBoolean(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		ReportParser parser = null;
		
		if (type.equals(Types.FORTIFY.toString())) {
			parser = new FortifyReportParser(language);
		}
		else if (type.equals(Types.FXCOP.toString())) {
			parser = new FxCopReportParser(analyzedFolder);
		}
		else if (type.equals(Types.RUBOCOP.toString())) {
			parser = new RuboCopReportParser();
		}
		else if (type.equals(Types.BRAKEMAN.toString())) {
			parser = new BrakemanReportParser(analyzedFolder);
		}
		else if (type.equals(Types.INFER.toString())) {
			parser = new InferReportParser(language);
		}
		
		if (parser != null) {
			parser.parse(inputFile);		
			
			KiuwanReport kiuwanReport = new KiuwanReport();
			kiuwanReport.setDefects(parser.getDefects());
			
			if (hashCode) {
				for(Violation defect: kiuwanReport.getDefects()) {
					try {
						defect.getFile().hashSourceCode();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			File file = new File(outputFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(KiuwanReport.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	 
			jaxbMarshaller.marshal(kiuwanReport, file);
			
			System.out.println("File " + outputFile + " successfully generated.");
		}
		else {
			System.out.println("Incorrect Type.");
		}
	}

}