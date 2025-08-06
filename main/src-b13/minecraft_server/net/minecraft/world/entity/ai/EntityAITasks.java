package net.minecraft.world.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityAITasks {
	private ArrayList<EntityAITaskEntry> tasksToDo = new ArrayList<EntityAITaskEntry>();
	private ArrayList<EntityAITaskEntry> executingTasks = new ArrayList<EntityAITaskEntry>();

	public void addTask(int i1, EntityAIBase entityAIBase2) {
		this.tasksToDo.add(new EntityAITaskEntry(this, i1, entityAIBase2));
	}

	public void onUpdateTasks() {
		ArrayList<EntityAITaskEntry> arrayList1 = new ArrayList<EntityAITaskEntry>();
		Iterator<EntityAITaskEntry> iterator2 = this.tasksToDo.iterator();

		while(true) {
			EntityAITaskEntry entityAITaskEntry3;
			while(true) {
				if(!iterator2.hasNext()) {
					boolean z5 = false;
					if(z5 && arrayList1.size() > 0) {
						System.out.println("Starting: ");
					}

					Iterator<EntityAITaskEntry> iterator6;
					EntityAITaskEntry entityAITaskEntry7;
					for(iterator6 = arrayList1.iterator(); iterator6.hasNext(); entityAITaskEntry7.action.startExecuting()) {
						entityAITaskEntry7 = (EntityAITaskEntry)iterator6.next();
						if(z5) {
							System.out.println(entityAITaskEntry7.action.toString() + ", ");
						}
					}

					if(z5 && this.executingTasks.size() > 0) {
						System.out.println("Running: ");
					}

					for(iterator6 = this.executingTasks.iterator(); iterator6.hasNext(); entityAITaskEntry7.action.updateTask()) {
						entityAITaskEntry7 = (EntityAITaskEntry)iterator6.next();
						if(z5) {
							System.out.println(entityAITaskEntry7.action.toString());
						}
					}

					return;
				}

				entityAITaskEntry3 = (EntityAITaskEntry)iterator2.next();
				boolean z4 = this.executingTasks.contains(entityAITaskEntry3);
				if(!z4) {
					break;
				}

				if(!this.func_46116_a(entityAITaskEntry3) || !entityAITaskEntry3.action.continueExecuting()) {
					entityAITaskEntry3.action.resetTask();
					this.executingTasks.remove(entityAITaskEntry3);
					break;
				}
			}

			if(this.func_46116_a(entityAITaskEntry3) && entityAITaskEntry3.action.shouldExecute()) {
				arrayList1.add(entityAITaskEntry3);
				this.executingTasks.add(entityAITaskEntry3);
			}
		}
	}

	private boolean func_46116_a(EntityAITaskEntry entityAITaskEntry1) {
		Iterator<EntityAITaskEntry> iterator2 = this.tasksToDo.iterator();

		while(iterator2.hasNext()) {
			EntityAITaskEntry entityAITaskEntry3 = (EntityAITaskEntry)iterator2.next();
			if(entityAITaskEntry3 != entityAITaskEntry1) {
				if(entityAITaskEntry1.priority >= entityAITaskEntry3.priority) {
					if(this.executingTasks.contains(entityAITaskEntry3) && !this.areTasksCompatible(entityAITaskEntry1, entityAITaskEntry3)) {
						return false;
					}
				} else if(this.executingTasks.contains(entityAITaskEntry3) && !entityAITaskEntry3.action.isContinuous()) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean areTasksCompatible(EntityAITaskEntry entityAITaskEntry1, EntityAITaskEntry entityAITaskEntry2) {
		return (entityAITaskEntry1.action.getMutexBits() & entityAITaskEntry2.action.getMutexBits()) == 0;
	}
}
