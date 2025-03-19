/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * $Id$
 */

package consulo.images.desktop.awt.impl.thumbnail;

import consulo.annotation.access.RequiredReadAction;
import consulo.dataContext.DataProvider;
import consulo.disposer.Disposable;
import consulo.fileEditor.FileEditorManager;
import consulo.images.desktop.awt.impl.IfsUtil;
import consulo.images.desktop.awt.impl.editor.ImageComponent;
import consulo.images.desktop.awt.impl.editor.ThumbnailComponent;
import consulo.images.desktop.awt.impl.editor.ThumbnailComponentUI;
import consulo.language.editor.PsiActionSupportFactory;
import consulo.language.editor.util.PsiUtilBase;
import consulo.language.file.FileTypeManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.module.content.ProjectRootManager;
import consulo.navigation.Navigatable;
import consulo.project.Project;
import consulo.ui.ex.*;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionPopupMenu;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.awt.IdeBorderFactory;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.ui.ex.awt.SideBorder;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.util.dataholder.Key;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.event.*;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.options.*;
import org.intellij.images.thumbnail.ThumbnailView;
import org.intellij.images.thumbnail.actionSystem.ThumbnailViewActions;
import org.intellij.images.ui.ImageComponentDecorator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

final class ThumbnailViewUI extends JPanel implements DataProvider, Disposable {
    private final VirtualFileListener vfsListener = new VFSListener();
    private final OptionsChangeListener optionsListener = new OptionsChangeListener();

    private static final Navigatable[] EMPTY_NAVIGATABLE_ARRAY = new Navigatable[]{};

    private final ThumbnailView thumbnailView;
    private final CopyPasteSupport copyPasteSupport;
    private final DeleteProvider deleteProvider;
    private ThumbnailListCellRenderer cellRenderer;
    private JList<VirtualFile> list;
    private static final Comparator<VirtualFile> VIRTUAL_FILE_COMPARATOR = (o1, o2) -> {
        if (o1.isDirectory() && !o2.isDirectory()) {
            return -1;
        }
        if (o2.isDirectory() && !o1.isDirectory()) {
            return 1;
        }

        return o1.getPath().toLowerCase().compareTo(o2.getPath().toLowerCase());
    };

    public ThumbnailViewUI(ThumbnailViewImpl thumbnailView) {
        super(new BorderLayout());

        this.thumbnailView = thumbnailView;

        PsiActionSupportFactory factory = PsiActionSupportFactory.getInstance();
        copyPasteSupport = factory.createPsiBasedCopyPasteSupport(
            thumbnailView.getProject(),
            this,
            () -> (PsiElement[])getData(PsiElement.KEY_OF_ARRAY)
        );

        deleteProvider = factory.createPsiBasedDeleteProvider();
    }

    private void createUI() {
        if (cellRenderer != null && list != null) {
            return;
        }
        cellRenderer = new ThumbnailListCellRenderer();
        ImageComponent imageComponent = cellRenderer.getImageComponent();

        VirtualFileManager.getInstance().addVirtualFileListener(vfsListener);

        Options options = OptionsManager.getInstance().getOptions();
        EditorOptions editorOptions = options.getEditorOptions();
        // Set options
        TransparencyChessboardOptions chessboardOptions = editorOptions.getTransparencyChessboardOptions();
        ImageComponent.Chessboard chessboard = imageComponent.getTransparencyChessboard();
        chessboard.setVisible(chessboardOptions.isShowDefault());
        chessboard.setCellSize(chessboardOptions.getCellSize());
        chessboard.setWhiteColor(TargetAWT.to(chessboardOptions.getWhiteColor()));
        chessboard.setBlackColor(TargetAWT.to(chessboardOptions.getBlackColor()));

        options.addPropertyChangeListener(optionsListener);

        list = new JBList<>();
        list.setModel(new DefaultListModel<>());
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.setCellRenderer(cellRenderer);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        ThumbnailsMouseAdapter mouseListener = new ThumbnailsMouseAdapter();
        list.addMouseListener(mouseListener);
        list.addMouseMotionListener(mouseListener);

        ThumbnailComponentUI componentUI = (ThumbnailComponentUI)UIManager.getUI(cellRenderer);
        Dimension preferredSize = componentUI.getPreferredSize(cellRenderer);

        list.setFixedCellWidth(preferredSize.width);
        list.setFixedCellHeight(preferredSize.height);

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(
            list,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP));

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup)actionManager.getAction(ThumbnailViewActions.GROUP_TOOLBAR);
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ThumbnailViewActions.ACTION_PLACE, actionGroup, true);
        actionToolbar.setTargetComponent(this);

        JComponent toolbar = actionToolbar.getComponent();

        FocusRequester focusRequester = new FocusRequester();
        toolbar.addMouseListener(focusRequester);
        scrollPane.addMouseListener(focusRequester);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        createUI();
        if (list != null) {
            DefaultListModel<VirtualFile> model = (DefaultListModel<VirtualFile>)list.getModel();
            model.clear();
            VirtualFile root = thumbnailView.getRoot();
            if (root != null && root.isValid() && root.isDirectory()) {
                Set<VirtualFile> files = findFiles(root.getChildren());
                VirtualFile[] virtualFiles = VirtualFileUtil.toVirtualFileArray(files);
                Arrays.sort(virtualFiles, VIRTUAL_FILE_COMPARATOR);

                model.ensureCapacity(model.size() + virtualFiles.length + 1);
                for (VirtualFile virtualFile : virtualFiles) {
                    model.addElement(virtualFile);
                }
                if (model.size() > 0) {
                    list.setSelectedIndex(0);
                }
            }
            else {
                thumbnailView.setVisible(false);
            }
        }
    }

    public boolean isTransparencyChessboardVisible() {
        createUI();
        return cellRenderer.getImageComponent().getTransparencyChessboard().isVisible();
    }

    public void setTransparencyChessboardVisible(boolean visible) {
        createUI();
        cellRenderer.getImageComponent().getTransparencyChessboard().setVisible(visible);
        list.repaint();
    }

    public void setSelected(VirtualFile file, boolean selected) {
        createUI();
        list.setSelectedValue(file, false);
    }

    public void scrollToSelection() {
        int minSelectionIndex = list.getMinSelectionIndex();
        int maxSelectionIndex = list.getMaxSelectionIndex();
        if (minSelectionIndex != -1 && maxSelectionIndex != -1) {
            list.scrollRectToVisible(list.getCellBounds(minSelectionIndex, maxSelectionIndex));
        }
    }

    public boolean isSelected(VirtualFile file) {
        int index = ((DefaultListModel)list.getModel()).indexOf(file);
        return index != -1 && list.isSelectedIndex(index);
    }

    @Nonnull
    public VirtualFile[] getSelection() {
        if (list != null) {
            java.util.List<VirtualFile> selectedValues = list.getSelectedValuesList();
            if (selectedValues != null) {
                return selectedValues.toArray(new VirtualFile[selectedValues.size()]);
            }
        }
        return VirtualFile.EMPTY_ARRAY;
    }

    private final class ThumbnailListCellRenderer extends ThumbnailComponent implements ListCellRenderer<VirtualFile> {
        private final ImageFileTypeManager typeManager = ImageFileTypeManager.getInstance();

        @Override
        public Component getListCellRendererComponent(JList list, VirtualFile file, int index, boolean isSelected, boolean cellHasFocus) {
            setFileName(file.getName());
            setToolTipText(IfsUtil.getReferencePath(thumbnailView.getProject(), file));
            setDirectory(file.isDirectory());
            if (file.isDirectory()) {
                int imagesCount = 0;
                VirtualFile[] children = file.getChildren();
                for (VirtualFile child : children) {
                    if (typeManager.isImage(child)) {
                        imagesCount++;
                        if (imagesCount > 100) {
                            break;
                        }
                    }
                }
                setImagesCount(imagesCount);
            }
            else {
                // File rendering
                setFileSize(file.getLength());
                try {
                    BufferedImage image = IfsUtil.getImage(file);
                    ImageComponent imageComponent = getImageComponent();
                    imageComponent.getDocument().setValue(image);
                    setFormat(IfsUtil.getFormat(file));
                }
                catch (Exception e) {
                    // Ignore
                    ImageComponent imageComponent = getImageComponent();
                    imageComponent.getDocument().setValue((BufferedImage)null);
                }
            }

            if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
            }
            else {
                setForeground(list.getForeground());
                setBackground(list.getBackground());
            }

            return this;
        }
    }

    private Set<VirtualFile> findFiles(VirtualFile[] roots) {
        Set<VirtualFile> files = new HashSet<>();
        for (VirtualFile root : roots) {
            files.addAll(findFiles(root));
        }
        return files;
    }

    private Set<VirtualFile> findFiles(VirtualFile file) {
        Set<VirtualFile> files = new HashSet<>(0);
        Project project = thumbnailView.getProject();
        if (!project.isDisposed()) {
            ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
            boolean projectIgnored = rootManager.getFileIndex().isIgnored(file);

            if (!projectIgnored && !FileTypeManager.getInstance().isFileIgnored(file)) {
                ImageFileTypeManager typeManager = ImageFileTypeManager.getInstance();
                if (file.isDirectory()) {
                    if (thumbnailView.isRecursive()) {
                        files.addAll(findFiles(file.getChildren()));
                    }
                    else if (isImagesInDirectory(file)) {
                        files.add(file);
                    }
                }
                else if (typeManager.isImage(file)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private boolean isImagesInDirectory(VirtualFile dir) {
        ImageFileTypeManager typeManager = ImageFileTypeManager.getInstance();
        VirtualFile[] files = dir.getChildren();
        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                // We can be sure for fast searching
                return true;
            }
            if (typeManager.isImage(file)) {
                return true;
            }
        }
        return false;
    }

    private final class ThumbnailsMouseAdapter extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseDragged(@Nonnull MouseEvent e) {
            Point point = e.getPoint();
            int index = list.locationToIndex(point);
            if (index != -1) {
                Rectangle cellBounds = list.getCellBounds(index, index);
                if (!cellBounds.contains(point) && (KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK) {
                    list.clearSelection();
                    e.consume();
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mousePressed(@Nonnull MouseEvent e) {
            Point point = e.getPoint();
            int index = list.locationToIndex(point);
            if (index != -1) {
                Rectangle cellBounds = list.getCellBounds(index, index);
                if (!cellBounds.contains(point) && (KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK) {
                    list.clearSelection();
                    e.consume();
                }
            }
        }

        @Override
        public void mouseClicked(@Nonnull MouseEvent e) {
            Point point = e.getPoint();
            int index = list.locationToIndex(point);
            if (index != -1) {
                Rectangle cellBounds = list.getCellBounds(index, index);
                if (!cellBounds.contains(point) && (KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK) {
                    index = -1;
                    list.clearSelection();
                }
            }
            if (index != -1) {
                if (MouseEvent.BUTTON1 == e.getButton() && e.getClickCount() == 2) {
                    // Double click
                    list.setSelectedIndex(index);
                    VirtualFile selected = list.getSelectedValue();
                    if (selected != null) {
                        if (selected.isDirectory()) {
                            thumbnailView.setRoot(selected);
                        }
                        else {
                            FileEditorManager fileEditorManager = FileEditorManager.getInstance(thumbnailView.getProject());
                            fileEditorManager.openFile(selected, true);
                        }
                        e.consume();
                    }
                }
                if (MouseEvent.BUTTON3 == e.getButton() && e.getClickCount() == 1) {
                    // Ensure that we have selection
                    if ((KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK) {
                        // Ctrl is not pressed
                        list.setSelectedIndex(index);
                    }
                    else {
                        // Ctrl is pressed
                        list.getSelectionModel().addSelectionInterval(index, index);
                    }
                    // Single right click
                    ActionManager actionManager = ActionManager.getInstance();
                    ActionGroup actionGroup = (ActionGroup)actionManager.getAction(ThumbnailViewActions.GROUP_POPUP);
                    ActionPopupMenu menu = actionManager.createActionPopupMenu(ThumbnailViewActions.ACTION_PLACE, actionGroup);
                    JPopupMenu popupMenu = menu.getComponent();
                    popupMenu.pack();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());

                    e.consume();
                }
            }
        }
    }

    @Nullable
    @Override
    @RequiredReadAction
    public Object getData(@Nonnull Key<?> dataId) {
        if (Project.KEY == dataId) {
            return thumbnailView.getProject();
        }
        else if (VirtualFile.KEY == dataId) {
            VirtualFile[] selectedFiles = getSelectedFiles();
            return selectedFiles.length > 0 ? selectedFiles[0] : null;
        }
        else if (VirtualFile.KEY_OF_ARRAY == dataId) {
            return getSelectedFiles();
        }
        else if (PsiFile.KEY == dataId) {
            return getData(PsiElement.KEY);
        }
        else if (PsiElement.KEY == dataId) {
            VirtualFile[] selectedFiles = getSelectedFiles();
            return selectedFiles.length > 0 ? PsiManager.getInstance(thumbnailView.getProject()).findFile(selectedFiles[0]) : null;
        }
        else if (PsiElement.KEY_OF_ARRAY == dataId) {
            return getSelectedElements();
        }
        else if (Navigatable.KEY == dataId) {
            VirtualFile[] selectedFiles = getSelectedFiles();
            return new ThumbnailNavigatable(selectedFiles.length > 0 ? selectedFiles[0] : null);
        }
        else if (CopyProvider.KEY == dataId) {
            return copyPasteSupport.getCopyProvider();
        }
        else if (CutProvider.KEY == dataId) {
            return copyPasteSupport.getCutProvider();
        }
        else if (PasteProvider.KEY == dataId) {
            return copyPasteSupport.getPasteProvider();
        }
        else if (DeleteProvider.KEY == dataId) {
            return deleteProvider;
        }
        else if (Navigatable.KEY_OF_ARRAY == dataId) {
            VirtualFile[] selectedFiles = getSelectedFiles();
            Set<Navigatable> navigatables = new HashSet<>(selectedFiles.length);
            for (VirtualFile selectedFile : selectedFiles) {
                if (!selectedFile.isDirectory()) {
                    navigatables.add(new ThumbnailNavigatable(selectedFile));
                }
            }
            return navigatables.toArray(EMPTY_NAVIGATABLE_ARRAY);
        }
        else if (ThumbnailView.DATA_KEY == dataId) {
            return thumbnailView;
        }
        else if (ImageComponentDecorator.DATA_KEY == dataId) {
            return thumbnailView;
        }

        return null;
    }

    @Nonnull
    @RequiredReadAction
    private PsiElement[] getSelectedElements() {
        VirtualFile[] selectedFiles = getSelectedFiles();
        Set<PsiElement> psiElements = new HashSet<>(selectedFiles.length);
        PsiManager psiManager = PsiManager.getInstance(thumbnailView.getProject());
        for (VirtualFile file : selectedFiles) {
            PsiFile psiFile = psiManager.findFile(file);
            PsiElement element = psiFile != null ? psiFile : psiManager.findDirectory(file);
            if (element != null) {
                psiElements.add(element);
            }
        }
        return PsiUtilBase.toPsiElementArray(psiElements);
    }

    @Nonnull
    private VirtualFile[] getSelectedFiles() {
        if (list != null) {
            java.util.List<VirtualFile> selectedValues = list.getSelectedValuesList();
            if (selectedValues != null) {
                return selectedValues.toArray(new VirtualFile[selectedValues.size()]);
            }
        }
        return VirtualFile.EMPTY_ARRAY;
    }

    @Override
    public void dispose() {
        removeAll();

        Options options = OptionsManager.getInstance().getOptions();
        options.removePropertyChangeListener(optionsListener);

        VirtualFileManager.getInstance().removeVirtualFileListener(vfsListener);

        list = null;
        cellRenderer = null;
    }

    private final class ThumbnailNavigatable implements Navigatable {
        private final VirtualFile file;

        public ThumbnailNavigatable(VirtualFile file) {
            this.file = file;
        }

        @Override
        public void navigate(boolean requestFocus) {
            if (file != null) {
                FileEditorManager manager = FileEditorManager.getInstance(thumbnailView.getProject());
                manager.openFile(file, true);
            }
        }

        @Override
        public boolean canNavigate() {
            return file != null;
        }

        @Override
        public boolean canNavigateToSource() {
            return file != null;
        }
    }

    private final class VFSListener implements VirtualFileListener {
        @Override
        public void contentsChanged(@Nonnull VirtualFileEvent event) {
            VirtualFile file = event.getFile();
            if (list != null) {
                int index = ((DefaultListModel)list.getModel()).indexOf(file);
                if (index != -1) {
                    Rectangle cellBounds = list.getCellBounds(index, index);
                    list.repaint(cellBounds);
                }
            }
        }

        @Override
        public void fileDeleted(@Nonnull VirtualFileEvent event) {
            VirtualFile file = event.getFile();
            VirtualFile root = thumbnailView.getRoot();
            if (root != null && consulo.ide.impl.idea.openapi.vfs.VfsUtil.isAncestor(file, root, false)) {
                refresh();
            }
            if (list != null) {
                ((DefaultListModel)list.getModel()).removeElement(file);
            }
        }

        @Override
        public void propertyChanged(@Nonnull VirtualFilePropertyEvent event) {
            refresh();
        }

        @Override
        public void fileCreated(@Nonnull VirtualFileEvent event) {
            refresh();
        }

        @Override
        public void fileMoved(@Nonnull VirtualFileMoveEvent event) {
            refresh();
        }
    }

    private final class OptionsChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Options options = (Options)evt.getSource();
            EditorOptions editorOptions = options.getEditorOptions();
            TransparencyChessboardOptions chessboardOptions = editorOptions.getTransparencyChessboardOptions();
            GridOptions gridOptions = editorOptions.getGridOptions();

            ImageComponent imageComponent = cellRenderer.getImageComponent();
            ImageComponent.Chessboard chessboard = imageComponent.getTransparencyChessboard();
            chessboard.setCellSize(chessboardOptions.getCellSize());
            chessboard.setWhiteColor(TargetAWT.to(chessboardOptions.getWhiteColor()));
            chessboard.setBlackColor(TargetAWT.to(chessboardOptions.getBlackColor()));

            ImageComponent.Grid grid = imageComponent.getGrid();
            grid.setLineZoomFactor(gridOptions.getLineZoomFactor());
            grid.setLineSpan(gridOptions.getLineSpan());
            grid.setLineColor(TargetAWT.to(gridOptions.getLineColor()));
        }
    }

    private class FocusRequester extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            requestFocus();
        }
    }
}
