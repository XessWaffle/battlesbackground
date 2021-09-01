package util;

import core.Targetable;

public class Order {
	public static final int HOLD_POSITION = 2;
	public static final int MOVE_TO = 1;
	public static final int DO_NOTHING = 0;
	public static final int ATTACK = -1;
	public static final int DEFEND = -2;
	
	private int order = DO_NOTHING;
	private Targetable target;
	
	private boolean complete;
	
	public Order(int order, Targetable target) {
		this.setOrder(order);
		this.setTarget(target);
		this.setComplete(false);
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the target
	 */
	public Targetable getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Targetable target) {
		this.target = target;
	}

	/**
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * @param complete the complete to set
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public String toString() {
		String ret = "";
		
		if(order == ATTACK) {
			ret += "ATTACK at ";
		}
		
		else if(order == DEFEND) {
			ret += "DEFEND at ";
		}
		
		else if(order == MOVE_TO) {
			ret += "MOVE at ";
		}
		
		else if(order == HOLD_POSITION) {
			ret += "HOLD at ";
		} 
		
		else {
			return "DO NOTHING";
		}
		
		if(target != null)
			ret += target.getLoc().toString();
		
		return ret;
		
	}
	
}
