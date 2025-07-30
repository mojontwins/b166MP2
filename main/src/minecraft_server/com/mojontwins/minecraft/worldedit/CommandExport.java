package com.mojontwins.minecraft.worldedit;

import java.util.StringTokenizer;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandExport extends CommandWorldEdit {

	@Override
	public String getString() {
		return "export";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(idx == 2 && "list".equals(tokens [1])) {
			this.theCommandSender.printMessage(theWorld, ExporterBase.getList());
		} else if(idx == 3 && "help".equals(tokens [1])) {
			ExporterBase exporter = ExporterBase.getByName(tokens [2]);
			if(exporter != null) {
				this.theCommandSender.printMessage(theWorld, "[" + exporter.getName() + "] ");
				StringTokenizer tokenizer = new StringTokenizer(exporter.getHelp(), "\n");
				while(tokenizer.hasMoreTokens()) {
					this.theCommandSender.printMessage(theWorld, tokenizer.nextToken());
				}
			} else {
				this.theCommandSender.printMessage(theWorld, "Unknown exporter \"" + tokens[2] + "\"");
			}
		} else if(idx >= 3) {
			if(this.checkCorners(theWorld)) {
				this.theCommandSender.printMessage(theWorld, "Attempting to export...");
				
				String exporterName = tokens [1];
				String fileName = tokens [2];
				String arg = idx > 3 ? tokens [3] : "";
				
				ExporterBase exporter = ExporterBase.getByName(exporterName);
				if (exporter != null) {
					if(WorldEdit.export(theWorld, 0, true, thePlayer, fileName, exporter, arg)) {
						this.theCommandSender.printMessage(theWorld, WorldEdit.clipboardSize() + " block exported to " + fileName + " using exporter " + exporterName);
						return WorldEdit.clipboardSize();
					} else {
						this.theCommandSender.printMessage(theWorld, "Error exporting D: Check console for more info...");
					}
				} else {
					this.theCommandSender.printMessage(theWorld, "Unknown exporter \"" + exporterName + "\"");
				}
				
			} 
		}
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Exports current area using a exporter\n/export <exporter> <filename> [<arg>]\n/export list\n/export help <exporter>";
	}

}
