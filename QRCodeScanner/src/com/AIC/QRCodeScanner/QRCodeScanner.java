package com.AIC.QRCodeScanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.AIC.QRCodeScanner.R;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.Result;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class QRCodeScanner extends Activity {
	
	private static final String LSBuildingsDB = null;
	private static final String androidBeta = null;
	private static final int PICTURE_RESULT = 0;

	DataBaseHelper myDbHelper = new DataBaseHelper(null);
	
	List<QueryLister> model = new ArrayList<QueryLister>();
	QueryLister cl = new QueryLister();
	
	String newString = null;

	File sdcard = Environment.getExternalStorageDirectory();
	File file = new File(sdcard,"state.txt");
	
	QueryHelper helper = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		myDbHelper = new DataBaseHelper(this);

		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    Button scan=(Button)findViewById(R.id.scan);
	    scan.setOnClickListener(onScan);
	    
	    Button database=(Button)findViewById(R.id.database);
	    database.setOnClickListener(onDatabase);
	    
	    Button databaseOpen=(Button)findViewById(R.id.databaseOpen);
	    databaseOpen.setOnClickListener(onDatabaseOpen);
	    
	    Button querylist=(Button)findViewById(R.id.querycheck);
	    querylist.setOnClickListener(onQueryList);
	    
	    Button clear=(Button)findViewById(R.id.clear);
	    clear.setOnClickListener(onClear);
	    
	    //Export button no longer needed since log in log out data are written to the text file
	    //Button export=(Button)findViewById(R.id.exportToText);
	    //export.setOnClickListener(onExport);
	    
	    Button check=(Button)findViewById(R.id.checkExportData);
	    check.setOnClickListener(onCheck);
	    
		helper = new QueryHelper(this);
		
		/*
		try {
			myDbHelper.createDataBase();

			Toast msg = Toast.makeText(getBaseContext(),"Database Exists!", Toast.LENGTH_SHORT);
			msg.show();
			
		} 
		catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		 */
 
	}
	
	
	/*
	 * Regular stuff from here on
	 */
	
	//Scan Button
	public Button.OnClickListener onScan = new Button.OnClickListener() {
	    public void onClick(View v) {
	        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	        intent.setPackage("com.google.zxing.client.android");
	        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	        startActivityForResult(intent, 0);
	    }
	};
	
	
	//Clear Button
	public Button.OnClickListener onClear = new Button.OnClickListener() {
	    public void onClick(View v) {
	        
	    	PrintWriter writer;
	    	
			try {
				writer = new PrintWriter(file);
				writer.print("");
		    	writer.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	    		    	
	    	String clearAlert = cl.clear;
	        Toast clear = Toast.makeText(getBaseContext(), clearAlert, Toast.LENGTH_LONG);
	        clear.show();
	        
	        cl.clearList();
	    }
	};
	
	
	//Check Export Data - THIS ONE SHOWS THE LOG IN LOG OUT DATA~!
	public Button.OnClickListener onCheck = new Button.OnClickListener(){
		public void onClick(View v){
			
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard,"state.txt");

			//Read text from file
			StringBuilder text = new StringBuilder();
			
			if(file.exists()){
				Toast msg = Toast.makeText(getBaseContext(),"File Exists!", Toast.LENGTH_SHORT);
				msg.show();
			}
			else{
				Toast msg = Toast.makeText(getBaseContext(),"File Doesn't Exist!", Toast.LENGTH_SHORT);
				msg.show();
			}


			try {
			    BufferedReader br = new BufferedReader(new FileReader(file));
			    String line;

			    while ((line = br.readLine()) != null) {
			        text.append(line);
			        text.append('\n');
			    }
			}
			catch (IOException e) {
			    //You'll need to add proper error handling here
			}

			//Find the view by its id
			TextView tv = (TextView)findViewById(R.id.text_view);

			//Set the text
			tv.setText(text);
			tv.setMovementMethod(new ScrollingMovementMethod());
		
		
		}
	};
	
	
	//Data list version
	public Button.OnClickListener onQueryList = new Button.OnClickListener(){
		public void onClick(View v){
			String commandList = cl.getQueryList(); 
			Toast command = Toast.makeText(getBaseContext(), commandList, Toast.LENGTH_SHORT);
			command.show();
			Toast.makeText(getBaseContext(), commandList, Toast.LENGTH_LONG);
		}
	};

	
	/* DATABASE VERSION!
	public Button.OnClickListener onQueryList = new Button.OnClickListener(){
		public void onClick(View v){
			String commandList = helper.getName(1); 
			Toast command = Toast.makeText(getBaseContext(), commandList, Toast.LENGTH_LONG);
			command.show();
		}
	};
	*/
	
	//Open Database!
	public Button.OnClickListener onDatabase = new Button.OnClickListener(){
		
		public void onClick(View v){
			try {
				myDbHelper.createDataBase();

				Toast msg = Toast.makeText(getBaseContext(),"Database Exists!", Toast.LENGTH_SHORT);
				msg.show();
				
			} 
			catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
	  
	 	}

	};
	
	public Button.OnClickListener onDatabaseOpen = new Button.OnClickListener(){
		
		public void onClick(View v){
		
				try {
					myDbHelper.openDataBase();
					Toast msg = Toast.makeText(getBaseContext(),"Database Opened!", Toast.LENGTH_SHORT);
					msg.show();

					
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
			
			
	 	}

	};
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            
	            // Handle successful scan
	            if(contents.contains("TEL:") == false){
	            	newString = "random";
	            	invalidQuery();  
	            }
	            else{
	            	newString = contents.replaceAll("TEL:", "");
	            }
	            
	            //insert database queries here lawl
	            // Since the database was created properly, time to look for the Building Color and Room  
	            // of the room that the dude logged in/out of.
	            // since all the numbers correspond to a room somewhere, it's just simple SQL Query
	            // if the data is fetched properly, all that's left to do is to assign it properly to the string in the alertDialog
	           	            
	            final String queryResult = search(myDbHelper);
	            
	            //If QR Code is Legit and working, do the rest of awesome stuff.
	            if(queryResult != null){
	            	
	            	// Put stuff here, mainly this creates the dialogue box interaction
	            	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	                
	            		@Override
	            		public void onClick(DialogInterface dialog, int which) {
	            			String inputString = null;
	            			
	            			//timestamp stuff
	            			java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
	            			String date = dateFormat.format(new Date());

	            			Calendar c = Calendar.getInstance(); 
	            			int second = c.get(Calendar.SECOND);
	            			int minute = c.get(Calendar.MINUTE);
	            			int hour = c.get(Calendar.HOUR);
	                	
	            			String timestamp = date + " " + hour + ":" + minute + ":" + second; 
	                	
	            			switch (which){
	            			case DialogInterface.BUTTON_POSITIVE:
	            				inputString = "Log In" + " " + queryResult;
	            				cl.setQueryList(inputString); 
	            				break;

	            			case DialogInterface.BUTTON_NEGATIVE:
	            				inputString = "Log Out" + " " + queryResult;
	            				cl.setQueryList(inputString);
		                        break;
	            			}
	                    
	            			try {
		                		FileOutputStream os = new FileOutputStream(file, true); 
		       			     	OutputStreamWriter out = new OutputStreamWriter(os);
		       			     	out.write(inputString + " " +  timestamp + "\n");
		       			     		       			     	
		       			     	Toast msg = Toast.makeText(getBaseContext(),"Data Written!", Toast.LENGTH_SHORT);
		       			     	msg.show();
		       			     
		       			     	out.close();
		                	}
		                	catch (IOException e) {
		       		        // Unable to create file, likely because external storage is
		       		        // not currently mounted.
		       				
		                		Toast msg = Toast.makeText(getBaseContext(),"Failed to write!", Toast.LENGTH_LONG);
		                		msg.show();
		       				
		                		Log.w("ExternalStorage", "Error writing " + file, e);
		                	}	
	                    
	            			cl.setQueryList("\n");
	            		}
	            	};
	            

		            //this one over here creates an instance of that dialogue box and displays it
		            AlertDialog.Builder builder = new AlertDialog.Builder(this);
		            
		            builder.setMessage("Welcome to " + queryResult + "!").setPositiveButton("Log In!", dialogClickListener)
		                .setNegativeButton("Log Out!", dialogClickListener).show();
		            
		            //TODO fix camera intent 
		            
		            openCam();
		            
		            
		        } 
	            
	            //Just in case wrong QR Code is scanned
	            else{
	            	//Toast msg = Toast.makeText(getBaseContext(), "Invalid QR Code Scanned!", Toast.LENGTH_LONG);
	        	    //msg.show();
	            	
	            	invalidQuery();
	            }
	        }
	        
	        else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        }
	    }
	}
	
	public String search(DataBaseHelper myDB){
		
		String data = null;
		Cursor cursor = null;
		SQLiteDatabase db = myDB.getReadableDatabase();
		
		try{
			cursor = db.rawQuery("SELECT BuildingColor, Room FROM LSBuildingsDB WHERE _id =" + newString, null);
		}
		catch(Exception e){
			//Nothing to do here. Just so the app doesn't crash like a fucking noob every time I scan a wrong QR Code.
			//All I need to do is to set data to null (done above) and let the rest of the code do the awesome work!
		}
		
		if( cursor != null ){
			cursor.moveToFirst();
			
			data = cursor.getString(cursor.getColumnIndexOrThrow("BuildingColor")) + " " +
		  			cursor.getString(cursor.getColumnIndex("Room"));   
			cursor.close();
		}

        return data;

	}
	

	public void invalidQuery(){
		Toast msg = Toast.makeText(getBaseContext(), "Invalid QR Code Scanned!", Toast.LENGTH_LONG);
	    msg.show();
	    
	}

	
	@Override
	public void onDestroy() { 
		super.onDestroy();
		helper.close(); 
	}
	
	/*
	public class Camera extends Object{
		
		//System.println("Camera opened!");
		//System.out.println("onSaveInstanceState()" );
	}
	
	
	public static Camera open (int cameraId){
    	return null;
    }
    */
	
	private void openCam(){
		
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
        this.startActivityForResult(camera, PICTURE_RESULT);
		
	}
	
}