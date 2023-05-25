package tetris

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage


class TetrisApplication : Application() {

    companion object {
        private const val windowWidth = 512.0
        private const val windowHeight = 512.0
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private var lastFrameTimeNs: Long = 0
    private var updateIntervalMs = 500

    override fun start(mainStage: Stage) {
        mainStage.title = "Tetris"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(windowWidth, windowHeight)
        root.children.add(canvas)

        graphicsContext = canvas.graphicsContext2D

        mainScene.setOnKeyPressed { handleInput(it) }

        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                update(currentNanoTime)
            }
        }.start()

        mainStage.show()

        Game.start() // TODO place elsewhere
    }

    private fun update(currentTimeNS: Long) {
        val elapsedMs = (currentTimeNS - lastFrameTimeNs) / 1_000_000
        if (elapsedMs < updateIntervalMs) return
        lastFrameTimeNs = currentTimeNS

        // TODO update
        Game.tick()

        drawGraphics()
    }

    fun drawGraphics() {
        graphicsContext.clearRect(0.0, 0.0, windowWidth, windowHeight)

        Game.draw(graphicsContext)

        // TODO add ui
    }

    private fun handleInput(e: KeyEvent) {
        Game.handleInput(e)
        drawGraphics()
    }
}

fun main() {
    Application.launch(TetrisApplication::class.java)
}