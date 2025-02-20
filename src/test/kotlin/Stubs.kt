import com.intellij.driver.client.Remote

@Remote("org.jetbrains.testPlugin.PluginStorage", plugin = "org.example.demo")
interface PluginStorage{
    fun getPluginStorage(): Storage
}

@Remote("org.jetbrains.testPlugin.PluginService", plugin = "org.example.demo")
interface PluginService {
    fun getAnswer(): Int
}

@Remote("org.jetbrains.testPlugin.PluginProjectService", plugin = "org.example.demo")
interface PluginProjectService {
    fun getStrings(): Array<String>
}

@Remote("org.jetbrains.testPlugin.Storage", plugin = "org.example.demo")
interface Storage{
    fun getAttributes(): List<String>
    fun getKey(): String
}
