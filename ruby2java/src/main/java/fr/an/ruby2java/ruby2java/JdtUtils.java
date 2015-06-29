package fr.an.ruby2java.ruby2java;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class JdtUtils {

	public static IDocument cuToDocument(CompilationUnit source) {
		IDocument document = new Document();
		@SuppressWarnings("unchecked")
		Map<String,String> options = JavaCore.getOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "4");
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, "space");
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");

		TextEdit text_edit = source.rewrite(document, options);
		try {
			text_edit.apply(document);
		} catch (Exception ex) {
			throw new RuntimeException("", ex);
		}
		return document;
	}

	public static CompilationUnit newCompilationUnit() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource("".toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();

		return cu;
	}


}
