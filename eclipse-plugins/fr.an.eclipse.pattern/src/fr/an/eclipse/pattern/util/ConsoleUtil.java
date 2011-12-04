package fr.an.eclipse.pattern.util;

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.an.eclipse.pattern.PatternUIPlugin;

@SuppressWarnings("restriction")
public final class ConsoleUtil {
	
    private static final String DEBUG_MSG = "DEBUG - ";
    private static final String JOTO_REFACTORING = "JotoRefactoring";
    private static final Color BLUE = new Color(Display.getCurrent(), 0, 0, 255);
    private static final Color RED = new Color(Display.getCurrent(), 255, 0, 0);
    
    private static boolean debug = false; 
    
    public static void logWithHyperLink(final String msg, final IResource resource, final int docOffset, final Object ...params) {
        if (UiUtil.isSWTGraphicsThread()) {
            _logWithHyperLink(msg, resource, docOffset, params);
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    _logWithHyperLink(msg, resource, docOffset, params);
                }
            });                            
        }
    }

    public static void log(final String msg, final Object ...params) {
        if (UiUtil.isSWTGraphicsThread()) {
            _log(msg, params);
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    _log(msg, params);
                }
            });                            
        }
    }

    public static void debug(final String msg, final Object ...params) {
        if (debug) {
            if (UiUtil.isSWTGraphicsThread()) {
                _debug(msg, params);
            } else {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        _debug(msg, params);
                    }
                });                            
            }
        }
    }
    
    public static void logError(final String msg, final Object ...params) {
        if (UiUtil.isSWTGraphicsThread()) {
            _logError(msg, params);
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    _logError(msg, params);
                }
            });                            
        }
    }
    

    //---------------------------------------------------------------------------------------------------
    
    public static boolean isDebug() {
        return ConsoleUtil.debug;
    }

    public static void setDebug(boolean debug) {
        ConsoleUtil.debug = debug;
    }

    //---------------------------------------------------------------------------------------------------
    
    private static MessageConsole getConsole() {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++)
           if (JOTO_REFACTORING.equals(existing[i].getName()))
              return (MessageConsole) existing[i];
        
        //no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(JOTO_REFACTORING, null);
        conMan.addConsoles(new IConsole[]{myConsole});
        
        return myConsole;
     }

    private static void log(MessageConsoleStream out, String msg, Object ...params) {
        try {
            out.println(String.format(msg, params));
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                //NOP
            }
        }
    }    
    
    private static void _logWithHyperLink(String msg, final IResource resource, int docOffset, Object ...params) {
        String pattern = resource.getFullPath().toString();
        String hyperLinkMsg = pattern;
        if (docOffset > 0) {
            pattern = pattern + " ,position:" + docOffset;
            hyperLinkMsg = pattern + " (In char, not a line number)";
        }
        
        getConsole().addPatternMatchListener(new ResourcePatternMatchListener(resource, docOffset, pattern));
            
        log(msg + hyperLinkMsg, params);
    }
    
    private static void _log(final String msg, final Object ...params) {
        MessageConsoleStream out = getConsole().newMessageStream();
        out.setColor(BLUE);
        log(out, msg, params);
    }
    
    private static void _debug(final String msg, final Object ...params) {
        if (debug) {
            MessageConsoleStream out = getConsole().newMessageStream();
            log(out, DEBUG_MSG + msg, params);
        }
    }
    
    private static void _logError(final String msg, final Object ...params) {
        MessageConsoleStream out = getConsole().newMessageStream();
        out.setColor(RED);
        log(out, msg, params);
    }

    // -------------------------------------------------------------------------
    
    /**
     * internal inner class for _logWithHyperLink()
     */
    protected static class ResourcePatternMatchListener implements IPatternMatchListener {

        private TextConsole console;
        private IResource resource = null;
        private int offset;
        private String pattern;

        public ResourcePatternMatchListener(IResource resource, int offset, String pattern) {
            super();
            this.resource = resource;
            this.offset = offset;
            this.pattern = pattern;
        }


        public int getCompilerFlags() {
            return Pattern.UNIX_LINES;
        }

        public String getLineQualifier() {
            return null;
        }

        public String getPattern() {
            return pattern;
        }

        public void connect(TextConsole console) {
            this.console = console;
        }

        public void disconnect() {
            this.console = null;
        }

        public void matchFound(PatternMatchEvent event) {
            if (console != null) {
                try {
                    console.addHyperlink(new ResourceHyperLink(resource, offset), event.getOffset(), event.getLength());
                } catch (BadLocationException e) {
                    //NOP
                } finally {
                    console.removePatternMatchListener(this);
                }
                
            }
        }
    }

    /**
     *
     */
    protected static class ResourceHyperLink implements IHyperlink {
        private IResource resource;
        private int docOffset;

        public ResourceHyperLink(IResource resource, int docOffset) {
            this.resource = resource;
            this.docOffset = docOffset;
        }

        public void linkActivated() {
            IEditorPart editorPart;
            try {
                editorPart = EditorUtility.openInEditor(resource);

                ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
                if (editor != null) {
                    editor.selectAndReveal(docOffset, 0);
                }
            } catch (PartInitException e) {
               PatternUIPlugin.logError("Error during the initialisation of " + resource  , e);
            }
            
        }

        public void linkEntered() {
            //NOP
        }

        public void linkExited() {
            //NOP
        }
    }

}
