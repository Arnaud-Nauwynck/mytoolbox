package fr.an.eclipse.pattern;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PatternUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.an.eclipse.pattern"; //$NON-NLS-1$

	// The shared instance
	private static PatternUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public PatternUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PatternUIPlugin getDefault() {
		return plugin;
	}

	

	public static void logInfo(String msg) {
		IStatus status = new Status(IStatus.INFO, PLUGIN_ID, msg);
		getDefault().getLog().log(status);
	}

	public static void logWarning(String msg, Exception ex) {
		IStatus status = new Status(Status.WARNING, PLUGIN_ID, msg, ex);
		getDefault().getLog().log(status);
	}
	
	public static void logError(String msg, Throwable ex) {
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		getDefault().getLog().log(status);
	}

	public static String getPluginId() {
		return PLUGIN_ID;
	}
	

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window= getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getActivePage();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}

	public static IStatus newStatusOK(String message) {
		return new Status(IStatus.OK, PatternUIPlugin.getPluginId(), 0, message, null);
	}
	
	public static IStatus newStatusError(String msg, Throwable ex) {
		return new Status(Status.ERROR, PLUGIN_ID, msg, ex);
	}

	public static IStatus newStatusWarning(String msg, Throwable ex) {
		return new Status(Status.WARNING, PLUGIN_ID, msg, ex);
	}

	public static MultiStatus newMultiStatus(String message) {
		return new MultiStatus(PLUGIN_ID, Status.OK, message, null);
	}

	public static MultiStatus newMultiStatusError(String message, MultiStatus merged) {
		MultiStatus res = new MultiStatus(PLUGIN_ID, Status.ERROR, message, null);
		res.merge(merged);
		return res;
	}

}
