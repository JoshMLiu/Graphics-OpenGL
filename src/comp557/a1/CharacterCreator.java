package comp557.a1;

// Josh Liu ID:260612384

public class CharacterCreator {

	static public String name = "ROCKY BALBOA - JOSH LIU 260612384";
	
	/** 
	 * Creates a character.
	 * @return root DAGNode
	 */
	static public DAGNode create() {
		
		// Rocky - human with body, legs, arms and head, and clothes. 
		
		// root
    	FreeJointDAGNode root = new FreeJointDAGNode("root");
    	SphericalDAGNode torso = new SphericalDAGNode("torso", 0d, 0d, -0.2d, 1.85d, 1.6d, 1.3d);
    	root.add(torso);
    	 	
    	// belt
    	CylindricalDAGNode belt = new CylindricalDAGNode("belt", 0d, 0.75d, 0d, 1.7d, 0.5d, 1.5d);
    	belt.setColor(100, 20, 20);
    	torso.add(belt);
    	SphericalDAGNode medal = new SphericalDAGNode("medal", 0d, -0.25d, 0.75d, 1d, 1d, 0.1d);
    	medal.setColor(230, 190, 138);
    	belt.add(medal);
    	
    	// shorts
    	SphericalDAGNode shortbottom = new SphericalDAGNode("shortbottom", 0d, -0.05d, 0d, 1.9d, 1.65d, 1.35d);
    	shortbottom.setColor(255, 255, 255);
    	torso.add(shortbottom);
    	
    	// upper body ->
    	
    	//  abs and chest 
    	BallJointDAGNode lowerabs = new BallJointDAGNode("lowerabsjoint", 0d, 0.1d, 0.1d);
    	lowerabs.setDirection("up", true);
    	root.add(lowerabs);
    	SphericalDAGNode stomach = new SphericalDAGNode("stomach", 0d, 0.75d, -0.2d, 1.6d, 1.6d, 1.25d);
    	lowerabs.add(stomach);
    	SphericalDAGNode sternum = new SphericalDAGNode("sternum", 0d, 1.6d, 0.2d, 1.9d, 0.4d, 0.4d);
    	stomach.add(sternum);
    	SphericalDAGNode leftchest = new SphericalDAGNode("leftchest", 0.47d, -0.5d, 0d, 1.3d, 1.3d, 0.9d);
    	SphericalDAGNode rightchest = new SphericalDAGNode("rightchest", -0.47d, -0.5d, 0d, 1.3d, 1.3d, 0.9d);
    	sternum.add(leftchest);
    	sternum.add(rightchest);
    	SphericalDAGNode back = new SphericalDAGNode("back", 0d, -0.6d, -0.45d, 2.2d, 2.1d, 1.55d);
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
    	SphericalDAGNode leftchin = new SphericalDAGNode("leftchin", 0.12d, -0.5d, 0.35d, 0.4d, 0.4d, 0.4d);
    	SphericalDAGNode rightchin = new SphericalDAGNode("rightchin", -0.12d, -0.5d, 0.35d, 0.4d, 0.4d, 0.4d);
    	head.add(leftchin);
    	head.add(rightchin);
    	SphericalDAGNode nose = new SphericalDAGNode("nose", 0d, -0.02d, 0.5d, 0.3d, 0.4d, 0.5d);
    	SphericalDAGNode mouth = new SphericalDAGNode("mouth", 0d, -0.28d, 0d, 0.5d, 0.2d, 0.3d);
    	mouth.setColor(220, 130, 130);
    	nose.add(mouth);
    	SphericalDAGNode lefteye = new SphericalDAGNode("lefteye", 0.23d, 0.14d, 0d, 0.25d, 0.1d, 0.25d);
    	SphericalDAGNode righteye = new SphericalDAGNode("righteye", -0.23d, 0.14d, 0d, 0.25d, 0.1d, 0.25d);
    	lefteye.setColor(0, 0, 0);
    	righteye.setColor(0, 0, 0);
    	nose.add(lefteye);
    	nose.add(righteye);
    	head.add(nose);
    	SphericalDAGNode leftear = new SphericalDAGNode("leftear", 0.65d, 0.1d, 0d, 0.15d, 0.4d, 0.3d);
    	SphericalDAGNode rightear = new SphericalDAGNode("rightear", -0.65d, 0.1d, 0d, 0.15d, 0.4d, 0.3d);
    	head.add(leftear);
    	head.add(rightear);
    	// hair
    	SphericalDAGNode hair = new SphericalDAGNode("hair", 0d, 0.05d, -0.05d, 1.3d, 1.5d, 1.3d);
    	hair.setColor(100, 20, 20);
    	head.add(hair);
    	// hat
    	BallJointDAGNode hathinge = new BallJointDAGNode("hathinge", 0d, 0d,  0d);
    	hathinge.setDirection("up", true);
    	head.add(hathinge);
    	CylindricalDAGNode hat = new CylindricalDAGNode("hat", 0d, 0.9d, 0d, 1.2d, 0.5d, 1.4d);
    	hat.setColor(120, 100, 100);
    	CylindricalDAGNode brim = new CylindricalDAGNode("brim", 0d, -0.5d, 0d, 1.5d, 0.05d, 2.05d);
    	CylindricalDAGNode band = new CylindricalDAGNode("band", 0d, 0.2d, 0d, 1.21d, 0.2d,  1.41d);
    	band.setColor(255, 255, 255);
    	brim.setColor(120, 100, 100);
    	brim.add(band);
    	hat.add(brim);
    	hathinge.add(hat);
    	   	
    	// left arm
    	BallJointDAGNode leftshoulder = new BallJointDAGNode("leftshoulderjoint", 0.9f, -0.25d, -0.6d);
    	leftshoulder.setDirection("right", true);
    	sternum.add(leftshoulder);
    	leftshoulder.add(new SphericalDAGNode("leftshoulder", 0d, 0d, 0d, 1.5d, 1.5d, 1.5d));
    	SphericalDAGNode leftbicep = new SphericalDAGNode("leftbicep", 0.8d, 0d, 0d, 1.4d, 1.1d, 1.1d);
    	leftshoulder.add(leftbicep);
    	HingeJointDAGNode leftelbow = new HingeJointDAGNode("leftelbowjoint", 0.7d, 0d, 0d, "yaw");
    	leftbicep.add(leftelbow); 
    	leftelbow.flipAxis();
    	leftelbow.add(new SphericalDAGNode("leftelbow", 0d, 0d, 0d, 0.7d, 0.7d, 0.7d));
    	SphericalDAGNode leftforearm = new SphericalDAGNode("leftforearm", 0.7d, 0d, 0d, 1.4d, 0.8d, 0.8d);
    	leftelbow.add(leftforearm);
    	// wrist and hand
    	BallJointDAGNode leftwrist = new BallJointDAGNode("leftwristjoint", 0.7d, 0d, 0d);
    	leftwrist.setDirection("right", true);
    	SphericalDAGNode leftwrist2 = new SphericalDAGNode("leftwrist", 0d, 0d, 0d, 0.5d, 0.5d, 0.5d);
    	leftwrist2.setColor(255, 0, 0);
    	leftwrist.add(leftwrist2);
    	leftforearm.add(leftwrist);
    	SphericalDAGNode lefthand = new SphericalDAGNode("lefthand", 0.4d, 0d, 0d, 1.2d, 0.6d, 0.45d);
    	SphericalDAGNode leftthumb = new SphericalDAGNode("leftthumb", -0.2d, -0.2d, -0.1d, 0.3d, 0.6d, 0.3d);
    	lefthand.add(leftthumb);
    	leftwrist.add(lefthand);
    	
    	// left glove 
    	SphericalDAGNode leftglove = new SphericalDAGNode("leftglove", 0.2d, 0d, 0d, 1.2d, 1.15d, 1.1d);
    	leftglove.setColor(255, 0, 0);
    	lefthand.add(leftglove);
    	SphericalDAGNode leftglovethumb = new SphericalDAGNode("leftglovethumb", -0.3d, -0.3d, -0.1d, 0.55d, 0.5d, 0.55d);
    	leftglovethumb.setColor(255, 0, 0);
    	leftglove.add(leftglovethumb);
    	
    	// right arm
    	BallJointDAGNode rightshoulder = new BallJointDAGNode("rightshoulderjoint", -0.9f, -0.25d, -0.6d);
    	rightshoulder.setDirection("left", true);
    	sternum.add(rightshoulder);
    	rightshoulder.add(new SphericalDAGNode("rightshoulder", 0d, 0d, 0d, 1.5d, 1.5d, 1.5d));
    	SphericalDAGNode rightbicep = new SphericalDAGNode("rightbicep", -0.8d, 0d, 0d, 1.4d, 1.1d, 1.1d);
    	rightshoulder.add(rightbicep);
    	HingeJointDAGNode rightelbow = new HingeJointDAGNode("rightelbowjoint", -0.7d, 0d, 0d, "yaw");
    	rightbicep.add(rightelbow); 
    	rightelbow.add(new SphericalDAGNode("rightelbow", 0d, 0d, 0d, 0.7d, 0.7d, 0.7d));
    	SphericalDAGNode rightforearm = new SphericalDAGNode("rightforearm", -0.7d, 0d, 0d, 1.4d, 0.8d, 0.8d);
    	rightelbow.add(rightforearm);
    	// wrist and hand
    	BallJointDAGNode rightwrist = new BallJointDAGNode("rightwristjoint", -0.7d, 0d, 0d);
    	rightwrist.setDirection("left", true);
    	SphericalDAGNode rightwrist2 = new SphericalDAGNode("rightwrist", 0d, 0d, 0d, 0.5d, 0.5d, 0.5d);
    	rightwrist2.setColor(255, 0, 0);
    	rightwrist.add(rightwrist2);
    	rightforearm.add(rightwrist);
    	SphericalDAGNode righthand = new SphericalDAGNode("righthand", -0.4d, 0d, 0d, 1.2d, 0.6d, 0.45d);
    	SphericalDAGNode rightthumb = new SphericalDAGNode("rightthumb", 0.2d, 0.2d, 0.1d, 0.3d, 0.6d, 0.3d);
    	righthand.add(rightthumb);
    	rightwrist.add(righthand);
    	
    	// right glove 
    	SphericalDAGNode rightglove = new SphericalDAGNode("rightglove", -0.2d, 0d, 0d, 1.2d, 1.15d, 1.1d);
    	rightglove.setColor(255, 0, 0);
    	righthand.add(rightglove);
    	SphericalDAGNode rightglovethumb = new SphericalDAGNode("rightglovethumb", 0.3d, 0.3d, 0.1d, 0.55d, 0.5d, 0.55d);
    	rightglovethumb.setColor(255, 0, 0);
    	rightglove.add(rightglovethumb);
    	
    	// lower body ->
    	
    	// left leg
    	BallJointDAGNode lefthip = new BallJointDAGNode("lefthipjoint", 0.49d, -0.3d, 0.1d);
    	torso.add(lefthip);
    	SphericalDAGNode leftthigh = new SphericalDAGNode("leftthigh", 0d, -1d, 0d, 0.9d, 2d, 0.9d);
    	lefthip.add(leftthigh);
    	SphericalDAGNode lefthip2 = new SphericalDAGNode("lefthip", 0d, 0d, 0d, 1d, 1d, 1d);
    	lefthip2.setColor(255, 255, 255);
    	lefthip.add(lefthip2);
    	HingeJointDAGNode leftknee = new HingeJointDAGNode("leftkneejoint", 0d, -1d, 0d, "pitch");
    	leftthigh.add(leftknee);
    	leftknee.add(new SphericalDAGNode("leftknee", 0d, 0d, 0d, 0.8d, 0.8d, 0.8d));   
    	// knee down
    	SphericalDAGNode leftcalf = new SphericalDAGNode("leftcalf", 0d, -1d, 0d, 0.7d, 2d, 0.7d);
    	leftcalf.setColor(255, 255, 255);;
    	leftknee.add(leftcalf);
    	// ankle down
    	SphericalDAGNode leftankle = new SphericalDAGNode("leftankle", 0d, -1d, 0d, 0.4d, 0.6d, 0.4d);
    	leftankle.setColor(0, 0, 255);
    	leftcalf.add(leftankle);
    	HingeJointDAGNode leftheel = new HingeJointDAGNode("leftheeljoint", 0d, -0.5d, 0d, "pitch");
    	SphericalDAGNode leftheel2 = new SphericalDAGNode("leftheel", 0d, 0d, 0d, 0.6d, 0.6d, 0.6d);
    	leftheel2.setColor(255, 255, 255);
    	leftankle.add(leftheel);
    	leftheel.add(leftheel2);
    	// foot 
    	SphericalDAGNode leftfoot = new SphericalDAGNode("leftfoot", 0d, -0.07d, 0.4d, 0.6d, 0.45d, 1.3d);
    	leftfoot.setColor(255, 255, 255);
    	leftheel.add(leftfoot);
    	
    	// shorts trunk
    	CylindricalDAGNode lefttrunk = new CylindricalDAGNode("lefttrunk", 0d, 1d, 0d, 1d, 1.3d, 1d);
    	lefttrunk.setColor(255, 255, 255);
    	leftthigh.add(lefttrunk);
    	CylindricalDAGNode leftband = new CylindricalDAGNode("leftband", 0d, -1d, 0d, 1.01d, 0.3d, 1.01d);
    	leftband.setColor(0, 0, 255);
    	lefttrunk.add(leftband);
    	
    	// right leg
    	BallJointDAGNode righthip = new BallJointDAGNode("righthipjoint", -0.49d, -0.3d, 0.1d);
    	torso.add(righthip);
    	SphericalDAGNode rightthigh = new SphericalDAGNode("rightthigh", 0d, -1d, 0d, 0.9d, 2d, 0.9d);
    	righthip.add(rightthigh);
    	SphericalDAGNode righthip2 = new SphericalDAGNode("righthip", 0d, 0d, 0d, 1d, 1d, 1d);
    	righthip2.setColor(255, 255, 255);
    	righthip.add(righthip2);
    	HingeJointDAGNode rightknee = new HingeJointDAGNode("rightkneejoint", 0d, -1d, 0d, "pitch");
    	rightthigh.add(rightknee);
    	rightknee.add(new SphericalDAGNode("rightknee", 0d, 0d, 0d, 0.8d, 0.8d, 0.8d));   
    	// knee down
    	SphericalDAGNode rightcalf = new SphericalDAGNode("rightcalf", 0d, -1d, 0d, 0.7d, 2d, 0.7d);
    	rightcalf.setColor(255, 255, 255);;
    	rightknee.add(rightcalf);
    	// ankle down
    	SphericalDAGNode rightankle = new SphericalDAGNode("rightankle", 0d, -1d, 0d, 0.4d, 0.6d, 0.4d);
    	rightankle.setColor(0, 0, 255);
    	rightcalf.add(rightankle);
    	HingeJointDAGNode rightheel = new HingeJointDAGNode("rightheeljoint", 0d, -0.5d, 0d, "pitch");
    	rightankle.add(rightheel);
    	SphericalDAGNode rightheel2 = new SphericalDAGNode("rightheel", 0d, 0d, 0d, 0.6d, 0.6d, 0.6d);
    	rightheel2.setColor(255, 255, 255);
    	rightheel.add(rightheel2);
    	// foot 
    	SphericalDAGNode rightfoot = new SphericalDAGNode("rightfoot", 0d, -0.07d, 0.4d, 0.6d, 0.45d, 1.2d);
    	rightfoot.setColor(255, 255, 255);
    	rightheel.add(rightfoot);
    	
    	// shorts trunk
    	CylindricalDAGNode righttrunk = new CylindricalDAGNode("righttrunk", 0d, 1d, 0d, 1d, 1.3d, 1d);
    	righttrunk.setColor(255, 255, 255);
    	rightthigh.add(righttrunk);
    	CylindricalDAGNode rightband = new CylindricalDAGNode("rightband", 0d, -1d, 0d, 1.01d, 0.3d, 1.01d);
    	rightband.setColor(0, 0, 255);
    	righttrunk.add(rightband);
    	
    	return root;
	}
}
