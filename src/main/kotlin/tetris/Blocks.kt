package tetris

import javafx.scene.paint.Color

abstract class Block(x: Int, y: Int) {
    var position = Vector(x, y)
        private set

    open val color: Color = Color.BLACK

    // The relative coordinates of the squares that make one block
    abstract val offsets: MutableList<Vector>

    // Moves the block in the given direction
    fun move(direction: Vector) {
        position += direction
    }

    // Rotates the block in the given direction
    open fun rotate(rotation: (Vector) -> Vector) {
        (0 until offsets.size).forEach { index -> offsets[index] = rotation(offsets[index]) }
    }

    // Updates the block offsets if a given line was destroyed
    fun updateOffsets(removedLineY: Int) {
        // Remove offsets that were in the line
        offsets.removeIf { o -> position.y + o.y == removedLineY }
        // Move offsets down above the line
        offsets.map { o -> if (position.y + o.y < removedLineY) o.y++ }
    }
}

class OBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.YELLOW

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, 0),
        Vector(0, 1),
        Vector(1, 0),
        Vector(1, 1),
    )

    override fun rotate(rotation: (Vector) -> Vector) {}
}

class IBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.BLUE

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, -2),
        Vector(0, -1),
        Vector(0, 0),
        Vector(0, 1),
    )
}

class SBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.GREEN

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, 0),
        Vector(1, 0),
        Vector(-1, 1),
        Vector(0, 1),
    )
}

class ZBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.RED

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, 0),
        Vector(-1, 0),
        Vector(1, 1),
        Vector(0, 1),
    )
}

class TBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.PURPLE

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(-1, 0),
        Vector(0, 0),
        Vector(1, 0),
        Vector(0, 1),
    )
}

class LBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.ORANGE

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, -1),
        Vector(0, 0),
        Vector(0, 1),
        Vector(1, 1),
    )
}

class FBlock(x: Int, y: Int) : Block(x, y) {
    override val color: Color = Color.DARKBLUE

    override val offsets: MutableList<Vector> = mutableListOf(
        Vector(0, -1),
        Vector(1, -1),
        Vector(0, 0),
        Vector(0, 1),
    )
}