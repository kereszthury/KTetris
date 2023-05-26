package tetris

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.stage.Stage


class TetrisApplication : Application() {

    companion object {
        private const val windowWidth = 350.0
        private const val windowHeight = 650.0
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private lateinit var button: Button

    private var lastFrameTimeNs: Long = 0

    override fun start(mainStage: Stage) {
        mainStage.title = "Tetris"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        // Add in canvas
        val canvas = Canvas(windowWidth, windowHeight)
        root.children.add(canvas)
        graphicsContext = canvas.graphicsContext2D

        // Add in start button
        button = Button("Start")
        button.setOnAction {
            Game.start()
            canvas.requestFocus()
        }
        button.layoutX = 50.0
        button.layoutY = 575.0
        button.prefWidth = 100.0
        button.prefHeight = 50.0
        button.style = "-fx-font-size: 20px;"
        root.children.add(button)

        mainScene.setOnKeyPressed { handleInput(it) }

        // Start the game loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                update(currentNanoTime)
            }
        }.start()

        Game.drawOffset = Vector(25, 100)
        mainStage.isResizable = false
        mainStage.show()
    }

    // Game loop
    private fun update(currentTimeNS: Long) {
        val elapsedMs = (currentTimeNS - lastFrameTimeNs) / 1_000_000
        if (elapsedMs < Game.updateIntervalMs) return
        lastFrameTimeNs = currentTimeNS

        Game.tick()
        drawGraphics()
    }

    // Refreshes the screen
    private fun drawGraphics() {
        button.isDisable = Game.started

        graphicsContext.clearRect(0.0, 0.0, windowWidth, windowHeight)
        drawTexts()
        Game.draw(graphicsContext)
    }

    // Passes the keyboard input to the game and refreshes the screen
    private fun handleInput(e: KeyEvent) {
        Game.handleInput(e)
        drawGraphics()
    }

    // Draws out the texts on the screen
    private fun drawTexts() {
        // Draw title
        graphicsContext.fill = Color.RED
        graphicsContext.stroke = Color.BLACK
        graphicsContext.lineWidth = 2.0
        graphicsContext.font = Font.font("Times New Roman", FontWeight.BOLD, 58.0)
        graphicsContext.textAlign = TextAlignment.CENTER
        graphicsContext.fillText("TETRIS!", 175.0, 60.0)
        graphicsContext.strokeText("TETRIS!", 175.0, 60.0)
        // Draw score text
        graphicsContext.font = Font.font("Times New Roman", FontWeight.BOLD, 24.0)
        graphicsContext.textAlign = TextAlignment.CENTER
        graphicsContext.fill = Color.GREEN
        graphicsContext.fillText("SCORE", 250.0, 595.0)
        // Draw score value
        graphicsContext.font = Font.font("Times New Roman", FontWeight.BOLD, 30.0)
        graphicsContext.fill = Color.BLUE
        graphicsContext.fillText("${Game.points}", 250.0, 625.0)
    }
}

fun main() {
    Application.launch(TetrisApplication::class.java)
}