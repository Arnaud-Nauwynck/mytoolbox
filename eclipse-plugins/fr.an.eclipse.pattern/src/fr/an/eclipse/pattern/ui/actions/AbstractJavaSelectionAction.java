package fr.an.eclipse.pattern.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.JavaTextSelection;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.CompilationUnitScanner;
import fr.an.eclipse.pattern.util.ConsoleUtil;
import fr.an.eclipse.pattern.util.JavaElementUtil;
import fr.an.eclipse.pattern.util.UiUtil;


/**
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractJavaSelectionAction implements IObjectActionDelegate {

	private ISelection selection;

	// ------------------------------------------------------------------------
	
	protected AbstractJavaSelectionAction() {
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	protected Shell getShell() {
	    if (UiUtil.isSWTGraphicsThread()) {
            return Display.getDefault().getActiveShell();
        } else {
            return null;
        }
	}

	protected Display getDisplay() {
		return Display.getDefault();
	}
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String message = getActionTitle() + " ...";
		final MultiStatus status = new MultiStatus(PatternUIPlugin.PLUGIN_ID, IStatus.OK, message, null);
		final StringBuffer resultMessage = new StringBuffer();
		try {
			Job job = new Job(getActionTitle()) {
				public IStatus run(IProgressMonitor monitor) {
				    monitor.beginTask(getActionTitle(), 10);
				    
					Set<ICompilationUnit> compilationUnits = selectionToCompilationUnits(monitor);
					monitor.worked(1);
					
					if (compilationUnits == null || compilationUnits.size() == 0) {
						return Status.OK_STATUS;
					}
					
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 2);
					doRun(status, subProgressMonitor, resultMessage, compilationUnits);
					subProgressMonitor.done();
					monitor.worked(9);
					
			       if (!status.isOK()) {
			            final String title = getActionTitle() + " - Failed";
                        ConsoleUtil.logError("Action failed: " + resultMessage);

			            final String msg = "status " + status + "\n" + truncateText(resultMessage.toString());
			            Display.getDefault().asyncExec(new Runnable() {
			                public void run() {
			                    ErrorDialog.openError(getShell(), title, msg, status);
			                }
			            });
			            
			        } else {
			            ConsoleUtil.log("done " + resultMessage);

			            final String msg = "done:" + truncateText(resultMessage.toString());
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                MessageDialog.openInformation(getShell(),
                                                              "Joto Eclipse Plug-in",
                                                              msg);
                            }
                        });
			        }
					
			        monitor.done();
					return status;
				}
			};
			
			job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
			job.schedule();
		} catch(Exception ex) {
			String title = getActionTitle() + " - Failed"; 
			String msg = "error " + ex.toString();
			ErrorDialog.openError(getShell(), title, msg, status);
		}
	}

	protected Set<ICompilationUnit> selectionToCompilationUnits(IProgressMonitor monitor) {
		List<IJavaElement> jelts = getSelectedJavaElements();

		CompilationUnitScanner recursiveCUScanner = new CompilationUnitScanner();
		try {
			recursiveCUScanner.recursiveScanCompilationUnits(monitor, jelts);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		Set<ICompilationUnit> res = recursiveCUScanner.getResultCompilationUnits();  
				
		return res;
	}

	protected List<IJavaElement> getSelectedJavaElements() {
		List<IJavaElement> jelts = null;
		Object[] resources = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSel = (IStructuredSelection) selection;
			resources = structuredSel.toArray();
			jelts = JavaElementUtil.resourcesToJavaElements(resources);
		} else if (selection instanceof JavaTextSelection) {
			JavaTextSelection sel2 = (JavaTextSelection) selection;
			try {
				IJavaElement[] elts = sel2.resolveElementAtOffset();
				jelts = new ArrayList<>(Arrays.asList(elts));
			} catch(Exception ex) {
				resources = new Object[0];	
			}
		} else {
			// TODO unsupported selection (text selection ?...)
			resources = new Object[0]; 
		}
		
		if (jelts == null && resources != null) {
			jelts = JavaElementUtil.resourcesToJavaElements(resources);
		}
		return jelts;
	}
	
	private String truncateText(String p) {
		String res = p;
		if (res.length() > 300) {
			res = res.substring(0, 300);
		}
		return res;
	}

	protected abstract String getActionTitle();
	
	protected abstract void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits);
	
}
