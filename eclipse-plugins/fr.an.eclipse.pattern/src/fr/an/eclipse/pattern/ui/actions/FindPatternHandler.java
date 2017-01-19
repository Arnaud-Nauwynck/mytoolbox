package fr.an.eclipse.pattern.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class FindPatternHandler extends AbstractHandler {

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
                MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(
                                event).getShell(), "Info", "Info for you");
                return null;
        }

}
