package fr.an.opendocument.tool;

import java.io.File;
import java.util.Iterator;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.presentation.Slide;

public class OdpImpressApp {

	protected File inputDocFile = new File("presentation.odp");
	protected File exportDocDir = new File("export");
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) {
		try {
			OdpImpressApp app = new OdpImpressApp();
			app.parseArgs(args);
			app.run();
		} catch(Exception ex) {
			ex.printStackTrace(System.err);
			System.err.println("Failed exiting(-1)");
		}
	}

	// ------------------------------------------------------------------------

	private void parseArgs(String[] args) {
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-i")) {
				inputDocFile = new File(args[++i]);
			} else if (arg.equals("-o")) {
				exportDocDir = new File(args[++i]);
			} else {
				throw new RuntimeException("Unrecognised arg '" + arg + "'");
			}
		}
	}
	
	protected void run() throws Exception {
		PresentationDocument doc;
		try {
			doc = PresentationDocument.loadDocument(inputDocFile);
		} catch (Exception e) {
			String msg = "Failed to load document '" + inputDocFile + "'";
			throw new RuntimeException(msg, e);
		}
		
		analyzeDoc(doc);
		
		System.out.println("done");
	}

	private void analyzeDoc(PresentationDocument doc) throws Exception {
		int slideCount = doc.getSlideCount();
		System.out.println("number of slides: " + slideCount);
		for(Iterator<Slide> slideIter = doc.getSlides(); slideIter.hasNext(); ) {
			Slide slide = slideIter.next();
			int slideIndex = slide.getSlideIndex();
			System.out.println("Slide " + slideIndex);
			DrawPageElement slideElt = slide.getOdfElement();
			slideElt.accept(new PrintElementVisitor());
			System.out.println();
		}
			
		OdfContentDom docDom = doc.getContentDom();
		
		OfficePresentationElement contentRoot = doc.getContentRoot();

		contentRoot.accept(new PrintElementVisitor());
	}
	
}
