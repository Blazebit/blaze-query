package com.blazebit.query.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class IncrementVersionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.tasks.register('incrementVersion', IncrementVersionTask)
    }
}
