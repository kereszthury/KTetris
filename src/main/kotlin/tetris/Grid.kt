package tetris

class Grid(val width: Int, val height: Int) {
    private val occupiedCells = arrayOfNulls<Block?>(width * height)
    val droppedBlocks = mutableListOf<Block>()

    // Locks the cells in which the block is
    fun lockCells(block: Block) {
        for (part in block.offsets) {
            val vector = part + block.position
            if (vectorsInGrid(vector)) {
                occupiedCells[width * vector.y + vector.x] = block
            }
        }
    }

    // Unlocks the cells in which the block was
    fun unlockCells(block: Block) {
        for (index in occupiedCells.indices) {
            if (block == occupiedCells[index]) {
                occupiedCells[index] = null
            }
        }
    }

    // Returns the block that is in the given cell
    fun getCell(position: Vector): Block? {
        return if (!vectorsInGrid(position)) null
        else occupiedCells[width * position.y + position.x]
    }

    // Destroys full lines, returns with the amount destroyed
    fun destroyFullLines(): Int {
        var destroyedLines = 0
        var yCheck = height - 1
        while (yCheck >= 0) {
            // Check if there is an empty slot in the row
            var rowFull = true
            for (x in 0 until width) {
                if (getCell(Vector(x, yCheck)) == null) {
                    yCheck--
                    rowFull = false
                    break
                }
            }
            // There was no empty slot, so blocks need to be updated
            if (rowFull) {
                destroyedLines++
                droppedBlocks.forEach { b ->
                    unlockCells(b)
                    b.updateOffsets(yCheck)
                    lockCells(b)
                }
                droppedBlocks.removeIf { b -> b.offsets.size == 0 }
            }
        }
        return destroyedLines
    }

    // Returns true if a block can be translated to a given place in the grid
    fun canGoTo(block: Block?, translate: (Vector) -> (Vector)): Boolean {
        if (block == null) return false

        val vectorList = mutableListOf<Vector>()
        vectorList.addAll(block.offsets)
        val vectorArray = vectorList.map(translate).map { vector -> vector + block.position }.toTypedArray()

        return vectorsInPlayArea(*vectorArray) && areCellsFree(*vectorArray)
    }

    // Clears the grid
    fun clear() {
        for (block in droppedBlocks) {
            unlockCells(block)
        }
        droppedBlocks.clear()
    }

    // Returns true if all the given cells are free
    private fun areCellsFree(vararg cells: Vector): Boolean {
        for (cell in cells) {
            if (getCell(cell) != null) return false
        }
        return true
    }

    // Returns true if the given vectors are in the visible part of the grid
    private fun vectorsInGrid(vararg positions: Vector): Boolean {
        if (!vectorsInPlayArea()) return false
        for (position in positions) {
            if (position.y < 0) {
                return false
            }
        }
        return true
    }

    // Returns true if the given vectors are in the playable area (contained by the two sides and the bottom, the top is open)
    private fun vectorsInPlayArea(vararg positions: Vector): Boolean {
        for (position in positions) {
            if (position.x < 0 || position.x >= width || position.y >= height) {
                return false
            }
        }
        return true
    }
}