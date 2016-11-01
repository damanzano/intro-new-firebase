import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import processing.core.PApplet;

public class Ejecutable extends PApplet {
	FirebaseDatabase database;
	ArrayList<Bolita> bolitas;
	
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
		bolitas = new ArrayList<Bolita>();
		
		// Escuchar que pasa en la bd
		DatabaseReference ref = database.getReference("bolitas");
		
		ref.addValueEventListener(new ValueEventListener() {
		    
		    public void onDataChange(DataSnapshot dataSnapshot) {
		        Object post = dataSnapshot.getValue();
		        System.out.println(post);
		        Bolita b = dataSnapshot.getValue(Bolita.class);
		        bolitas.add(b);
		    }
		    
		    public void onCancelled(DatabaseError databaseError) {
		        System.out.println("The read failed: " + databaseError.getCode());
		    }
		});

	}

	public void draw() {
		for (Iterator iterator = bolitas.iterator(); iterator.hasNext();) {
			Bolita bolita = (Bolita) iterator.next();
			ellipse(bolita.getX(), bolita.getY(), 50, 50);
		}
		
	}

}
