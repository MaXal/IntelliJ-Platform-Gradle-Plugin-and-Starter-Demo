import com.intellij.driver.sdk.ui.components.welcomeScreen
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.newContext
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class PluginTest {
    @Test
    fun simpleTest() {
        Starter.newContext(IdeProductProvider.IU, "testExample").apply {
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
}