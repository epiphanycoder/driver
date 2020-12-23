package org.ibfd.word2xml.kfch;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.ibfd.common.commandline.Options;
import org.ibfd.common.commandline.Options.Multiplicity;
import org.ibfd.common.commandline.Options.Separator;
import org.ibfd.word2xml.common.DocUtils;
import org.ibfd.word2xml.common.FileLogger;
import org.ibfd.common.aspose.AsposeLicenseStore;

import java.io.File;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFCHMain {
	
	/**
	 * Log file is created in the project's root directory
	 */
	public static FileLogger logger = new FileLogger("KfchWord2XML.log");
	
	/**
	 * For log purpose. Stores the file path that's being processed.
	 */
	public static String processingFilePath = "";

	/**
	 * Word Parser
	 */
	private static KFCHWordParser kFCHWordParser = new KFCHWordParser();
	
	/**
	 * Xml Builder
	 */
	private static KFCHXmlBuilder kFCHXmlBuilder = new KFCHXmlBuilder();
	
	/**
	 * Read Aspose License
	 */
	static {
		try {
			AsposeLicenseStore.getAsposeWords17License();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options(args);
		options.getSet().addOption("src", Separator.BLANK, Multiplicity.ONCE);
		options.getSet().addOption("des", Separator.BLANK, Multiplicity.ONCE);

		if(!options.check(false, false)){
			printUsage();
			exit(0);
		}
		processingFilePath = "";
		String src = options.getSet().getOption("src").getResultValue(0);
		String des = options.getSet().getOption("des").getResultValue(0);

		verifyExistence(src);
		verifyValidDirectory(des);

		final String absDesDir = getAbsDir(des);
			
		try{
			List<File> files = DocUtils.getFiles(src, ".*\\.docx?");
			
			if ( files == null || files.size() == 0 ){
				logger.error("Please check the src option. No \".doc\" or \".docx\" file is found there.");
			}
			
			for (Iterator<File> fIterator=files.iterator(); fIterator.hasNext(); ){
				File f = fIterator.next();
				processingFilePath = f.getAbsolutePath();
				convertWordToXml(processingFilePath, absDesDir);
			}
			
		}catch (Exception e){
			handleException(e);
		}

		exit(0);
	}
	
		
   	/**
	 * 
	 * @param e
	 */
	public static void handleException(Exception e) {
		logger.error("Error while processing file: " + processingFilePath + " Error: " + e );
		e.printStackTrace();
	}
	
	 /**
     * 
     * @param wordFileLocation
     * @param destinationDir
     */
	private static void convertWordToXml(String wordFileLocation, String destinationDir) {
        /*try {
        	System.out.println("\n\n## Converting " + wordFileLocation + " starts " );
        	KFCHWordData kFCHWordData = kFCHWordParser.parse(wordFileLocation);
        	if (kFCHWordData == null) {
        		return;
        	}
        	Document xmlDoc = kFCHXmlBuilder.buildXml(kFCHWordData);
        	if (xmlDoc == null) {
        		return;
        	}
        	String xmlFileName = kFCHXmlBuilder.getDestPath();
        	if (StringUtils.isNotEmpty(xmlFileName)) {
        		DocUtils.writeDocument(xmlDoc, new File(destinationDir,xmlFileName));
        	}else {
        		logger.error("Converting " + processingFilePath + " terminates : Output xml file name can not be produced." );
        	}
        } catch (Exception e) {
        	handleException(e);
        }*/
	    
	    try {
            System.out.println("\n\n## Converting " + wordFileLocation + " starts " );
            KFCHWordData kFCHWordData = kFCHWordParser.parse(wordFileLocation);
            Document xmlDoc = kFCHXmlBuilder.buildXml(kFCHWordData);
            String xmlFileName = kFCHXmlBuilder.getDestPath();
            if (StringUtils.isNotEmpty(xmlFileName)) {
                DocUtils.writeDocument(xmlDoc, new File(destinationDir,xmlFileName));
            }else {
                logger.error("Converting " + processingFilePath + " terminates : Output xml file name can not be produced." );
            }
        } catch (Exception e) {
            handleException(e);
        }
	    
	}
	
	/**
	 * 
	 * @param path
	 */
	private static void verifyValidDirectory(String path) {
		if (!new File(path).exists() && !new File(path).mkdirs()) {
			String err = path + " directory does not exist.";
			System.err.println(err);
			logger.error(err);
			exit(1);
		}
	}

	/**
	 * 
	 * @param code
	 */
	private static void exit(int code) {
		logger.close();
		if (code == 0 && ! logger.hasErrors()) { 
			System.out.print("--Program ended successfully.--"); 
		}
		System.exit(code);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private static String getAbsDir(String path) {
		File f = new File(path).getAbsoluteFile();
		return f.isFile() ? f.getParent() : f.getAbsolutePath();
	}

	/**
	 * 
	 */
	private static void printUsage() {
		
		System.out.println("\n\nUsage: java -jar kfch_word2xml.jar [-options]");
		System.out.println("where options include:");
		System.out.println();
		System.out.println("-src [value] \t File or Directory name. All DOC files in this directory or single file given will be considered as input file(s).");
		System.out.println("-des [value] \t Directory. All output XML files will be saved in this directory.");
		System.out.println("\n\n");
		
	}

	/**
	 * 
	 * @param path
	 */
	private static void verifyExistence(String path) {
		if (!new File(path).exists()) {
			String err = path + " file/directory does not exist.";
			System.err.println(err);
			logger.error(err);
			exit(1);
		}
	}
}
