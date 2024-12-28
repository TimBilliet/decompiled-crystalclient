package com.github.lunatrius.schematica.reference;

public final class Names {
  public static final class Command {
    public static final class Save {
      public static final class Message {
        public static final String SAVE_STARTED = "schematica.command.save.started";
        
        public static final String SAVE_SUCCESSFUL = "schematica.command.save.saveSucceeded";
        
        public static final String SAVE_FAILED = "schematica.command.save.saveFailed";
      }
    }
    
    public static final class Replace {
      public static final String NAME = "schematicaReplace";
      
      public static final class Message {
        public static final String USAGE = "schematica.command.replace.usage";
        
        public static final String NO_SCHEMATIC = "schematica.command.replace.noSchematic";
        
        public static final String SUCCESS = "schematica.command.replace.success";
      }
    }
    
    public static final class Undo {
      public static final String NAME = "schematicaUndo";
      
      public static final class Message {
        public static final String USAGE = "/schematicaUndo";
        
        public static final String NO_SCHEMATIC = "schematica.command.replace.noSchematic";
        
        public static final String SUCCESS = "schematica.command.replace.success";
      }
    }
  }
  
  public static final class Messages {
    public static final String TOGGLE_PRINTER = "schematica.message.togglePrinter";
    
    public static final String TOGGLE_BLOCK_BREAK = "schematica.message.toggleBlockBreak";
    
    public static final String INVALID_BLOCK = "schematica.message.invalidBlock";
    
    public static final String INVALID_PROPERTY = "schematica.message.invalidProperty";
    
    public static final String INVALID_PROPERTY_FOR_BLOCK = "schematica.message.invalidPropertyForBlock";
  }
  
  public static final class Gui {
    public static final String X = "schematica.gui.x";
    
    public static final String Y = "schematica.gui.y";
    
    public static final String Z = "schematica.gui.z";
    
    public static final String ON = "schematica.gui.on";
    
    public static final String OFF = "schematica.gui.off";
    
    public static final String DONE = "schematica.gui.done";
    
    public static final class Load {
      public static final String TITLE = "schematica.gui.title";
      
      public static final String FOLDER_INFO = "schematica.gui.folderInfo";
      
      public static final String OPEN_FOLDER = "schematica.gui.openFolder";
      
      public static final String NO_SCHEMATIC = "schematica.gui.noschematic";
    }
    
    public static final class Save {
      public static final String POINT_RED = "schematica.gui.point.red";
      
      public static final String POINT_BLUE = "schematica.gui.point.blue";
      
      public static final String SAVE = "schematica.gui.save";
      
      public static final String SAVE_SELECTION = "schematica.gui.saveselection";
    }
    
    public static final class Control {
      public static final String MOVE_SCHEMATIC = "schematica.gui.moveschematic";
      
      public static final String MATERIALS = "schematica.gui.materials";
      
      public static final String PRINTER = "schematica.gui.printer";
      
      public static final String OPERATIONS = "schematica.gui.operations";
      
      public static final String UNLOAD = "schematica.gui.unload";
      
      public static final String MODE_ALL = "schematica.gui.all";
      
      public static final String MODE_LAYERS = "schematica.gui.layers";
      
      public static final String HIDE = "schematica.gui.hide";
      
      public static final String SHOW = "schematica.gui.show";
      
      public static final String MOVE_HERE = "schematica.gui.movehere";
      
      public static final String FLIP = "schematica.gui.flip";
      
      public static final String ROTATE = "schematica.gui.rotate";
      
      public static final String TRANSFORM_PREFIX = "schematica.gui.";
      
      public static final String MATERIAL_NAME = "schematica.gui.materialname";
      
      public static final String MATERIAL_AMOUNT = "schematica.gui.materialamount";
      
      public static final String SORT_PREFIX = "schematica.gui.material";
      
      public static final String DUMP = "schematica.gui.materialdump";
    }
  }
  
  public static final class Keys {
    public static final String CATEGORY = "schematica.key.category";
    
    public static final String LOAD = "schematica.key.load";
    
    public static final String SAVE = "schematica.key.save";
    
    public static final String CONTROL = "schematica.key.control";
    
    public static final String LAYER_INC = "schematica.key.layerInc";
    
    public static final String LAYER_DEC = "schematica.key.layerDec";
    
    public static final String LAYER_TOGGLE = "schematica.key.layerToggle";
    
    public static final String RENDER_TOGGLE = "schematica.key.renderToggle";
    
    public static final String PRINTER_TOGGLE = "schematica.key.printerToggle";
    
    public static final String BLOCK_DESTROY_TOGGLE = "schematica.key.blockDestroyToggle";
    
    public static final String MOVE_HERE = "schematica.key.moveHere";
  }
  
  public static final class NBT {
    public static final String ROOT = "Schematic";
    
    public static final String MATERIALS = "Materials";
    
    public static final String FORMAT_ALPHA = "Alpha";
    
    public static final String ICON = "Icon";
    
    public static final String BLOCKS = "Blocks";
    
    public static final String DATA = "Data";
    
    public static final String ADD_BLOCKS = "AddBlocks";
    
    public static final String ADD_BLOCKS_SCHEMATICA = "Add";
    
    public static final String WIDTH = "Width";
    
    public static final String LENGTH = "Length";
    
    public static final String HEIGHT = "Height";
    
    public static final String MAPPING_SCHEMATICA = "SchematicaMapping";
    
    public static final String TILE_ENTITIES = "TileEntities";
    
    public static final String ENTITIES = "Entities";
    
    public static final String EXTENDED_METADATA = "ExtendedMetadata";
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\reference\Names.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */