/******************************************************************
 *  Compilation:  javac InterferensiQuantum.java
 *  Execution:    java InterferensiQuantum
 *  Dependencies: Complex.java & Open Source Physics Library (http://www.opensourcephysics.org)
 *
 *  Original Written by I Wayan Sudiarta
 *	Edited by Rian Affandi
 *  22 Januari 2017 
 *
 ******************************************************************/
import java.awt.event.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
import org.opensourcephysics.frames.*;

public class InterferensiQuantum extends AbstractSimulation implements InteractiveMouseHandler {
	Scalar2DFrame frame = new Scalar2DFrame("x", "y", "sc");
	int i, j, j2, nx;
	long n;
	double x, y, s, dx, dt, t, dt2dx2, dp, dp2, p0, p02;
    double [][] psiR; // psi(x)
    double [][] psiI ; // psitemp(x)
    double [][] psiI2 ; // psitemp(x) 	
    double [][] show; // psi(x)
    double [][] v ; // potensial v(x)	

	Complex ta, tb, tc, ctemp;

	public void initialize() {
		n=0;
		dx = 0.10;
		dp = 1.0; // ca	
		p0 = 4.0; // cb	
		p02 = p0*p0;
		dp2 = dp*dp;
		dt = 0.2*dx*dx; // untuk stabil dt < dx^2
		dt2dx2 = dt/(2.0*dx*dx);
		
		nx = control.getInt("nx");  // "size" was missing in first printing of CSM book.
		
		psiR = new double [nx+1][nx+1]; // psi(x)
		psiI = new double [nx+1][nx+1]; // psitemp(x)
		psiI2 = new double [nx+1][nx+1]; // psitemp(x) 		
		show = new double[nx+1][nx+1];
		v = new double [nx+1][nx+1]; // potensial v(x)	
		
		for(i=0; i<=400;i++){
			for(j=0; j<=400;j++){
				x = (i-200)*dx;
				y = (j-200)*dx;
				v[i][j] = 0.0;//0.5*(x*x+y*y);
			}
		}
	
		for(i=0;i<400;i++){
			for(j=0;j<=400;j++){
				t = 0.0;
          
				x = (i-100)*dx;
				y = (j-200)*dx;
				ta = new Complex(-dp2*(x*x+y*y)/2.0, -p02*t/2.0 + p0*(x));
				tb = new Complex(1.0, dp2*t);
				ta = ta.divide(tb);
				ta = ta.exp();

				tc = new Complex(dp,0.0);
				tc = tc.divide(tb);
				tc = tc.sqrt();
				tc = tc.multiply(ta);  		  
          
				psiR[i][j]= tc.real();

				t = dt/2.0;
		  
				ta = new Complex(-dp2*(x*x+y*y)/2.0, -p02*t/2.0 + p0*(x));
				tb = new Complex(1.0, dp2*t);
				ta = ta.divide(tb);
				ta = ta.exp();
				tc = new Complex(dp,0.0);
				tc = tc.divide(tb);
				tc = tc.sqrt();
				tc = tc.multiply(ta);  		  

				psiI[i][j]= tc.imaginary();
			}
        }
		
		frame.setPaletteType(ColorMapper.DUALSHADE);
		initArrays();
		frame.setVisible(true);
		frame.showDataTable(true); // show the data table
	
	}

	public void initArrays() {
		frame.setAll(show);
	}
    
	public InterferensiQuantum() {
		frame.setInteractiveMouseHandler(this);
	}

  public void doStep() {
    	for(int k = 0; k<30;k++){	
			n++;		   
			// Hitung Psi Real
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					psiR[i][j] = psiR[i][j] - dt2dx2*(psiI[i+1][j] - 2.0*psiI[i][j] + psiI[i-1][j])- dt2dx2*(psiI[i][j+1]+ psiI[i][j-1]- 2.0*psiI[i][j])*1.0;
					psiR[i][j] = psiR[i][j] + dt*v[i][j]*psiI[i][j];
				}
			}
			
			for(i=0; i<=nx; i++){
				for(j=0;j<=nx;j++){ 
					psiI2[i][j] = psiI[i][j];
				}
			}	
			
			// Hitung Psi Imajiner
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					psiI[i][j] = psiI[i][j] + dt2dx2*(psiR[i+1][j] - 2.0*psiR[i][j] + psiR[i-1][j]) + dt2dx2*(psiR[i][j+1]+ psiR[i][j-1]-2.0*psiR[i][j])*1.0;
					psiI[i][j] = psiI[i][j] - dt*v[i][j]*psiR[i][j];
				}
			} 
		  
			// kondisi batas
			psiI[0][0] = 0;
			psiI[nx][nx] = 0;
			psiR[0][0] = 0;
			psiR[nx][nx] = 0;
			
			
			for(j=0;j<=400;j++){
				if(j<=180||j>=220){
				psiI[200][j] = 0;
				psiR[200][j] = 0;}
				if(j>=190&&j<=210){
				psiI[200][j] = 0;
				psiR[200][j] = 0;	
				}
			}
			
			
			for(i=0; i<=nx; i++){
				for(j=0; j<=nx; j++){
					show[i][j] = (psiI[i][j] + psiI2[i][j])*(psiI[i][j] + psiI2[i][j])*0.25+psiR[i][j]*psiR[i][j];
				}	
			}
		}
		frame.setAll(show);
	}

	public void reset() {
		control.setValue("nx", 400);
		initialize();
	}

	public void handleMouseAction(InteractivePanel panel, MouseEvent evt) {
		switch(panel.getMouseAction()) {
		case InteractivePanel.MOUSE_DRAGGED :
		case InteractivePanel.MOUSE_PRESSED :
		double x = panel.getMouseX(); // mouse x in world units
		double y = panel.getMouseY();
		int i = frame.xToIndex(x);    // closest array index
		int j = frame.yToIndex(y);
		frame.setMessage("V="+decimalFormat.format(show[i][j]));
		break;
		case InteractivePanel.MOUSE_RELEASED :
		panel.setMessage(null);
		break;
    }
  }

	public static void main(String[] args) {
		SimulationControl.createApp(new InterferensiQuantum());
	}
}