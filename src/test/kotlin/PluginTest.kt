import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.openFile
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.common.welcomeScreen
import com.intellij.driver.sdk.ui.components.elements.button
import com.intellij.driver.sdk.ui.components.elements.dialog
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.config.ConfigurationStorage
import com.intellij.ide.starter.config.splitMode
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.driver.remoteDev.RemDevDriverRunner
import com.intellij.ide.starter.driver.engine.DriverRunner
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.project.NoProject
import com.intellij.ide.starter.runner.RemDevTestContainer
import com.intellij.ide.starter.runner.Starter
import com.intellij.ide.starter.runner.TestContainer
import com.intellij.ide.starter.runner.TestContainerImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes
import org.kodein.di.DI
import org.kodein.di.bindProvider
import kotlin.booleanArrayOf

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

    @ParameterizedTest(name = "split-mode={0}")
    @ValueSource(booleans = [false, true])
    fun oneMoreTest(splitMode: Boolean) {
        if (splitMode) {
            di = DI {
                extend(di)
                bindProvider<TestContainer<*>>(overrides = true) { TestContainer.newInstance<RemDevTestContainer>() }
                bindProvider<DriverRunner> { RemDevDriverRunner() }
            }
        }

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