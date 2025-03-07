import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.UntrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubAppConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.untrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.triggers.schedule
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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

    vcsRoot(Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_HttpsGithubComT120891923testRefsHeadsMain)

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
            id = "PROJECT_EXT_1572"
            displayName = "tc-bs-u8ijfsijfdjiaiudf"
            appId = "1169971"
            clientId = "Iv23lirOo9Pp1xBLehVb"
            clientSecret = "credentialsJSON:5537b48f-0c3f-4bb5-b9e8-59c0355d9e30"
            privateKey = "credentialsJSON:c7562c8a-40a9-4f1b-9c95-ef9937b70acb"
            webhookSecret = "credentialsJSON:6a50da6c-6bab-490a-a537-2182735404de"
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

object Sandbox_AntonZamolotskikhSandbox_TmpProjectTw92044_HttpsGithubComT120891923testRefsHeadsMain : GitVcsRoot({
    id("HttpsGithubComT120891923testRefsHeadsMain")
    name = "https://github.com/t120891923/test#refs/heads/main"
    url = "https://github.com/t120891923/test"
    branch = "refs/heads/main"
    authMethod = token {
        userName = "oauth2"
        tokenId = "tc_token_id:CID_2970263f4eac8f42ced81f6aa3517cc0:-1:de54f5b5-70e2-4b39-9ca2-bfee2b357d68"
    }
})
