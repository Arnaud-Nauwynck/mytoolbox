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

import java.util.Iterator;

import fr.an.com.sun.source.tree.*;
import fr.an.com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

/**
 * A path of tree nodes, typically used to represent the sequence of ancestor
 * nodes of a tree node up to the top level CompilationUnitTree node.
 *
 * @author Jonathan Gibbons
 * @since 1.6
 */
@jdk.Supported
public class JCTreePath implements Iterable<JCTree> {
    /**
     * Gets a tree path for a tree node within a compilation unit.
     * @return null if the node is not found
     */
    public static JCTreePath getPath(JCCompilationUnit unit, JCTree target) {
        return getPath(new JCTreePath(unit), target);
    }

    /**
     * Gets a tree path for a tree node within a subtree identified by a TreePath object.
     * @return null if the node is not found
     */
    public static JCTreePath getPath(JCTreePath path, JCTree target) {
        path.getClass();
        target.getClass();

        class Result extends Error {
            static final long serialVersionUID = -5942088234594905625L;
            JCTreePath path;
            Result(JCTreePath path) {
                this.path = path;
            }
        }

        class PathFinder extends JCTreePathScanner {
            private JCTree target;
            
            public PathFinder(JCTree target) {
                this.target = target;
            }

            public void scan(JCTree tree) {
                if (tree == target) {
                    throw new Result(new JCTreePath(getCurrentPath(), target));
                }
                super.scan(tree);
            }
        }

        if (path.getLeaf() == target) {
            return path;
        }

        try {
            new PathFinder(target).scan(path);
        } catch (Result result) {
            return result.path;
        }
        return null;
    }

    /**
     * Creates a TreePath for a root node.
     */
    public JCTreePath(JCCompilationUnit t) {
        this(null, t);
    }

    /**
     * Creates a TreePath for a child node.
     */
    public JCTreePath(JCTreePath p, JCTree t) {
        if (t.getKind() == Tree.Kind.COMPILATION_UNIT) {
            compilationUnit = (JCCompilationUnit) t;
            parent = null;
        }
        else {
            compilationUnit = p.compilationUnit;
            parent = p;
        }
        leaf = t;
    }
    /**
     * Get the compilation unit associated with this path.
     */
    public JCCompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * Get the leaf node for this path.
     */
    public JCTree getLeaf() {
        return leaf;
    }

    /**
     * Get the path for the enclosing node, or null if there is no enclosing node.
     */
    public JCTreePath getParentPath() {
        return parent;
    }

    /**
     *  Iterates from leaves to root.
     */
    @Override
    public Iterator<JCTree> iterator() {
        return new Iterator<JCTree>() {
            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public JCTree next() {
                JCTree t = next.leaf;
                next = next.parent;
                return t;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private JCTreePath next = JCTreePath.this;
        };
    }

    private JCCompilationUnit compilationUnit;
    private JCTree leaf;
    private JCTreePath parent;
}
