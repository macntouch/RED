/*
 * Copyright 2019 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.editor.libraries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.junit.Rule;
import org.junit.Test;
import org.robotframework.red.junit.ShellProvider;
import org.robotframework.red.swt.SwtThread;

public class ReferencedLibraryArgumentsDialogTest {

    @Rule
    public ShellProvider shell = new ShellProvider();

    @Test
    public void entryDialogProperlyGeneratesArguments_whenProvidedInTextBox() throws Exception {
        final AtomicBoolean finished = new AtomicBoolean(false);
        final AtomicReference<ReferencedLibraryArgumentsDialog> dialog = new AtomicReference<>(null);

        final Thread guiChangesRequestingThread = new Thread(() -> {
            SwtThread.asyncExec(() -> {
                dialog.set(new ReferencedLibraryArgumentsDialog(shell.getShell()));
                dialog.get().open();
                finished.set(true);
            });
            SwtThread.asyncExec(() -> {
                dialog.get().getText().setText("1::2::3::4");
                dialog.get().getOkButton().notifyListeners(SWT.Selection, new Event());
            });
        });
        guiChangesRequestingThread.start();
        guiChangesRequestingThread.join();

        while (!finished.get()) {
            Thread.sleep(100);
            while (shell.getShell().getDisplay().readAndDispatch()) {
                // handle all events coming to UI
            }
        }
        assertThat(dialog.get().getCreatedElement().getArgsStream()).containsExactly("1", "2", "3", "4");
    }
}
