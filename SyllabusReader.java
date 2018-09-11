/* 
 * Author: Callum Hafner Schnee, Independent, September 4 2018 
 * EventCreator is a licensed file and any attempt to use it in a program other than Consyl will be persecuted
 * Copyright License c. 2018 
 */
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor; 



public class SyllabusReader { 
	
	private static File syllabus; 
	private File converted; 
	
	public SyllabusReader() {
		syllabus = null; 
	}
	
	public void fileSelector() {
		JFileChooser chooser=new JFileChooser(); 
		FileNameExtensionFilter wordfilter=new FileNameExtensionFilter("Syllabus Documents", "docx", "doc", "pdf"); 
		//FileNameExtensionFilter pdffilter = new FileNameExtensionFilter("PDF Documents", "pdf"); 
		//chooser.addChoosableFileFilter(wordfilter);
		//chooser.addChoosableFileFilter(pdffilter); 
		chooser.setFileFilter(wordfilter);
		int in = chooser.showOpenDialog(null);
		try {
			if(in==JFileChooser.APPROVE_OPTION) {
				syllabus=chooser.getSelectedFile();
					if(!chooser.accept(chooser.getSelectedFile())) {
						System.out.println("File chosen is not compatible");
						return;
					} else {
						System.out.println(chooser.getSelectedFile().getName()+" opened");
					}
			}
		} catch(Exception e) {
			System.out.println("Please choose a file");
		}
	}

	public void readFile(File syllabus) { 
		String filename = syllabus.getName();
		int dot = filename.lastIndexOf('.');
		String fending = filename.substring(dot, filename.length());
		if(".pdf".equals(fending)) {
			PDFParser parser;
	        String text=null;
	        PDFTextStripper stripper;
	        PDDocument pdDoc=null;
	        COSDocument cosDoc=null;
			try {
	            parser=new PDFParser(new RandomAccessBufferedFileInputStream(syllabus));
	        } catch (IOException e) {
	            System.err.println("Unable to open PDF Parser. "+e.getMessage());
	            return;
	        }
			try {
	            parser.parse();
	            cosDoc=parser.getDocument();
	            stripper=new PDFTextStripper();
	            pdDoc=new PDDocument(cosDoc);
	            text=stripper.getText(pdDoc);
	        } catch (Exception e) {
	            System.err.println("An exception occured in parsing the PDF Document."+ e.getMessage());
	        } finally {
	            try {
	                if (cosDoc != null)
	                    cosDoc.close();
	                if (pdDoc != null)
	                    pdDoc.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
			try {
	            converted=new File("/Users/schnee/Desktop/sampletextfile.txt");
	            if (!converted.exists()) {
	                converted.createNewFile();
	            }
	            FileWriter writer=new FileWriter(converted.getAbsoluteFile());
	            BufferedWriter bwriter=new BufferedWriter(writer);
	            bwriter.write(text);
	            bwriter.close();
	            System.out.println("Done");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} else if(".doc".equals(fending) || ".docx".equals(fending)) {
			//System.out.println("File chosen is compatible");
			WordExtractor extractor=null;
			String text=null;
			try {
				FileInputStream in=new FileInputStream(syllabus.getAbsolutePath());
				HWPFDocument document=new HWPFDocument(in);
				extractor=new WordExtractor(document);
				text=extractor.getText();	
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
	            converted=new File("/Users/schnee/Desktop/sampletextfile.txt");
	            if (!converted.exists()) {
	                converted.createNewFile();
	            }
	            FileWriter writer=new FileWriter(converted.getAbsoluteFile());
	            BufferedWriter bwriter=new BufferedWriter(writer);
	            bwriter.write(text);
	            bwriter.close();
	            System.out.println("Done");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} else {
			System.out.println("File chosen is not compatible");
			return;
		}
		
	}
	
	public File getTxt() {
		return converted; 
	}
	
	public File getSyllabus() {
		return syllabus; 
	}
	
	public static void main(String[] args) throws IOException {
		SyllabusReader reader = new SyllabusReader(); 
		reader.fileSelector(); 
		reader.readFile(syllabus);
	}
} 

