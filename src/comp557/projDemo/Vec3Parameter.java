package comp557.projDemo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Tuple3d;

import mintools.parameters.DoubleParameter;
import mintools.parameters.Parameter;
import mintools.parameters.ParameterListener;
import mintools.swing.VerticalFlowPanel;

public class Vec3Parameter extends JPanel {

	private static final long serialVersionUID = -7176729017675559468L;

	float x,y,z;
	
	DoubleParameter xp = new DoubleParameter( "x", 0, -10, 10 );
	DoubleParameter yp = new DoubleParameter( "y", 0, -10, 10 );
	DoubleParameter zp = new DoubleParameter( "z", 0, -10, 10 );
	
	public Vec3Parameter( String name, double xx, double yy, double zz ) {
		setLayout( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.weightx = 1; 
        gbc.weighty = 0;
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.setBorder( new TitledBorder(name) );
		vfp.add( xp.getSliderControls(false) );
		vfp.add( yp.getSliderControls(false) );
		vfp.add( zp.getSliderControls(false) );
		this.add( vfp.getPanel(), gbc );
		gbc.gridy++;
        gbc.weighty = 1;
        add( new JPanel(), gbc );
        gbc.weighty = 0;		
        xp.addParameterListener( new ParameterListener<Double>() {
			@Override
			public void parameterChanged(Parameter<Double> parameter) {
				x = xp.getFloatValue();			
			}
		});
		yp.addParameterListener( new ParameterListener<Double>() {
			@Override
			public void parameterChanged(Parameter<Double> parameter) {
				y = yp.getFloatValue();			
			}
		});
		zp.addParameterListener( new ParameterListener<Double>() {
			@Override
			public void parameterChanged(Parameter<Double> parameter) {
				z = zp.getFloatValue();			
			}
		});
		xp.setValue(xx);		
		yp.setValue(yy);		
		zp.setValue(zz);
	}
	
	public void set( Tuple3d p ) {
		xp.setValue( p.x );
		yp.setValue( p.y );
		zp.setValue( p.z );
	}
	
	public void get( Tuple3d p ) {
		p.x = xp.getValue();
		p.y = yp.getValue();
		p.z = zp.getValue();
	}
}
