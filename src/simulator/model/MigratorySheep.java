package simulator.model;

import simulator.misc.Vector2D;

public class MigratorySheep extends Sheep{

	public MigratorySheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
		super(mateStrategy, dangerStrategy, pos);
	}
	
	public MigratorySheep(MigratorySheep p1,Animal p2) {
		
	}

}
 