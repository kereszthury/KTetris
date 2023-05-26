package tetris

// Class representing size or a position on the grid
data class Vector(var x: Int, var y: Int) {
    companion object {
        val down = Vector(0, 1)

        val left = Vector(-1, 0)

        val right = Vector(1, 0)

        val rotateRight: (Vector) -> Vector = { vector -> Vector(vector.y, -vector.x) }

        val rotateLeft: (Vector) -> Vector = { vector -> Vector(-vector.y, vector.x) }
    }

    operator fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }
}