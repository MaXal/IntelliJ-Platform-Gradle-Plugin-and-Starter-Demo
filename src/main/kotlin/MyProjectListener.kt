import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity


class DumbModeFinishedListener : StartupActivity {

    override fun runActivity(project: Project) {

        DumbService.getInstance(project).smartInvokeLater {
            throw Exception("DumbMode")
        }
    }
}
