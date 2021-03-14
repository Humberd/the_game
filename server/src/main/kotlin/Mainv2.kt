import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType


fun main() {
    val config = LwjglApplicationConfiguration()
    LwjglApplication(PhysicsGame(), config)
}

class PhysicsGame : ApplicationAdapter() {
    lateinit var world: World
    var camera: OrthographicCamera? = null
    var debugRenderer: Box2DDebugRenderer? = null
    override fun create() {
        world = World(Vector2(0f, -10f), true)
        camera = OrthographicCamera(50f, 25f)
        debugRenderer = Box2DDebugRenderer()

        // ground
        createEdge(BodyType.StaticBody, -20f, -10f, 20f, -10f, 0f)
        // left wall
        createEdge(BodyType.StaticBody, -20f, -10f, -20f, 10f, 0f)
        // right wall
        createEdge(BodyType.StaticBody, 20f, -10f, 20f, 10f, 0f)
        createCircle(BodyType.DynamicBody, 0f, 0f, 1f, 3f)
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                val touchedPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                camera!!.unproject(touchedPoint)
                if (MathUtils.randomBoolean()) {
                    createBox(BodyType.DynamicBody, touchedPoint.x, touchedPoint.y, 1f, 1f, 1f)
                } else {
                    createCircle(BodyType.DynamicBody, touchedPoint.x, touchedPoint.y, 1f, 3f)
                }
                return true
            }
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(.125f, .125f, .125f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        debugRenderer!!.render(world, camera!!.combined)
        world.step(1 / 60f, 6, 2)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer!!.dispose()
    }

    private fun createBox(type: BodyType, x: Float, y: Float, width: Float, height: Float, density: Float): Body {
        val poly = PolygonShape()
        poly.setAsBox(width, height)
        val def = BodyDef()
        def.type = type
        val body: Body = world.createBody(def)
        body.createFixture(poly, density)
        body.setTransform(x, y, 0f)
        poly.dispose()
        return body
    }

    private fun createEdge(type: BodyType, x1: Float, y1: Float, x2: Float, y2: Float, density: Float): Body {
        val poly = EdgeShape()
        poly[Vector2(0f, 0f)] = Vector2(x2 - x1, y2 - y1)
        val def = BodyDef()
        def.type = type
        val body: Body = world.createBody(def)
        body.createFixture(poly, density)
        body.setTransform(x1, y1, 0f)
        poly.dispose()
        return body
    }

    private fun createCircle(type: BodyType, x: Float, y: Float, radius: Float, density: Float): Body {
        val poly = CircleShape()
        poly.radius = radius
        val def = BodyDef()
        def.type = type
        val body: Body = world.createBody(def)
        body.createFixture(poly, density)
        body.setTransform(x, y, 0f)
        poly.dispose()
        return body
    }
}
