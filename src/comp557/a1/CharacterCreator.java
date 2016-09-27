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
		
		// root
    	FreeJointDAGNode root = new FreeJointDAGNode("root");
    	SphericalDAGNode torso = new SphericalDAGNode("torso", 0d, 0d, 0d, 1.77d, 1.77d, 1.77d);
    	root.add(torso);
    	
    	// upper body
    	
    	//  abs and chest 
    	HingeJointDAGNode lowerabs = new HingeJointDAGNode("lowerabsjoint", 0d, 0.1d, 0.1d, "pitch");
    	root.add(lowerabs);
    	SphericalDAGNode stomach = new SphericalDAGNode("stomach", 0d, 0.75d, 0d, 1.6d, 1.6d, 1.35d);
    	lowerabs.add(stomach);
    	SphericalDAGNode sternum = new SphericalDAGNode("sternum", 0d, 1.6d, 0.2d, 1.5d, 0.5d, 0.5d);
    	stomach.add(sternum);
    	SphericalDAGNode leftchest = new SphericalDAGNode("leftchest", 0.5d, -0.5d, 0d, 1.25d, 1.4d, 1.05d);
    	SphericalDAGNode rightchest = new SphericalDAGNode("rightchest", -0.5d, -0.5d, 0d, 1.25d, 1.4d, 1.05d);
    	sternum.add(leftchest);
    	sternum.add(rightchest);
    	SphericalDAGNode back = new SphericalDAGNode("back", 0d, -0.6d, -0.55d, 2.2d, 2.1d, 1.55d);
    	sternum.add(back);
    	
    	// neck
    	CylindricalDAGNode neck = new CylindricalDAGNode("neck", 0d, 0.2d, -0.3d, 1d, 1d, 1d);
    	sternum.add(neck);
    	// head
    	BallJointDAGNode headjoint = new BallJointDAGNode("headjoint", 0d, 0d, 0d);
    	neck.add(headjoint);
    	SphericalDAGNode headjoint2 = new SphericalDAGNode("headjoint2", 0d, 0d, 0d, 1d, 1d, 1d);
    	headjoint.add(headjoint2);
    	SphericalDAGNode head = new SphericalDAGNode("head", 0d, 0.75d, 0.2d, 1.3d, 1.5d, 1.3d);
    	headjoint2.add(head);
    	
    	// left arm
    	BallJointDAGNode leftshoulder = new BallJointDAGNode("leftshoulderjoint", 0.9f, -0.25d, -0.6d);
    	leftshoulder.setDirection("right");
    	sternum.add(leftshoulder);
    	leftshoulder.add(new SphericalDAGNode("leftshoulder", 0d, 0d, 0d, 1.4d, 1.4d, 1.4d));
    	SphericalDAGNode leftbicep = new SphericalDAGNode("leftbicep", 0.1d, -1d, 0d, 1.1d, 1.5d, 1.1d);
    	leftshoulder.add(leftbicep);
    	HingeJointDAGNode leftelbow = new HingeJointDAGNode("leftelbowjoint", 0d, -1d, 0d, "pitch");
    	leftelbow.flipAxis();
    	leftbicep.add(leftelbow);    	
    	leftelbow.add(new SphericalDAGNode("leftelbow", 0d, 0d, 0d, 1d, 1d, 1d));
    	SphericalDAGNode leftforearm = new SphericalDAGNode("leftforearm", 0d, -1d, 0d, 1d, 2d, 1d);
    	leftelbow.add(leftforearm);
    	
    	// lower body
    	
    	// left leg
    	BallJointDAGNode lefthip = new BallJointDAGNode("lefthipjoint", 0.5d, -0.3d, 0.19d);
    	torso.add(lefthip);
    	SphericalDAGNode leftthigh = new SphericalDAGNode("leftthigh", 0d, -1d, 0d, 1.1d, 2d, 1.1d);
    	lefthip.add(leftthigh);
    	lefthip.add(new SphericalDAGNode("lefthip", 0d, 0d, 0d, 1.1d, 1.1d, 1.1d));
    	HingeJointDAGNode leftknee = new HingeJointDAGNode("leftkneejoint", 0d, -1d, 0d, "pitch");
    	leftthigh.add(leftknee);
    	leftknee.add(new SphericalDAGNode("leftknee", 0d, 0d, 0d, 0.9d, 0.9d, 0.9d));   
    	// knee down
    	SphericalDAGNode leftcalf = new SphericalDAGNode("leftcalf", 0d, -1d, 0d, 0.9d, 2d, 0.9d);
    	leftknee.add(leftcalf);
    	// ankle down
    	SphericalDAGNode leftankle = new SphericalDAGNode("leftankle", 0d, -1d, 0d, 0.6d, 0.6d, 0.6d);
    	leftcalf.add(leftankle);
    	HingeJointDAGNode leftheel = new HingeJointDAGNode("leftheeljoint", 0d, -0.5d, 0d, "pitch");
    	leftankle.add(leftheel);
    	leftheel.add(new SphericalDAGNode("leftheel", 0d, 0d, 0d, 0.6d, 0.6d, 0.6d));
    	// foot 
    	SphericalDAGNode leftfoot = new SphericalDAGNode("leftfoot", 0d, -0.07d, 0.4d, 0.6d, 0.45d, 1.3d);
    	leftheel.add(leftfoot);
    	
    	// right leg
    	BallJointDAGNode righthip = new BallJointDAGNode("righthipjoint", -0.5d, -0.3d, 0.19d);
    	torso.add(righthip);
    	SphericalDAGNode rightthigh = new SphericalDAGNode("rightthigh", 0d, -1d, 0d, 1.1d, 2d, 1.1d);
    	righthip.add(rightthigh);
    	righthip.add(new SphericalDAGNode("righthip", 0d, 0d, 0d, 1.1d, 1.1d, 1.1d));
    	HingeJointDAGNode rightknee = new HingeJointDAGNode("rightkneejoint", 0d, -1d, 0d, "pitch");
    	rightthigh.add(rightknee);
    	rightknee.add(new SphericalDAGNode("rightknee", 0d, 0d, 0d, 0.9d, 0.9d, 0.9d));   
    	// knee down
    	SphericalDAGNode rightcalf = new SphericalDAGNode("rightcalf", 0d, -1d, 0d, 0.9d, 2d, 0.9d);
    	rightknee.add(rightcalf);
    	// ankle down
    	SphericalDAGNode rightankle = new SphericalDAGNode("rightankle", 0d, -1d, 0d, 0.6d, 0.6d, 0.6d);
    	rightcalf.add(rightankle);
    	HingeJointDAGNode rightheel = new HingeJointDAGNode("rightheeljoint", 0d, -0.5d, 0d, "pitch");
    	rightankle.add(rightheel);
    	rightheel.add(new SphericalDAGNode("rightheel", 0d, 0d, 0d, 0.6d, 0.6d, 0.6d));
    	// foot 
    	SphericalDAGNode rightfoot = new SphericalDAGNode("rightfoot", 0d, -0.07d, 0.4d, 0.6d, 0.45d, 1.2d);
    	rightheel.add(rightfoot);
    	
    	
    	return root;
	}
}
