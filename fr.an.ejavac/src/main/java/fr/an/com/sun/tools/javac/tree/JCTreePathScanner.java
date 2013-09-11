/*
 * Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package fr.an.com.sun.tools.javac.tree;

/**
 * A TreeVisitor that visits all the child tree nodes, and provides
 * support for maintaining a path for the parent nodes.
 * To visit nodes of a particular type, just override the
 * corresponding visitorXYZ method.
 * Inside your method, call super.visitXYZ to visit descendant
 * nodes.
 *
 * same as fr.an.com.sun.source.util.TreePathScanner, but not generic, and using javac JCTree instead of interface Tree
 * 
 * @since 1.8-ARN
 */
@jdk.Supported
public class JCTreePathScanner extends TreeScanner {

    /**
     * Scan a tree from a position identified by a TreePath.
     */
    public void scan(JCTreePath path) {
        this.path = path;
        try {
            path.getLeaf().accept(this);
        } finally {
            this.path = null;
        }
    }
    
    /**
     * Scan a single node.
     * The current path is updated for the duration of the scan.
     */
    @Override
    public void scan(JCTree tree) {
        if (tree == null)
            return;

        JCTreePath prev = path;
        path = new JCTreePath(path, tree);
        try {
            tree.accept(this);
        } finally {
            path = prev;
        }
    }

    /**
     * Get the current path for the node, as built up by the currently
     * active set of scan calls.
     */
    public JCTreePath getCurrentPath() {
        return path;
    }

    private JCTreePath path;
}
