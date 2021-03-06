/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.handler;

import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.robotframework.ide.eclipse.main.plugin.hyperlink.detectors.ITableHyperlinksDetector;
import org.robotframework.ide.eclipse.main.plugin.hyperlink.detectors.TableHyperlinksToKeywordsDetector;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.DISectionEditorPart;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.ISectionEditorPart;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotFormEditor;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.SelectionLayerAccessor;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.handler.OpenDeclarationForTablesHandler.E4OpenDeclarationForTablesHandler;
import org.robotframework.red.commands.DIParameterizedHandler;

public class OpenDeclarationForTablesHandler extends DIParameterizedHandler<E4OpenDeclarationForTablesHandler> {

    public OpenDeclarationForTablesHandler() {
        super(E4OpenDeclarationForTablesHandler.class);
    }

    public static class E4OpenDeclarationForTablesHandler {

        @Execute
        public void openDeclarationForTables(final @Named(ISources.ACTIVE_EDITOR_NAME) RobotFormEditor editor) {
            final IEditorPart editorPart = editor.getActiveEditor();
            if (editorPart instanceof DISectionEditorPart) {
                @SuppressWarnings("unchecked")
                final DISectionEditorPart<ISectionEditorPart> part = (DISectionEditorPart<ISectionEditorPart>) editorPart;
                final List<ITableHyperlinksDetector> detectors = part.getDetectors();
                final SelectionLayerAccessor accessor = part.getSelectionLayerAccessor();
                final PositionCoordinate[] positions = accessor.getSelectedPositions();
                if (positions.length == 1) {
                    final int row = positions[0].getRowPosition();
                    final int column = positions[0].getColumnPosition();
                    final String label = accessor.getLabelFromCell(row, column);
                    final Optional<IHyperlink> hyperlink = getHyperlink(row, column, label, detectors);
                    if (hyperlink.isPresent()) {
                        hyperlink.get().open();
                    }
                }
            }
        }
    }

    private static Optional<IHyperlink> getHyperlink(final int row, final int column, final String label,
            final List<ITableHyperlinksDetector> detectors) {
        final java.util.Optional<TableHyperlinksToKeywordsDetector> keywordDetector = detectors.stream()
                .filter(TableHyperlinksToKeywordsDetector.class::isInstance)
                .map(TableHyperlinksToKeywordsDetector.class::cast)
                .findFirst();
        if (keywordDetector.isPresent()) {
            final Optional<IHyperlink> hyperlink = detect(row, column, label, keywordDetector.get());
            if (hyperlink.isPresent()) {
                return hyperlink;
            }
        }
        for (final ITableHyperlinksDetector detector : detectors) {
            final Optional<IHyperlink> hyperlink = detect(row, column, label, detector);
            if (hyperlink.isPresent()) {
                return hyperlink;
            }
        }
        return Optional.empty();
    }

    private static Optional<IHyperlink> detect(final int row, final int column, final String label,
            final ITableHyperlinksDetector detector) {
        final List<IHyperlink> hyperlinks = detector.detectHyperlinks(row, column, label, 0);
        if (hyperlinks != null && hyperlinks.size() > 0) {
            return Optional.of(hyperlinks.get(0));
        }
        return Optional.empty();
    }
}
