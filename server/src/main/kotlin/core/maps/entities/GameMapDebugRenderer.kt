package core.maps.entities

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.box2d.World
import kotlin.concurrent.thread

class GameMapDebugRenderer(
    private val navigation: GameMapNavigation,
    private val physics: World
) : ApplicationAdapter() {
    lateinit var camera: OrthographicCamera
    lateinit var debugRenderer: Renderer

    init {
        thread(isDaemon = true) {
            Lwjgl3Application(this, Lwjgl3ApplicationConfiguration().also {
                val secondMonitor = Lwjgl3ApplicationConfiguration.getMonitors()[1]
                it.setWindowPosition(secondMonitor.virtualX + 30, secondMonitor.virtualY + 150)
                it.setWindowedMode(800, 800)
            })
        }

    }

    override fun create() {
        camera = OrthographicCamera(25f, 25f)
        camera.translate(10f, 10f)
        camera.update()
        camera.projection.mul(Matrix4().scale(1f, -1f, 1f))
        camera.combined.set(camera.projection)
        Matrix4.mul(camera.combined.`val`, camera.view.`val`)
        debugRenderer = Renderer(navigation.navMesh)
    }

    override fun render() {
        Gdx.gl.glClearColor(.125f, .125f, .125f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        debugRenderer.render(physics, camera.combined)
    }

    override fun dispose() {
        debugRenderer.dispose()
    }
}
