package com.github.lunatrius.schematica.util;

import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.pattern.BlockStateReplacer;

import java.util.List;

public class SchematicReplaceAction {
    private final List<MBlockPos> updatedPositions;

    private final BlockStateReplacer.BlockStateInfo previous;

    public List<MBlockPos> getUpdatedPositions() {
        return this.updatedPositions;
    }

    public BlockStateReplacer.BlockStateInfo getPrevious() {
        return this.previous;
    }

    public SchematicReplaceAction(BlockStateReplacer.BlockStateInfo previous, List<MBlockPos> updated) {
        this.previous = previous;
        this.updatedPositions = updated;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematic\\util\SchematicReplaceAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */