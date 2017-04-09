/**
 * @author  administrator
 */
public class Comparision {
	
	/**
	 * @uml.property  name="containsInG1"
	 * @uml.associationEnd  qualifier="this:Difference java.util.Hashtable"
	 */
	private boolean containsInG1;
	/**
	 * @uml.property  name="containsInG2"
	 * @uml.associationEnd  qualifier="this:Difference java.util.Hashtable"
	 */
	private boolean containsInG2;

	public Comparision(final boolean containsInG1, final boolean containsInG2) {
		this.containsInG1 = containsInG1;
		this.containsInG2 = containsInG2;
	}
	
	public boolean isInG1() {
		return containsInG1;
	}

	public boolean isInG2() {
		return containsInG2;
	}
	
	public void setG1(final boolean containsInG1) {
		this.containsInG1 = containsInG1;
	}
	
	public void setG2(final boolean containsInG2) {
		this.containsInG2 = containsInG2;
	}
}