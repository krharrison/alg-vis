/*******************************************************************************
 * Copyright (c) 2012-present Jakub Kováč, Jozef Brandýs, Katarína Kotrlová,
 * Pavol Lukča, Ladislav Pápay, Viktor Tomkovič, Tatiana Tóthová
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package algvis.ds.dictionaries.redblacktree;

import algvis.core.Algorithm;
import algvis.core.visual.ZDepth;
import algvis.ds.dictionaries.bst.BSTInsert;
import algvis.ui.view.REL;

public class RBInsert extends Algorithm {
    private final RB T;
    private final int K;

    public RBInsert(RB T, int x) {
        super(T.panel);
        this.T = T;
        K = x;
    }

    @Override
    public void runAlgorithm() {
        setHeader("insert", K);
        RBNode w = (RBNode) new BSTInsert(T, K)
            .insert(new RBNode(T, K, ZDepth.ACTIONNODE)).orElse(null);

        if (w != null) {
            // TODO komentar "ideme bublat" (nieco ako pri BSTDelete:
            // "first we have to find a node")
            pause();

            // bubleme nahor
            RBNode parent = w.getParent2();
            while (!w.isRoot() && parent.isRed()) {
                w.mark();
                final boolean isleft = parent.isLeft();
                final RBNode grandparent = parent.getParent2();
                RBNode uncle = (isleft ? grandparent.getRight() : grandparent.getLeft());
                if (uncle == null) {
                    uncle = T.NULL;
                }
                if (uncle.isRed()) {
                    // case 1
                    addStep(grandparent, REL.TOP, "rbinsertcase1", "" + K, w.getKeyS());
                    pause();
                    parent.setRed(false);
                    uncle.setRed(false);
                    grandparent.setRed(true);
                    w.unmark();
                    w = grandparent;
                    w.mark();
                    parent = w.getParent2();
                    pause();
                } else {
                    // case 2
                    if (isleft != w.isLeft()) {
                        addStep(grandparent, REL.TOP, "rbinsertcase2", "" + K, w.getKeyS());
                        pause();
                        T.rotate(w);
                        pause();
                    } else {
                        w.unmark();
                        w = w.getParent2();
                        w.mark();
                    }
                    parent = w.getParent2();
                    // case 3
                    addStep(uncle, REL.TOP, "rbinsertcase3", "" + K, w.getKeyS());
                    pause();
                    w.setRed(false);
                    parent.setRed(true);
                    T.rotate(w);
                    pause();
                    w.unmark();
                    break;
                }
            }

            w.unmark();

            RBNode root = (RBNode) T.getRoot();
            if(root.isRed()) {
                T.getRoot().mark();
                addStep(T.getRoot(), REL.TOP, "rbcolorroot", "" + K, w.getKeyS());
                pause();
                ((RBNode) T.getRoot()).setRed(false);
                T.getRoot().unmark();
            }
            T.reposition();
            addNote("done");
        }

        assert (((RBNode) T.getRoot()).testStructure()
            && ((RBNode) T.getRoot()).testStructure()
            && ((RBNode) T.getRoot()).testRedBlack());
    }
}
