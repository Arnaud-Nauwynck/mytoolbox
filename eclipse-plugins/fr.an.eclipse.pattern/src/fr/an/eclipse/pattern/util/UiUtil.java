package fr.an.eclipse.pattern.util;


import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.an.eclipse.pattern.PatternUIPlugin;

public class UiUtil {

    public static String askTextFileContent(final String defaultFileName) {
        final String[] contents = new String[1];

        if (UiUtil.isSWTGraphicsThread()) {
            Shell shell = Display.getDefault().getActiveShell();
            contents[0] = _askTextFileContent(shell, defaultFileName);
        } else {
            final Shell shell = null;
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    contents[0] = _askTextFileContent(shell, defaultFileName);
                }
            });
        }

        return contents[0];
    }
    

    private static String _askTextFileContent(Shell shell, String defaultFileName) {
        shell = new Shell(Display.getDefault());
        
        String [] filterNames = new String [] { "*.txt", "All Files (*)"};
        String [] filterExtensions = new String [] { "*.txt", "*" };
        
        IPath defaultPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        String oldPath = PatternUIPlugin.getDefault().getPreferenceStore().getString("old.path");
        if (oldPath != null) {
            defaultPath = new Path(oldPath);
        }
        
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterNames (filterNames);
        dialog.setFilterExtensions (filterExtensions);
        dialog.setFilterPath(defaultPath.toOSString());
        dialog.setFileName(defaultFileName);
        
        String fileName = dialog.open();
        
        //Store pref 
        PatternUIPlugin.getDefault().getPreferenceStore().putValue("old.path", dialog.getFilterPath());
        
        //read content
        String content = null;
        if (fileName != null) {
            File file = new File(fileName);
            content = IOUtil.readFileContent(file);
        }
        
        return content;
    }
	
	/**
	 * @param shell
	 * @return
	 */
	public static String askText(Shell shell) {
		
		final String[] res = new String[1];
		
		final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText("Dialog Shell");
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth = 10;
		formLayout.marginHeight = 10;
		formLayout.spacing = 10;
		dialog.setLayout (formLayout);

		Label label = new Label (dialog, SWT.NONE);
		label.setText ("Type a String:");
		FormData data = new FormData ();
		label.setLayoutData (data);

		Button cancel = new Button (dialog, SWT.PUSH);
		cancel.setText ("Cancel");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (100, 0);
		data.bottom = new FormAttachment (100, 0);
		cancel.setLayoutData (data);
		cancel.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				System.out.println("User cancelled dialog");
				dialog.close ();
			}
		});

		final Text text = new Text (dialog, SWT.BORDER | SWT.MULTI);
		data = new FormData ();
		data.width = 200;
		data.left = new FormAttachment (label, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
		data.top = new FormAttachment (label, 0, SWT.CENTER);
		data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
		text.setLayoutData (data);

		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment (100, 0);
		ok.setLayoutData (data);
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				res[0] = text.getText ();
				System.out.println ("User typed: " + text.getText ());
				dialog.close ();
			}
		});

		dialog.setDefaultButton(ok);
		
		Display display = shell.getDisplay();
		dialog.pack();
		dialog.open();
		while (!shell.isDisposed()) {
			 if (!display.readAndDispatch()) display.sleep();
		}
		
		return res[0];
	}
	
	
	/**
	 * 
	 * @param display
	 * @param clipboardText
	 */
	public static void copyTextToClipboard(Display display, String clipboardText) {
		Clipboard cb = new Clipboard(display);
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[] { clipboardText }, new Transfer[] { textTransfer });
	}
	
	public static boolean isSWTGraphicsThread() {
	    return Display.getDefault().getThread() == Thread.currentThread();
	}
}
