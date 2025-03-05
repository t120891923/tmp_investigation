some change 5

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.UntrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.projectFeatures.untrustedBuildsSettings
import jetbrains.buildServer.configs.kotlin.triggers.schedule
import jetbrains.buildServer.configs.kotlin.triggers.vcs

project {
    description = "REST API client written in Kotlin(copy of rest client)"

    features {
        untrustedBuildsSettings {
            manualRunsApproved = true
            approvalRules = "user:ilya.voronin" // TODO: return the "group:TEAMCITY_DEVELOP:1"
            defaultAction = UntrustedBuildsSettings.DefaultAction.APPROVE
            enableLog = true
        }
    }

    val buildAndTest = buildType {
        id("Build")
        name = "Build and test"
        requirements {
            equals("teamcity.agent.jvm.os.name", "Linux")
        }
        vcs {
            cleanCheckout = true
            root(DslContext.settingsRoot)
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

        steps {
            script {
                name = "Fix env variables"
                scriptContent = """
                ls
                """.trimIndent()
            }
        }
    }

    val securityCheck = buildType {
        id("SecurityCheck")
        name = "Security check with Qodana"

        vcs {
            cleanCheckout = true
            root(DslContext.settingsRoot)
        }

        requirements {
            equals("teamcity.agent.jvm.os.name", "Linux")
        }

        dependencies {
            snapshot(buildAndTest) {
                onDependencyFailure = FailureAction.FAIL_TO_START
                reuseBuilds = ReuseBuilds.SUCCESSFUL
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
                daily {
                    hour = 8
                }

                /* We want this build to run regardless of the new changes as new vulnerabilities are being discovered all the time*/
                withPendingChangesOnly = false
                enableQueueOptimization = false

                branchFilter = "+:<default>"
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

        steps {
            script {
                name = "Fix env variables"
                scriptContent = """
                ls
                """.trimIndent()
            }
        }
    }
}
