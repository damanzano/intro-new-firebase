import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import processing.core.PApplet;

public class Ejecutable extends PApplet {
	private FirebaseDatabase database;
	private List<Bolita> bolitas;

	public static void main(String[] args) {
		PApplet.main("Ejecutable");
	}

	public void settings() {
		size(500, 500);

	}

	public void setup() {
		// Configurar la conexión
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setServiceAccount(new FileInputStream("intro-redes-f721d3241013.json"))
					.setDatabaseUrl("https://intro-redes.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Inicializar la referencia a la bd y las colecciones
		database = FirebaseDatabase.getInstance();
		bolitas = Collections.synchronizedList(new ArrayList<Bolita>());
		
		// Consultar las bolitas iniciales
		consultarBolitas();

	}

	public void draw() {
		synchronized(bolitas){
			for (Iterator iterator = bolitas.iterator(); iterator.hasNext();) {
				Bolita bolita = (Bolita) iterator.next();
				ellipse(bolita.getX(), bolita.getY(), 50, 50);
			}
		}
		

	}
	
	public void mousePressed(){
		crearBolita(mouseX,mouseY);
	}

	/**
	 * Escuchar que pasa en la bd
	 */
	public void consultarBolitas() {
		// Obtener la referencia en la que estamos interesados
		DatabaseReference ref = database.getReference("bolitas");
		
		// Agregar un listener  a dicha referencia
		ref.addValueEventListener(new ValueEventListener() {
			
			// Establacer que se hace cuando dicha referencia cambia
			public void onDataChange(DataSnapshot dataSnapshot) {
				Object value = dataSnapshot.getValue();
				System.out.println(value);

				System.out.println("There are " + dataSnapshot.getChildrenCount() + " bolitas");

				for (DataSnapshot bolitaSnapshot : dataSnapshot.getChildren()) {
					Bolita b = bolitaSnapshot.getValue(Bolita.class);
					System.out.println(bolitaSnapshot.getKey() + " " + b.toString());
					bolitas.add(b);
				}

			}
			
			// Establecer que hacer si se cancela la operación
			public void onCancelled(DatabaseError databaseError) {
				System.out.println("The read failed: " + databaseError.getCode());
			}
		});
	}
	
	/**
	 * Crear una nueva bolita en el lienzo
	 * @param x
	 * @param y
	 */
	public void crearBolita(int x, int y){
		// Construir el objeto
		Bolita b = new Bolita();
		b.setX(x);
		b.setY(y);
		
		// Obtener la referencia donde vamos a guardar
		DatabaseReference ref = database.getReference("bolitas");
		
		// Crear una nueva clave
		DatabaseReference bolitaId = ref.push();
		
		bolitaId.setValue(b, new DatabaseReference.CompletionListener() {
			
			public void onComplete(DatabaseError error, DatabaseReference ref) {
				// TODO Auto-generated method stub
				if (error != null) {
					System.out.println("No se pudieron guardar los datos " + error.getMessage());
				} else {
					System.out.println("Datos guardados satisfactoriamente.");
				}
			}
		});
		
	}

}
