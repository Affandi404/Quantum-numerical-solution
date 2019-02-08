/******************************************************************
 *  Compilation:  javac EksitasiQuantum.java
 *  Execution:    java EksitasiQuantum
 *  Dependencies: Open Source Physics Library (http://www.opensourcephysics.org)
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

public class EksitasiQuantum extends AbstractSimulation implements InteractiveMouseHandler {
	Scalar2DFrame frame = new Scalar2DFrame("x", "y", "SCHRODINGER2D Numeric");
	Scalar2DFrame frame1 = new Scalar2DFrame("x", "y", "SCHRODINGER2D Analytic");

	int i, j, k, l, step, nx;
	double s, cc, x, y, dx, dt, m, sigmaN, error, skip, ervalue;
	long n;
	
    double [][] psi; // psi(x)
    double [][][] psie; // psi eksitasi(x)
    double [][] psitemp ; // psitemp(x)
    double [][] v ; // potensial v(x)	
    double [][] ca; // ca	
    double [][] cb; // cb	
    double [][][] analitic; // referensi secara analitik		
    double [][] analiticshow;// menampilkan refrensi analitik
    double [] sigmaA; //hitung error analitik
	
	public void initialize() {
		n = l = 0;
		error = 1.0;
		dx = 1.0/nx;
		dt = dx*dx/20.0; // untuk stabil dt < dx^2
		m = dt/(2.0*dx*dx);
		
		//Nilai Yang bisa disesuaikan oleh user
		nx = control.getInt("pixel");  
		skip = control.getInt("skip"); 		
		ervalue = control.getDouble("error");
		
		//deklarasi jenis aray
		v = new double [nx+1][nx+1]; 
		ca = new double [nx+1][nx+1]; 
		cb = new double [nx+1][nx+1];
		analitic = new double [nx+1][nx+1][2]; 
		analiticshow = new double [nx+1][nx+1]; 	
		sigmaA = new double [2]; 
		psi = new double [nx+1][nx+1];
		psie = new double [nx+1][nx+1][2];
		psitemp = new double [nx+1][nx+1]; 
		
		for(k=0;k<=1;k++){
			for(i=0; i<=nx; i++){
				for(j=0;j<=nx;j++){
					v[i][j]=0.0;				
					x = (double)i/nx;
					y = (double)j/nx;
					analitic[i][j][k] = 2.0*Math.sin((double)(1.0+k)*x*Math.PI)*Math.sin(y*Math.PI);				
					sigmaA[k]+=analitic[i][j][k];
				}
			}
		}

		for(i=0; i<=nx; i++){
			for(j=0;j<=nx;j++){
				ca[i][j] = (1.0 - 0.5*dt*v[i][j])/(1.0 + 0.5*dt*v[i][j]);
				cb[i][j] = m/(1.0 + 0.5*dt*v[i][j]);
				}
		}
		
		for(i=1; i<nx; i++){
			for(j=1;j<nx;j++){
				psi[i][j] = Math.random();
				analiticshow[i][j]=analitic[i][j][0];
			}
		}

		initArrays();
		frame1.setVisible(true);
		frame.setVisible(true);
	}

	public void initArrays() {
		frame1.setAll(analiticshow);
		frame.setAll(psi);
	}

	public EksitasiQuantum() {
    frame.setInteractiveMouseHandler(this);
	}

	public void doStep() {
		
		//cek sudah layak eksitasi belum
		if(ervalue>=error){
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					psie[i][j][l]=psi[i][j];//simpan nilai
					psi[i][j]=Math.random();
				}					
			}
			
			l++;
			
			for(k=0;k<l;k++){//orthogonalitas
				cc = 0.0;
				for(i=1; i<nx; i++){
					for(j=1;j<nx;j++){
						cc+=psi[i][j]*psie[i][j][k];
					}
				}
			
				cc = cc*dx*dx;

				for(i=1; i<nx; i++){
					for(j=1;j<nx;j++){
					psi[i][j]=psi[i][j]-(cc*psie[i][j][k]); 
					}
				}
				
				for(i=1; i<nx; i++){
					for(j=1;j<nx;j++){
						analiticshow[i][j]=analitic[i][j][l];
					}
				}
				
			}
		}
		
		for(step=0; step<skip;step++){
			n++;
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
				psitemp[i][j] = 10.0*(ca[i][j]*psi[i][j] + cb[i][j]*(psi[i+1][j] + psi[i-1][j] + psi[i][j+1] + psi[i][j-1] - 4.0*psi[i][j]));  
				}
			}
			// simpan psi
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					psi[i][j] = psitemp[i][j];
				}
			}
			//normalisasi
			sigmaN = s = 0.0;
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					s += psi[i][j]*psi[i][j];
				}
			}	
			s = Math.sqrt(s*dx*dx);
			for(i=1; i<nx; i++){
				for(j=1;j<nx;j++){
					psi[i][j] = psi[i][j]/s;			
					sigmaN += psi[i][j];			
				}
			}
			
		}
		
		//hitung nilai error
		error = sigmaN-sigmaA[l];
		System.out.println(n + ", error = " + error);
		
		frame1.setAll(analiticshow);
		frame.setAll(psi);
	}
	
	public void reset() {
		control.setValue("pixel", 100);
		control.setValue("skip", 100);
		control.setValue("error", 5.0E-12);
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
		frame.setMessage("psi="+decimalFormat.format(psi[i][j]));
		break;
		case InteractivePanel.MOUSE_RELEASED :
		panel.setMessage(null);
		break;
    }
  }
    
	public static void main(String[] args) {
		SimulationControl.createApp(new EksitasiQuantum());
	}

}