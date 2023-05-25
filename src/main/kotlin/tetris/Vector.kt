package tetris

data class Vector(var x: Int, var y: Int) {
    companion object {
        fun down(): Vector {
            return Vector(0, 1)
        }

        fun left(): Vector {
            return Vector(-1, 0)
        }

        fun right(): Vector {
            return Vector(1, 0)
        }

        fun rotateRight(vector: Vector): Vector {
            return Vector(vector.y, -vector.x)
        }

        fun rotateLeft(vector: Vector): Vector {
            return Vector(-vector.y, vector.x)
        }
    }

    operator fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }
}