package core.maps.entities

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer

class GameMapDebugRenderer(private val gameMap: GameMap): ApplicationAdapter() {
    val camera: OrthographicCamera
    lateinit var debugRenderer: Box2DDebugRenderer

    init {
        LwjglApplication(this, LwjglApplicationConfiguration().also {
            it.height = 800
            it.width = 800
        })
        camera = OrthographicCamera(25f, 25f)
        camera.projection.mul(Matrix4().scale(1f, -1f, 1f))
        camera.translate(10f, 10f)
    }

    override fun create() {
        debugRenderer = Box2DDebugRenderer()
    }

    override fun render() {
        Gdx.gl.glClearColor(.125f, .125f, .125f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        debugRenderer.render(gameMap.physics, camera.combined)
    }

    override fun dispose() {
        debugRenderer.dispose()
    }
}
