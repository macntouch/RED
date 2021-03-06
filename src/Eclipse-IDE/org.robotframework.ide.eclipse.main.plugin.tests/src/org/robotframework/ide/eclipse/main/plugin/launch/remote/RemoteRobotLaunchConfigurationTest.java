/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.launch.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.junit.Rule;
import org.junit.Test;
import org.robotframework.ide.eclipse.main.plugin.RedPreferences;
import org.robotframework.red.junit.PreferenceUpdater;
import org.robotframework.red.junit.ProjectProvider;
import org.robotframework.red.junit.RunConfigurationProvider;

public class RemoteRobotLaunchConfigurationTest {

    private static final String PROJECT_NAME = RemoteRobotLaunchConfigurationTest.class.getSimpleName();

    @Rule
    public ProjectProvider projectProvider = new ProjectProvider(PROJECT_NAME);

    @Rule
    public RunConfigurationProvider runConfigurationProvider = new RunConfigurationProvider(
            RemoteRobotLaunchConfiguration.TYPE_ID);

    @Rule
    public PreferenceUpdater preferenceUpdater = new PreferenceUpdater();

    @Test
    public void defaultConfigurationObtained_whenDefaultConfigurationPrepared() throws CoreException {
        final ILaunchConfigurationWorkingCopy config = RemoteRobotLaunchConfiguration
                .prepareDefault(projectProvider.getProject());
        final RemoteRobotLaunchConfiguration robotConfig = new RemoteRobotLaunchConfiguration(config);

        assertThat(config.getType()).isEqualTo(runConfigurationProvider.getType());
        assertThat(robotConfig.getProjectName()).isEqualTo(PROJECT_NAME);
        assertThat(robotConfig.isUsingRemoteAgent()).isTrue();
        assertThat(robotConfig.getAgentConnectionHost()).isEqualTo("127.0.0.1");
        assertThat(robotConfig.getAgentConnectionPort()).isBetween(1, 65_535);
        assertThat(robotConfig.getAgentConnectionTimeout()).isEqualTo(30);
        assertThat(robotConfig.getConfigurationVersion())
                .isEqualTo(RemoteRobotLaunchConfiguration.CURRENT_CONFIGURATION_VERSION);
    }

    @Test
    public void defaultConfigurationObtained_whenCustomConfigurationIsFilledWithDefaults() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();

        robotConfig.setAgentConnectionHostValue("1.2.3.4");
        robotConfig.setAgentConnectionPortValue("987");
        robotConfig.setAgentConnectionTimeoutValue("123");

        robotConfig.fillDefaults();

        assertThat(robotConfig.getProjectName()).isEqualTo("");
        assertThat(robotConfig.isUsingRemoteAgent()).isTrue();
        assertThat(robotConfig.getAgentConnectionHost()).isEqualTo("127.0.0.1");
        assertThat(robotConfig.getAgentConnectionPort()).isEqualTo(43_981);
        assertThat(robotConfig.getAgentConnectionTimeout()).isEqualTo(30);
        assertThat(robotConfig.getConfigurationVersion())
                .isEqualTo(RemoteRobotLaunchConfiguration.CURRENT_CONFIGURATION_VERSION);
    }

    @Test
    public void defaultConfigurationObtained_whenDefaultValuesAreDefinedInPreferences() throws Exception {
        preferenceUpdater.setValue(RedPreferences.LAUNCH_AGENT_CONNECTION_HOST, "some.host.com.pl");
        preferenceUpdater.setValue(RedPreferences.LAUNCH_AGENT_CONNECTION_PORT, 12345);
        preferenceUpdater.setValue(RedPreferences.LAUNCH_AGENT_CONNECTION_TIMEOUT, 666);

        final ILaunchConfigurationWorkingCopy config = RemoteRobotLaunchConfiguration
                .prepareDefault(projectProvider.getProject());
        final RemoteRobotLaunchConfiguration robotConfig = new RemoteRobotLaunchConfiguration(config);

        assertThat(config.getType()).isEqualTo(runConfigurationProvider.getType());
        assertThat(robotConfig.getProjectName()).isEqualTo(PROJECT_NAME);
        assertThat(robotConfig.isUsingRemoteAgent()).isTrue();
        assertThat(robotConfig.getAgentConnectionHost()).isEqualTo("some.host.com.pl");
        assertThat(robotConfig.getAgentConnectionPort()).isEqualTo(12345);
        assertThat(robotConfig.getAgentConnectionTimeout()).isEqualTo(666);
        assertThat(robotConfig.getConfigurationVersion())
                .isEqualTo(RemoteRobotLaunchConfiguration.CURRENT_CONFIGURATION_VERSION);
    }

    @Test
    public void whenServerIpIsEmpty_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionHostValue("");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionHost)
                .withMessage("Server IP cannot be empty")
                .withNoCause();
    }

    @Test
    public void whenPortIsEmpty_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionPortValue("");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionPort)
                .withMessage("Server port '' must be an Integer between 1 and %,d", 65_535)
                .withNoCause();
    }

    @Test
    public void whenPortIsNotANumber_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionPortValue("abc");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionPort)
                .withMessage("Server port 'abc' must be an Integer between 1 and %,d", 65_535)
                .withNoCause();
    }

    @Test
    public void whenPortIsBelowRange_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionPortValue("0");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionPort)
                .withMessage("Server port '0' must be an Integer between 1 and %,d", 65_535)
                .withNoCause();
    }

    @Test
    public void whenPortIsAboveRange_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionPortValue("65536");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionPort)
                .withMessage("Server port '65536' must be an Integer between 1 and %,d", 65_535)
                .withNoCause();
    }

    @Test
    public void whenTimeoutIsEmpty_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionTimeoutValue("");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionTimeout)
                .withMessage("Connection timeout '' must be an Integer between 1 and %,d", 3_600)
                .withNoCause();
    }

    @Test
    public void whenTimeoutIsNotANumber_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionTimeoutValue("abc");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionTimeout)
                .withMessage("Connection timeout 'abc' must be an Integer between 1 and %,d", 3_600)
                .withNoCause();
    }

    @Test
    public void whenTimeoutIsBelowRange_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionTimeoutValue("0");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionTimeout)
                .withMessage("Connection timeout '0' must be an Integer between 1 and %,d", 3_600)
                .withNoCause();
    }

    @Test
    public void whenTimeoutIsAboveRange_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionTimeoutValue("3601");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getAgentConnectionTimeout)
                .withMessage("Connection timeout '3601' must be an Integer between 1 and %,d", 3_600)
                .withNoCause();
    }

    @Test
    public void remoteSettingsAreCorrect_whenSet() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setAgentConnectionHostValue("192.168.1.21");
        robotConfig.setAgentConnectionPortValue("1234");
        robotConfig.setAgentConnectionTimeoutValue("567");
        assertThat(robotConfig.getAgentConnectionHost()).isEqualTo("192.168.1.21");
        assertThat(robotConfig.getAgentConnectionPort()).isEqualTo(1234);
        assertThat(robotConfig.getAgentConnectionTimeout()).isEqualTo(567);
    }

    @Test
    public void robotProjectObtainedFromConfiguration_whenProjectInWorkspace() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        assertThat(robotConfig.getProject()).isEqualTo(projectProvider.getProject());
    }

    @Test
    public void whenProjectNotInWorkspace_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setProjectName("not_existing");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getProject)
                .withMessage("Project 'not_existing' cannot be found in workspace")
                .withNoCause();
    }

    @Test
    public void whenProjectIsClosed_coreExceptionIsThrown() throws CoreException {
        projectProvider.getProject().close(null);

        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getProject)
                .withMessage("Project '%s' is currently closed", PROJECT_NAME)
                .withNoCause();
    }

    @Test
    public void whenProjectIsEmpty_coreExceptionIsThrown() throws CoreException {
        final RemoteRobotLaunchConfiguration robotConfig = getDefaultRemoteRobotLaunchConfiguration();
        robotConfig.setProjectName("");

        assertThatExceptionOfType(CoreException.class).isThrownBy(robotConfig::getProject)
                .withMessage("Project cannot be empty")
                .withNoCause();
    }

    private RemoteRobotLaunchConfiguration getDefaultRemoteRobotLaunchConfiguration() throws CoreException {
        return new RemoteRobotLaunchConfiguration(
                RemoteRobotLaunchConfiguration.prepareDefault(projectProvider.getProject()));
    }
}
