package comp557.projDemo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Tuple4d;

import mintools.parameters.DoubleParameter;
import mintools.parameters.Parameter;
import mintools.parameters.ParameterListener;
import mintools.swing.VerticalFlowPanel;

public class Vec4Parameter extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2738367051431263896L;

	float x,y,z,w;
	
	DoubleParameter xp = new DoubleParameter( "x", 0, -10, 10 );
	DoubleParameter yp = new DoubleParameter( "y", 0, -10, 10 );
	DoubleParameter zp = new DoubleParameter( "z", 0, -10, 10 );
	DoubleParameter wp = new DoubleParameter( "w", 0, -10, 10 );
	
	public Vec4Parameter( String name, double xx, double yy, double zz, double ww ) {
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
		vfp.add( wp.getSliderControls(false) );
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
		wp.addParameterListener( new ParameterListener<Double>() {
			@Override
			public void parameterChanged(Parameter<Double> parameter) {
				w = wp.getFloatValue();			
			}
		});
		xp.setValue(xx);		
		yp.setValue(yy);		
		zp.setValue(zz);
		zp.setValue(ww);

	}
	
	public void set( Tuple4d p ) {
		xp.setValue( p.x );
		yp.setValue( p.y );
		zp.setValue( p.z );
		wp.setValue( p.w );
	}
	
	public void get( Tuple4d p ) {
		p.x = xp.getValue();
		p.y = yp.getValue();
		p.z = zp.getValue();
		p.w = wp.getValue();
	}
}
