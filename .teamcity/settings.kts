import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.UntrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubAppConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.untrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.triggers.schedule
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.12"

project {
    description = "REST API client written in Kotlin(copy of rest client)"

    buildType(Tw92045tmp_Build)
    buildType(Tw92045tmp_SecurityCheck)

    features {
        untrustedBuildsSettings {
            id = "PROJECT_EXT_1"
            defaultAction = UntrustedBuildsSettings.DefaultAction.APPROVE
            enableLog = true
            approvalRules = "user:ilya.voronin"
            manualRunsApproved = true
        }
        githubAppConnection {
            id = "PROJECT_EXT_32"
            displayName = "tc-local-werqwerqerqw"
            appId = "1165912"
            clientId = "Iv23liVoWFSZd2iHOqjn"
            clientSecret = "credentialsJSON:fb58209e-03bc-4d34-b3de-5f3011fa00ff"
            privateKey = "credentialsJSON:05e33569-669e-477a-8e7c-cdb85ed2d8cd"
            webhookSecret = "credentialsJSON:76f6dfe4-604f-42e6-a929-47f3d89fa187"
            ownerUrl = "https://github.com/t120891923"
            useUniqueCallback = true
        }
    }
}

object Tw92045tmp_Build : BuildType({
    id("Build")
    name = "Build and test"

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Fix env variables"
            scriptContent = "ls"
        }
    }

    features {
        pullRequests {
            provider = github {
                authType = vcsRoot()
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                ignoreDrafts = true
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})

object Tw92045tmp_SecurityCheck : BuildType({
    id("SecurityCheck")
    name = "Security check with Qodana"

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Fix env variables"
            scriptContent = "ls"
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:<default>
                +pr: github_role=member
                +pr: github_role=collaborator
            """.trimIndent()
        }
        schedule {
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false
            enableQueueOptimization = false
        }
    }

    features {
        pullRequests {
            provider = github {
                authType = vcsRoot()
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                ignoreDrafts = true
            }
        }
    }

    dependencies {
        snapshot(Tw92045tmp_Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
