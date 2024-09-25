import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.openFile
import com.intellij.driver.sdk.ui.components.button
import com.intellij.driver.sdk.ui.components.dialog
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.components.welcomeScreen
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.project.NoProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

class PluginTest {
    @Test
    fun simpleTest() {
        Starter.newContext("testExample", TestCase(IdeProductProvider.IC, NoProject).withVersion("2024.2")).apply {
            val pathToPlugin = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromPath(Path(pathToPlugin))
        }.runIdeWithDriver().useDriverAndCloseIde {
            welcomeScreen {
                clickPlugins()
                x { byAccessibleName("Installed") }.click()
                shouldBe("Plugin is installed") {
                    x {
                        and(
                            byVisibleText("Demo"),
                            byJavaClass("javax.swing.JLabel")
                        )
                    }.present()
                }

            }
        }
    }

    @Test
    fun oneMoreTest() {
        Starter.newContext(
            "oneMoreTest",
            TestCase(
                IdeProductProvider.WS,
                GitHubProject.fromGithub(branchName = "master", repoRelativeUrl = "JetBrains/ij-perf-report-aggregator")
            ).useEAP()
        ).apply {
            setLicense(System.getenv("LICENSE_KEY"))
            val pathToPlugin = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromPath(Path(pathToPlugin))
        }.runIdeWithDriver().useDriverAndCloseIde {
            waitForIndicators(5.minutes)
            openFile("package.json")
            ideFrame {
                invokeAction("ShowDialogAction", now = false)
                dialog({ byTitle("Test Dialog") }) {
                    button("OK").click()
                }
            }
        }
    }
}