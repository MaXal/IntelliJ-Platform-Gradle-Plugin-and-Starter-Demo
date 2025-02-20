import com.intellij.driver.client.*
import com.intellij.driver.sdk.*
import com.intellij.ide.starter.ci.*
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.*
import org.kodein.di.*
import kotlin.io.path.Path


class PluginTest {
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
