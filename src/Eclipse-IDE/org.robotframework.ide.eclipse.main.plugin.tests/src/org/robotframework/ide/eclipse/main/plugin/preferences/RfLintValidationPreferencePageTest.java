/*
* Copyright 2017 Nokia Solutions and Networks
* Licensed under the Apache License, Version 2.0,
* see license.txt file for details.
*/
package org.robotframework.ide.eclipse.main.plugin.preferences;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.junit.Rule;
import org.junit.Test;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;
import org.robotframework.ide.eclipse.main.plugin.RedPreferences;
import org.robotframework.red.junit.Controls;
import org.robotframework.red.junit.PreferenceUpdater;
import org.robotframework.red.junit.ShellProvider;

public class RfLintValidationPreferencePageTest {

    @Rule
    public ShellProvider shellProvider = new ShellProvider();

    @Rule
    public PreferenceUpdater preferenceUpdater = new PreferenceUpdater();

    @Test
    public void initDoesNothing() {
        final IWorkbench workbench = mock(IWorkbench.class);

        final RfLintValidationPreferencePage page = new RfLintValidationPreferencePage();
        page.init(workbench);

        verifyZeroInteractions(workbench);
    }

    @Test
    public void singleTreeIsPlacedAtThePage() {
        final RfLintValidationPreferencePage page = new RfLintValidationPreferencePage();
        page.createControl(shellProvider.getShell());

        final List<Tree> tables = getTrees();
        assertThat(tables).hasSize(1);
    }

    @Test
    public void oneTextFieldIsPlacedAtThePage() {
        final RfLintValidationPreferencePage page = new RfLintValidationPreferencePage();
        page.createControl(shellProvider.getShell());

        final List<Text> textFields = getTextFields();
        assertThat(textFields).hasSize(1);
    }

    @Test
    public void additionalArgumentsFieldDisplaysCustomArguments() {
        preferenceUpdater.setValue(RedPreferences.RFLINT_ADDITIONAL_ARGUMENTS, "custom arguments");

        final RfLintValidationPreferencePage page = new RfLintValidationPreferencePage();
        page.createControl(shellProvider.getShell());

        assertThat(getAdditionalArgumentsField().getText()).isEqualTo("custom arguments");
    }

    @Test
    public void valuesAndPreferencesAreCorrectlyUpdated() {
        preferenceUpdater.setValue(RedPreferences.RFLINT_RULES_FILES, "/path/to/fst.py;/path/to/snd.py");
        preferenceUpdater.setValue(RedPreferences.RFLINT_RULES_CONFIG_NAMES, "Rule1;Rule2");
        preferenceUpdater.setValue(RedPreferences.RFLINT_RULES_CONFIG_SEVERITIES, "WARNING;ERROR");
        preferenceUpdater.setValue(RedPreferences.RFLINT_RULES_CONFIG_ARGS, ";100");
        preferenceUpdater.setValue(RedPreferences.RFLINT_ADDITIONAL_ARGUMENTS, "custom arguments");

        final RfLintValidationPreferencePage page = new RfLintValidationPreferencePage();
        page.createControl(shellProvider.getShell());

        assertNotEmptyValues();
        assertNotEmptyPreferences();

        page.performDefaults();

        assertEmptyValues();
        assertNotEmptyPreferences();

        page.performOk();

        assertEmptyValues();
        assertEmptyPreferences();
    }

    private void assertNotEmptyValues() {
        assertThat(getRulesTree().getItemCount()).isGreaterThanOrEqualTo(0);
        assertThat(getAdditionalArgumentsField().getText()).isNotEmpty();
    }

    private void assertEmptyValues() {
        assertThat(getRulesTree().getItemCount()).isEqualTo(0);
        assertThat(getAdditionalArgumentsField().getText()).isEmpty();
    }

    private void assertNotEmptyPreferences() {
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintRulesConfigs()).isNotEmpty();
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintRulesFiles()).isNotEmpty();
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintAdditionalArguments()).isNotEmpty();
    }

    private void assertEmptyPreferences() {
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintRulesConfigs()).isEmpty();
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintRulesFiles()).isEmpty();
        assertThat(RedPlugin.getDefault().getPreferences().getRfLintAdditionalArguments()).isEmpty();
    }

    private Tree getRulesTree() {
        return getTrees().get(0);
    }

    private List<Tree> getTrees() {
        return Controls.getControlsStream(shellProvider.getShell())
                .filter(c -> c instanceof Tree)
                .map(c -> Tree.class.cast(c))
                .collect(toList());
    }

    private Text getAdditionalArgumentsField() {
        return getTextFields().get(0);
    }

    private List<Text> getTextFields() {
        return Controls.getControlsStream(shellProvider.getShell())
                .filter(c -> c instanceof Text)
                .map(c -> Text.class.cast(c))
                .collect(toList());
    }
}
