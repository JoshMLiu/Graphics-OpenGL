package comp557.a1;

public class CharacterCreator {

	static public String name = "LITTLE MAC - JOSH LIU 260612384";
	
	/** 
	 * Creates a character.
	 * @return root DAGNode
	 */
	static public DAGNode create() {
		// TODO: use for testing, and ultimately for creating a character
		// Here we just return an empty node  to allow the sample code to run.
		// You should REMOVE THE LINE BELOW and instead 
		// return the root of your character	
    	FreeJointDAGNode root = new FreeJointDAGNode("root");
    	HingeJointDAGNode left = new HingeJointDAGNode("left", -1d, 0d, 0d, 0d, "yaw");
    	root.add(left);
    	SphericalDAGNode ls = new SphericalDAGNode("ls", 0d, 0d, 0d, 1d, 1d, 1d);
    	ls.setColor(1f, 0f, 0f);
    	left.add(ls);
    	BallJointDAGNode right = new BallJointDAGNode("right", 1d, 0d, 0d, 0d, 0d, 0d);
    	root.add(right);
    	return root;
	}
}
