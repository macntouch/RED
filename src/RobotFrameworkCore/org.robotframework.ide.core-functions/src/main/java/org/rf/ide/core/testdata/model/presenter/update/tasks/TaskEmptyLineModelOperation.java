/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.presenter.update.tasks;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.presenter.update.IExecutablesStepsHolderElementOperation;
import org.rf.ide.core.testdata.model.table.tasks.Task;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class TaskEmptyLineModelOperation implements IExecutablesStepsHolderElementOperation<Task> {

    @Override
    public boolean isApplicable(final ModelType elementType) {
        return elementType == ModelType.EMPTY_LINE;
    }

    @Override
    public boolean isApplicable(final IRobotTokenType elementType) {
        return elementType == RobotTokenType.EMPTY_CELL;
    }

    @Override
    public AModelElement<?> insert(final Task task, final int index, final AModelElement<?> modelElement) {
        return task.addElement(index, modelElement);
    }
}
