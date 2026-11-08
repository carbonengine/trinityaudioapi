// Copyright © 2026 CCP ehf.

package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object BumpVcpkgRegistry : BuildType({
    name = "Bump vcpkg Registry"
    description = "Opens a draft PR in the vcpkg registry that bumps this component's port when a vMAJOR.MINOR.PATCH tag is created"

    enablePersonalBuilds = false
    maxRunningBuilds = 1

    params {
        param("vcpkg_port_name", "carbon-trinityaudioapi")
        param("github_registry_repository", "carbonengine/vcpkg-registry")
        param("git_user_name", "TeamCity")
        param("git_user_email", "teamcity@ccpgames.com")
        param("teamcity.vcsTrigger.runBuildInNewEmptyBranch", "true")
    }

    vcs {
        root(DslContext.settingsRootId, "+:. => component")
        root(AbsoluteId("CarbonVcpkgRegistry"), "+:. => registry")
        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            name = "Open registry PR for release tag"
            scriptContent = """
                set -euo pipefail

                tag="%teamcity.build.vcs.branch.${DslContext.settingsRootId.id}%"
                tag="${'$'}{tag#refs/tags/}"
                echo "${'$'}tag" | grep -Eq '^v[0-9]+\.[0-9]+\.[0-9]+${'$'}' || { echo "${'$'}tag is not a release tag"; exit 0; }
                version="${'$'}{tag#v}"
                branch="bump-%vcpkg_port_name%-${'$'}version"
                port="ports/%vcpkg_port_name%"

                apt-get update -qq && apt-get install -y -qq --no-install-recommends zip
                export GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=accept-new"
                git config --global --add safe.directory '*'
                git config --global user.name "%git_user_name%"
                git config --global user.email "%git_user_email%"

                vcpkg_root="%teamcity.build.checkoutDir%/component/vendor/github.com/microsoft/vcpkg"
                "${'$'}vcpkg_root/bootstrap-vcpkg.sh" -disableMetrics

                cd registry
                git switch -c "${'$'}branch"
                sed -i -E "0,/^( *\"version\": )\"[^\"]*\"/s//\1\"${'$'}version\"/" "${'$'}port/vcpkg.json"
                sed -i -E "s|^( *REF )[0-9a-f]{40}.*|\1%build.vcs.number.${DslContext.settingsRootId.id}% #${'$'}tag|" "${'$'}port/portfile.cmake"
                git commit -a -m "Create ${'$'}tag port of %vcpkg_port_name%"
                python3 tools/update_ports.py %vcpkg_port_name% --vcpkg-root "${'$'}vcpkg_root"
                git push origin "${'$'}branch"

                curl --fail-with-body -sS -X POST "https://api.github.com/repos/%github_registry_repository%/pulls" \
                    -H "Authorization: Bearer %GITHUB_CARBON_PAT%" \
                    -d "{\"title\": \"WIP: Bump %vcpkg_port_name% ${'$'}version\", \"head\": \"${'$'}branch\", \"base\": \"main\", \"draft\": true, \"body\": \"Automated bump for tag ${'$'}tag.\"}" \
                    | python3 -c 'import json, sys; url = json.load(sys.stdin)["html_url"]; print("##teamcity[buildStatus text=\x27{build.status.text}; Draft PR: " + url + "\x27]")'
            """.trimIndent()
            dockerImage = "python:3.12"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
        }
    }

    triggers {
        vcs {
            // Tags are reported as logical branch names (e.g. v3.2.0), which requires "Use tags as branches" on the VCS root
            branchFilter = """
                +:v*
            """.trimIndent()
        }
    }

    features {
        sshAgent {
            teamcitySshKey = "ccpgames-carbon"
        }
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }
})
