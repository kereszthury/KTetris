package tetris

import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import kotlin.random.Random

object Game {
    private const val startUpdateTimeMs = 500
    private val grid = Grid(10, 15)

    // Variables used for drawing on canvas
    var drawOffset = Vector(0,0)
    var gameScale = 30.0

    var updateIntervalMs = startUpdateTimeMs
        private set

    var started = false
        private set

    var points = 0
        private set

    // The block that is currently falling
    private var activeBlock: Block? = null

    // List of different block types
    private val startBlocks = listOf(
        { x: Int, y: Int -> OBlock(x, y) },
        { x: Int, y: Int -> IBlock(x, y) },
        { x: Int, y: Int -> SBlock(x, y) },
        { x: Int, y: Int -> ZBlock(x, y) },
        { x: Int, y: Int -> TBlock(x, y) },
        { x: Int, y: Int -> LBlock(x, y) },
        { x: Int, y: Int -> FBlock(x, y) },
    )

    // Starts a new game
    fun start() {
        started = true
        updateIntervalMs = startUpdateTimeMs
        points = 0
        grid.clear()
        activeBlock = null
    }

    // Updates the game, drops down the block, spawns a new one if last got down, clears full lines
    fun tick() {
        if (!started) return

        if (activeBlock == null) getNewBlock()

        if (!dropDown()) {
            activeBlock = null
            checkForFullLines()
        }
    }

    // Drops the block as down as possible
    private fun dropToBottom() {
        if (activeBlock == null) return
        while (true) {
            if (!dropDown()) {
                activeBlock = null
                checkForFullLines()
                return
            }
        }
    }

    // Drops the active block down by one. If it is blocked, returns false
    private fun dropDown(): Boolean {
        if (activeBlock == null) return false
        grid.unlockCells(activeBlock!!)

        return if (grid.canGoTo(activeBlock) { vector -> vector + Vector.down() }) {
            activeBlock!!.move(Vector.down())
            grid.lockCells(activeBlock!!)
            true
        } else {
            grid.lockCells(activeBlock!!)
            false
        }
    }

    // Asks the grid to remove the full lines and if any is removed, increases score and game speed
    private fun checkForFullLines(){
        val destroyedLines = grid.destroyFullLines()
        points += grid.width * destroyedLines
        updateIntervalMs -= 20 * destroyedLines
    }

    // Spawns in a new block, if it can't drop, then it's game over
    private fun getNewBlock() {
        activeBlock = startBlocks[Random.nextInt(from = 0, until = startBlocks.size)].invoke(grid.width / 2 - 1, -2)
        grid.droppedBlocks.add(activeBlock!!)

        if (!grid.canGoTo(activeBlock!!) { vector -> (vector + Vector.down()) }) {
            gameOver()
        }
    }

    private fun gameOver() {
        started = false
    }

    // Class used to store actions to player input
    private class PlayerAction(val keyCode: KeyCode, val translate: (Vector) -> Vector, val method: (() -> Unit)?)

    private val actions = arrayOf(
        PlayerAction(KeyCode.LEFT, { vector -> (vector + Vector.left()) }, { activeBlock?.move(Vector.left()) }),
        PlayerAction(KeyCode.RIGHT, { vector -> (vector + Vector.right()) }, { activeBlock?.move(Vector.right()) }),
        PlayerAction(KeyCode.DOWN, Vector::rotateRight) { activeBlock?.rotate(true) },
        PlayerAction(KeyCode.UP, Vector::rotateLeft) { activeBlock?.rotate(false) },
        PlayerAction(KeyCode.SPACE, { vector -> (vector + Vector.down()) }, { dropToBottom() }),
    )

    // Keyboard input handling
    fun handleInput(e: KeyEvent) {
        if (activeBlock == null) return

        grid.unlockCells(activeBlock!!)

        val action = actions.find { a -> a.keyCode == e.code }
        if (action != null && grid.canGoTo(activeBlock, action.translate)) {
            action.method?.invoke()
        }

        if (activeBlock != null) grid.lockCells(activeBlock!!)
    }

    // Draws the game on the canvas
    fun draw(gc: GraphicsContext) {
        drawBlocks(gc)
        drawGrid(gc)
        drawOutline(gc)
    }

    // Draws the grid between cells
    private fun drawGrid(gc: GraphicsContext) {
        gc.stroke = Color.DARKGRAY
        gc.lineWidth = gameScale / 20.0

        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                gc.strokeRect(gameScale * x + drawOffset.x, gameScale * y + drawOffset.y, gameScale, gameScale)
            }
        }
    }

    // Draws out the blocks
    private fun drawBlocks(gc: GraphicsContext) {
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                val block = grid.getCell(Vector(x, y))
                if (block != null) {
                    gc.fill = block.color
                    gc.fillRect(gameScale * x + drawOffset.x, gameScale * y + drawOffset.y, gameScale, gameScale)
                }
            }
        }
    }

    // Draws out the outline
    private fun drawOutline(gc: GraphicsContext) {
        gc.stroke = Color.BLACK
        gc.lineWidth = gameScale / 5.0
        gc.strokeRect(0.0 + drawOffset.x, 0.0 + drawOffset.y, gameScale * grid.width, gameScale * grid.height)
    }
}