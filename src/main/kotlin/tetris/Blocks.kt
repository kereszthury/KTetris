package tetris

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

abstract class Block(x: Int, y: Int) {
    var position = Vector(x, y)
        private set

    open val color: Color = Color.BLACK

    abstract val parts: Array<BlockPart>

    // Drops the block down by one. If it is blocked, returns false
    fun dropDown(): Boolean {
        Game.unlockSlots(this)

        return if (Game.canMoveTo(this) { vector -> vector + Vector.down() }) {
            position.y++
            Game.lockSlots(this)
            true
        } else {
            Game.lockSlots(this)
            false
        }
    }

    // Forces the block to move, no matter if it is blocked
    fun move(direction: Vector) {
        Game.unlockSlots(this)
        position += direction
        Game.lockSlots(this)
    }

    open fun rotate(right: Boolean) {
        Game.unlockSlots(this)
        for (part in parts) {
            if (right) part.offset = Vector.rotateRight(part.offset)
            else part.offset = Vector.rotateLeft(part.offset)
        }
        Game.lockSlots(this)
    }

    fun draw(gc: GraphicsContext) {
        /*gc.fill = color
        for (part in parts) {
            if (part.destroyed) continue
            gc.fillRect(
                Game.gameScale * (position.x + part.offset.x),
                Game.gameScale * (position.y + part.offset.y),
                Game.gameScale,
                Game.gameScale
            )
        }*/
    }
}

class BlockPart(x: Int, y: Int) {
    var offset = Vector(x, y)
    var destroyed = false
}

class OBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.YELLOW

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, 0),
        BlockPart(0, 1),
        BlockPart(1, 0),
        BlockPart(1, 1),
    )

    override fun rotate(right: Boolean) {}
}

class IBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.BLUE

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, -2),
        BlockPart(0, -1),
        BlockPart(0, 0),
        BlockPart(0, 1),
    )
}

class SBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.GREEN

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, 0),
        BlockPart(1, 0),
        BlockPart(-1, 1),
        BlockPart(0, 1),
    )
}

class ZBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.RED

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, 0),
        BlockPart(-1, 0),
        BlockPart(1, 1),
        BlockPart(0, 1),
    )
}

class TBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.PURPLE

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(-1, 0),
        BlockPart(0, 0),
        BlockPart(1, 0),
        BlockPart(0, 1),
    )
}

class LBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.ORANGE

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, -1),
        BlockPart(0, 0),
        BlockPart(0, 1),
        BlockPart(1, 1),
    )
}

class FBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.DARKBLUE

    override val parts: Array<BlockPart> = arrayOf(
        BlockPart(0, -1),
        BlockPart(1, -1),
        BlockPart(0, 0),
        BlockPart(0, 1),
    )
}