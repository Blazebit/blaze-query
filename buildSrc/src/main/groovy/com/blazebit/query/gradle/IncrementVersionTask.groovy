package com.blazebit.query.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class IncrementVersionTask extends DefaultTask {

    @TaskAction
    def incrementVersion() {
        def propertiesFile = project.rootProject.file('gradle.properties')
        if (!propertiesFile.exists()) {
            throw new GradleException("gradle.properties file not found in the root directory")
        }

        def properties = new Properties()
        properties.load(propertiesFile.newInputStream())

        String currentVersion = properties['version']
        if (currentVersion == null) {
            throw new GradleException("No 'version' property found in gradle.properties")
        }

        def newVersion = increment(currentVersion)

        properties['version'] = newVersion
        properties.store(propertiesFile.newOutputStream(), null)

        println "Updated version from ${currentVersion} to ${newVersion}"
    }

    private static String increment(String currentVersion) {
        boolean isSnapshot = currentVersion.endsWith("-SNAPSHOT")
        if (isSnapshot) {
            currentVersion = currentVersion.replace("-SNAPSHOT", "")
        }

        def versionParts = currentVersion.split('\\.')
        if (versionParts.length != 3) {
            throw new GradleException("Version format should be 'major.minor.patch' or 'major.minor.patch-SNAPSHOT'")
        }

        def patchVersion = versionParts[2].toInteger() + 1
        return "${versionParts[0]}.${versionParts[1]}.${patchVersion}-SNAPSHOT"

    }
}