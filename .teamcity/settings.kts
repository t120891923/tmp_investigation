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

    buildType(Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_Build)
    buildType(Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_SecurityCheck)

    features {
        untrustedBuildsSettings {
            id = "PROJECT_EXT_1"
            defaultAction = UntrustedBuildsSettings.DefaultAction.APPROVE
            enableLog = true
            approvalRules = "user:ilya.voronin"
            manualRunsApproved = true
        }
        githubAppConnection {
            id = "PROJECT_EXT_3102"
            displayName = "tcbs-qwerqwerqwerqwersdfdv"
            appId = "1150953"
            clientId = "Iv23liTOZDqfWxnRGNT1"
            clientSecret = "credentialsJSON:0c9d1674-a618-467c-b1c3-e386a4a3b8a3"
            privateKey = "credentialsJSON:ffe87294-a15e-4a6e-aa18-68f146dc4d13"
            webhookSecret = "credentialsJSON:936bd040-6ca7-4cc4-b122-84910d3ffbf8"
            ownerUrl = "https://github.com/t120891923"
            useUniqueCallback = true
        }
    }
}

object Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_Build : BuildType({
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

object Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_SecurityCheck : BuildType({
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
        snapshot(Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
