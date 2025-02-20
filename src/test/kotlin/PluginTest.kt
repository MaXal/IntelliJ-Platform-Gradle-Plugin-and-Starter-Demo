import com.intellij.driver.client.service
import com.intellij.driver.client.utility
import com.intellij.driver.sdk.singleProject
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import kotlin.io.path.Path


class PluginTest {

    init {
        di = DI {
            extend(di)
            bindSingleton<CIServer>(overrides = true) {
                object : CIServer by NoCIServer {
                    override fun reportTestFailure(
                        testName: String,
                        message: String,
                        details: String,
                        linkToLogs: String?
                    ) {
                        fail { "$testName fails: $message. \n$details" }
                    }
                }
            }
        }
    }

    @Test
    fun testStubs() {
        Starter.newContext(
            testName = "testExample", TestCase(
                IdeProductProvider.IC, projectInfo = GitHubProject.fromGithub(
                    branchName = "master",
                    repoRelativeUrl = "JetBrains/ij-perf-report-aggregator"
                )
            ).withVersion("2024.2")
        ).apply {
            val pathToPlugin = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromPath(Path(pathToPlugin))
        }.runIdeWithDriver().useDriverAndCloseIde {
            val storage = utility<PluginStorage>().getPluginStorage()
            val key = storage.getKey()
            val attributes = storage.getAttributes()
            Assertions.assertEquals("static method", key)
            Assertions.assertEquals(listOf("static1", "static2"), attributes)

            val answer = service<PluginService>().getAnswer()
            Assertions.assertEquals(42, answer)

            waitForProjectOpen()
            val project = singleProject()
            val strings = service<PluginProjectService>(project).getStrings()
            Assertions.assertArrayEquals(arrayOf("foo", "bar"), strings)
        }
    }
}
