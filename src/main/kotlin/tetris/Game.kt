package tetris

import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import kotlin.random.Random

object Game {
    val gameScale = 30.0
    private val gridSize = Vector(10, 15)

    private var updateIntervalMs = 500

    var started = false
        private set

    var points = 0
        private set

    var activeBlock: Block? = null
        private set

    private val droppedBlocks = mutableListOf<Block>()
    private val startBlocks = listOf(
        {x: Int, y: Int -> OBlock(x,y)},
        {x: Int, y: Int -> IBlock(x,y)},
        {x: Int, y: Int -> SBlock(x,y)},
        {x: Int, y: Int -> ZBlock(x,y)},
        {x: Int, y: Int -> TBlock(x,y)},
        {x: Int, y: Int -> LBlock(x,y)},
        {x: Int, y: Int -> FBlock(x,y)},
    )

    private val occupiedSlots = arrayOfNulls<BlockPart?>(gridSize.x * gridSize.y)

    fun areSlotsFree(vararg slots: Vector): Boolean {
        for (slot in slots) {
            if (getSlot(slot) != null) return false
        }
        return true
    }

    fun lockSlots(block: Block) {
        for (part in block.parts) {
            if (!part.destroyed) {
                val vector = part.offset + block.position
                if (vectorsInGrid(vector)) {
                    occupiedSlots[gridSize.x * vector.y + vector.x] = part
                }
            }
        }
    }

    fun unlockSlots(block: Block) {
        for (index in occupiedSlots.indices) {
            if (block.parts.contains(occupiedSlots[index])){
                occupiedSlots[index] = null
            }
        }
    }

    fun vectorsInGrid(vararg positions: Vector): Boolean {
        for (position in positions) {
            if (position.x < 0 || position.x >= gridSize.x || position.y < 0){
                return false
            }
        }
        return true
    }

    fun getSlot(position: Vector): BlockPart? {
        return if (!vectorsInGrid(position)) null
        else occupiedSlots[gridSize.x * position.y + position.x]
    }

    fun start() {
        started = true
        points = 0
        droppedBlocks.clear()
        activeBlock = null
    }

    fun tick() {
        if (!started) return

        if (activeBlock == null)  getNewBlock()

        if (!activeBlock!!.dropDown()) {
            activeBlock = null
            // TODO check for destroyed blocks, clear list if all parts destroyed

            /*var yCheck = gridSize.y
            while (yCheck >= 0) {
                // TODO MAKE A 2D GRID WHERE BLOCKPARTS CAN LOCK THE PLACE BEFOR MOVEMENT BLOCK UNLOCKS ITS POSITION THEN CHECKS IF CAN MOVE MOVE AFTER THAT LOCK ITS PLACE arrayOf[][]Blockpart
            }*/
        }
    }

    // Drops the block as down as possible
    fun dropToBottom() {
        if (activeBlock == null) return
        while (true) {
            if (!activeBlock!!.dropDown()) {
                activeBlock = null
                return
            }
        }
    }

    private fun getNewBlock() {
        activeBlock = startBlocks[Random.nextInt(from = 0, until = startBlocks.size)].invoke(gridSize.x / 2 - 1, -2)
        droppedBlocks.add(activeBlock!!)

        if (!canMoveTo(activeBlock!!) { vector -> (vector + Vector.down()) }){
            // TODO game over
            started = false
        }
    }

    fun canMoveTo(block: Block?, translate: (Vector) -> (Vector)): Boolean {
        if (block == null) return false

        /*var copy = block.parts.clone()
        copy.map { translate }

        if (vectorsInGrid(copy))*/

        for (part in block.parts) {
            val nextPartPosition = block.position + translate(part.offset)

            if (nextPartPosition.x < 0 || nextPartPosition.x >= gridSize.x || nextPartPosition.y >= gridSize.y){
                return false
            }

            for (droppedBlock in droppedBlocks) {
                if (droppedBlock == block) continue

                for (droppedPart in droppedBlock.parts) {
                    if (droppedPart.destroyed) continue

                    val droppedPartPosition = droppedBlock.position + droppedPart.offset
                    if (droppedPartPosition == nextPartPosition) {
                        return false
                    }
                }
            }
        }

        return true
    }

    fun handleInput(e: KeyEvent) {
        if (activeBlock == null) return

        // TODO put in map
        when (e.code) {
            KeyCode.LEFT -> {
                if (canMoveTo(activeBlock) { vector -> (vector + Vector.left()) }) {
                    activeBlock?.move(Vector.left())
                }
            }
            KeyCode.RIGHT -> {
                if (canMoveTo(activeBlock) { vector -> (vector + Vector.right()) }) {
                    activeBlock?.move(Vector.right())
                }
            }
            KeyCode.DOWN -> {
                if (canMoveTo(activeBlock, Vector::rotateRight)) {
                    activeBlock?.rotate(true)
                }
            }
            KeyCode.UP -> {
                if (canMoveTo(activeBlock, Vector::rotateLeft)) {
                    activeBlock?.rotate(false)
                }
            }
            KeyCode.SPACE -> {
                dropToBottom()
            }

            else -> {}
        }
    }

    fun draw(gc: GraphicsContext) {
        for (block in droppedBlocks) {
            block.draw(gc)
        }
        drawGrid(gc)
    }

    private fun drawGrid(gc: GraphicsContext) {
        gc.stroke = Color.BLACK
        gc.fill = Color.BLACK
        gc.lineWidth = gameScale / 15.0

        for (x in 0 until gridSize.x) {
            for (y in 0 until gridSize.y) {
                gc.strokeRect(gameScale * x, gameScale * y, gameScale, gameScale)

                if (getSlot(Vector(x,y)) != null) gc.fillRect(gameScale * x, gameScale * y, gameScale, gameScale)
            }
        }
    }
}