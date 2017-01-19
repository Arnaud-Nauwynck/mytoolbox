
find command similar to Ctrl+Shift+G  (find regerences in Workspace)
$ find . -name \*.properties -exec grep -H "Search for references to the selected element in the workspace" {} \; 
./eclipse.jdt.ui/org.eclipse.jdt.ui/plugin.properties:ActionDefinition.referencesInWorkspace.description= Search for references to the selected element in the workspace

=> command description key : 
	ActionDefinition.referencesInWorkspace.description
command :
./eclipse.jdt.ui/org.eclipse.jdt.ui/plugin.xml

     <command
            name="%ActionDefinition.referencesInWorkspace.name"
            description="%ActionDefinition.referencesInWorkspace.description"
            categoryId="org.eclipse.search.ui.category.search"
            id="org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace">
     </command>
     <action
           definitionId="org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace"
           label="%ReferencesInWorkspace.label"
           retarget="true"
           menubarPath="org.eclipse.search.menu/referencesSubMenu/group1"
           allowLabelUpdate="true"
           id="org.eclipse.jdt.ui.actions.ReferencesInWorkspace">
     </action>
     <key
        sequence="M1+M2+G"
        commandId="org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace"
        schemeId="org.eclipse.ui.defaultAcceleratorConfiguration"/>
     
      