/******************************************************************
 *  Compilation:  javac OpenPhysicsTemplate.java
 *  Execution:    java OpenPhysicsTemplate
 *  Dependencies: Open Source Physics Library (http://www.opensourcephysics.org)
 *
 *	by Rian Affandi
 *  22 Januari 2017 
 *
 ******************************************************************/
import java.awt.event.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
import org.opensourcephysics.frames.*;

public class OpenPhysicsTemplate extends AbstractSimulation implements InteractiveMouseHandler {

	//DEKLARASIKAN SEMUA VARIABEL YANG AKAN DIGUNAKAN DISINI!
	
	public void initialize() {
		//MASUKAN NILAI AWAL DAN KAPASITAS ARRAY YANG DIGUNAKAN
	}

	public void initArrays() {
		//TULISKAN ARAY YANG INGIN DIBUATKAN GRAFIK DISINI
	}

	public OpenPhysicsTemplate() {
		//BIARKAN SEPERTI INI
		frame.setInteractiveMouseHandler(this);
	}

	public void doStep() {
		//SEGALA YANG INGIN DI ITERASIKAN DITULIS DISINI
	}
	
	public void reset() {
		//ATUR VARIABEL YANG INGIN DI KONTROL DI SINI
	}
  
	public void handleMouseAction(InteractivePanel panel, MouseEvent evt) {
		//INTERAKSI MOSE DITULIS DISINI
    }
  }
    
	public static void main(String[] args) {
		//SIMULASI DIMULAI!
		SimulationControl.createApp(new OpenPhysicsTemplate());
	}

}