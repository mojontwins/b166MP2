package net.minecraft.client.player;

import net.minecraft.client.GameSettingsKeys;

public class MovementInputFromOptions extends MovementInput {

	public MovementInputFromOptions() {
	}

	public void readMovementInput() {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;
		if(GameSettingsKeys.keyBindForward.pressed) {
			++this.moveForward;
		}

		if(GameSettingsKeys.keyBindBack.pressed) {
			--this.moveForward;
		}

		if(GameSettingsKeys.keyBindLeft.pressed) {
			++this.moveStrafe;
		}

		if(GameSettingsKeys.keyBindRight.pressed) {
			--this.moveStrafe;
		}

		this.jump = GameSettingsKeys.keyBindJump.pressed;
		this.sneak = GameSettingsKeys.keyBindSneak.pressed;
		if(this.sneak) {
			this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
			this.moveForward = (float)((double)this.moveForward * 0.3D);
		}

	}
}
