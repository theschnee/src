/* 
 * Author: Callum Hafner Schnee, Independent, September 4 2018 
 * EventCreator is a licensed file and any attempt to use it in a program other than Consyl will be persecuted
 * Copyright License c. 2018 
 * 
 * 
 * Note: it may be more efficient to create an indicator object for the date read/write algorithm so that the code is not all within the algorithm and repeated, but only reachable in cases where it is necessary
 * Daynames array and indicators may not be necessary for the successful implementation of this algorithm 
 */

import java.io.*;
import java.text.DateFormatSymbols;
import java.util.*;
import java.time.*; 

import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class EventCreator {
	
	private File converted; 
	private ArrayList<ArrayList<String>> dates;
	
	public EventCreator() {
		SyllabusReader reader = new SyllabusReader();
		converted=reader.getTxt();
	}
	
	public EventCreator(File converted) {
		this.converted=converted;
	}
	
	public String getCourseName() {
		return null; 
		//TO-DO: subject or class name pulled from document and returned here 
	}
	
	public void sortTextFile() { 
		// TO-DO: If necessary, sort text file into lines starting with the notation for the date and following with the assignment due 
	}
	
	public ArrayList<ArrayList<String>> createDates() {
		dates = new ArrayList<ArrayList<String>>();
		ArrayList<String> days = new ArrayList<String>();
		
		// adds all the days of the month to the ArrayList
		for(int i=0; i<31; i++) {
			String day = Integer.toString(i);
			days.add(day); 
		}
		
		// adds all the months in the year to the ArrayList
		ArrayList<String> months = new ArrayList<String>();
		String[] m = new DateFormatSymbols().getMonths(); 
		for(int k=0; k<12; k++) {
			String month = m[k];
			months.add(month);
		}
		
		// adds all the manes of the days of the week to the ArrayList
		ArrayList<String> daynames = new ArrayList<String>();
		daynames.add("monday");
		daynames.add("tuesday");
		daynames.add("wednesday");
		daynames.add("thursday");
		daynames.add("friday");
		daynames.add("saturday");
		daynames.add("sunday");
		
		// adds the ArrayLists created into the parent ArrayList
		dates.add(days);
		dates.add(months);
		dates.add(daynames); 
		
		return dates; 
	}
	
	public void login() {
		//To-Do: create login for google, other platforms 
	}
	
	@SuppressWarnings("deprecation")
	public void createEvents() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(converted));
		String line; 
		
		//reads document line by line 
		while((line=reader.readLine())!=null) {
			//splits sentences into individual words by comma, forward slash, and space
			Date date = null;
			String[] tokens = line.split(":|,| ");
			String idxone=null;
			String idxtwo=null;
			String idxthree=null;
			String tokenone=null;
			String tokentwo=null;
			String tokenthree=null;
			int day = 0;
			int month = 0; 
			int year = 0;
			int idxcount=0;
			//goes through each word in the line 
			for(int i=0; i<tokens.length; i++) {
				if(tokens[i].contains("/") && tokens[i].matches(".*\\d+.*")) {
					date = getSlashDate(tokens[i]);
					break;
				}
				for(int j=0; j<dates.size(); j++) {
					for(String s:dates.get(j)) {
						//if the token equals any known indicator from dates or a 4-digit number within 100 from 2000 representative of the year
						if(tokens[i].toLowerCase()==s || Integer.parseInt(tokens[i])>=1900 && Integer.parseInt(tokens[i])<=2100) {
							//the day and month in a non-slashDate occurrence will be noted and stored when the index is taken
							if(idxone==null) {
								idxone=tokens[i];
								idxcount=1;
								if(j==0) day=dates.get(j).indexOf(s)+1;
								else if(j==1) month=dates.get(j).indexOf(s)+1;
								else if(j==2) tokenone="dayname";
								else year=Integer.parseInt(tokens[i]);
							} else if(idxtwo==null) {
								idxtwo=tokens[i];
								idxcount=2;
								if(j==0) day=dates.get(j).indexOf(s)+1;
								else if(j==1) month=dates.get(j).indexOf(s)+1;
								else if(j==2) tokentwo="dayname";
								else year=Integer.parseInt(tokens[i]);
							} else if(idxthree==null) {
								idxthree=tokens[i];
								idxcount=3; 
								if(j==0) day=dates.get(j).indexOf(s)+1;
								else if(j==1) month=dates.get(j).indexOf(s)+1;
								else if(j==2) tokenthree="dayname";
								else year=Integer.parseInt(tokens[i]);
							}
						}
					}
				}
				if(idxcount==3) break;
			}			
			
			//another way to scan and find the day and year
			/* if(idxone.matches(".*\\d+.*")) day=Integer.parseInt(idxone);
			   else if	(idxtwo.matches(".*\\d+.*")) day=Integer.parseInt(idxtwo);
			   else day=Integer.parseInt(idxthree);
			*/
			
			//scans and finds the year - if no year found current year used
			if(year==0) year = Year.now().getValue();
			
			//Creates the date that will be used in the calendar - Note: use date until google api switches to localtime from Java 8
			date = new Date(year, month, day);
			
			//re-combines the rest of the line that is not a date and creates a description
			StringBuilder builder = new StringBuilder();
			for(int k=idxcount; k<tokens.length; k++) {
			    builder.append(tokens[k]);
			}
			String description = builder.toString();
			
			// TO-DO: Create if statement for different calendars depending on which calendar user is using (google, apple, microsoft, etc.)
			createGoogleEvent(description, date); 
			
		}
	reader.close(); 
	}
	
	//Deals with the special case of slash dates
	@SuppressWarnings("deprecation")
	public Date getSlashDate(String slashDate) {
			String slashTwo;
			String slashThree;
			int day;
			int month;
			int year;
		//separation of first number 
			int a=slashDate.indexOf("/");
			if(a!=slashDate.lastIndexOf("/")) slashTwo=slashDate.substring(a+1, slashDate.lastIndexOf("/"));
			else slashTwo = slashDate.substring(a+1,slashDate.length());
			slashDate=slashDate.substring(0,a);
		//separation of second and third numbers
			int b=slashTwo.indexOf("/");
			slashThree=slashTwo.substring(b+1,slashTwo.length());
			slashTwo=slashTwo.substring(0,b);

		//Creates the date that will be used in the calendar
		//America: month/day/year Europe: day/month/year
		TimeZone timezone = TimeZone.getDefault();
		if("EST" == timezone.getDisplayName() || "CST" == timezone.getDisplayName() || "PST" == timezone.getDisplayName() || "MST" == timezone.getDisplayName()) {
			month = Integer.parseInt(slashDate);
			day = Integer.parseInt(slashTwo);
			year = Integer.parseInt(slashThree);
		} else {
			day = Integer.parseInt(slashDate);
			month = Integer.parseInt(slashTwo);
			year = Integer.parseInt(slashThree);
		}
		return new Date(year, month, day);
	}

	public void createGoogleEvent(String description, Date date) {
					Event event = new Event();
					event.setSummary(getCourseName()+" Assignment"); 
					event.setLocation("Dartmouth College, Hanover NH"); //TO-DO: Pull location of university 
					event.setDescription(description);

						DateTime startDateTime = new DateTime(date);
						TimeZone timezone = TimeZone.getDefault();
						EventDateTime start = new EventDateTime()
						    .setDateTime(startDateTime)
						    .setTimeZone(timezone.getID());
						event.setStart(start);
	}
	
	public void createMicrosoftEvent(String description, Date date) {
		// TO-DO: Creates event that gets inserted into a Microsoft calendar 
	}
	
	public void createAppleEvent(String description, Date date) {
		// TO-DO: Creates event that gets inserted into iCal
	}
	
	public void oldDateMethod() {
		String idxone = null;
		String idxtwo = null;
		String idxthree = null;
		if(idxone.contains("/")) {
			int a=idxone.indexOf("/");
			if(a!=idxone.lastIndexOf("/")) idxtwo=idxone.substring(a+1, idxone.lastIndexOf("/"));
			else idxtwo=idxone.substring(a+1,idxone.length());
			idxone=idxone.substring(0,a);
		} else if(idxtwo.contains("/")) {
			int a=idxtwo.indexOf("/");
			if(a!=idxtwo.lastIndexOf("/")) idxthree=idxtwo.substring(a+1, idxtwo.lastIndexOf("/"));
			else idxthree=idxtwo.substring(a+1,idxtwo.length());
			idxtwo=idxtwo.substring(0,a);
		} else if(idxthree.contains("/")) {
			int a=idxthree.indexOf("/");
			if(a!=idxthree.lastIndexOf("/")) idxone=idxthree.substring(a+1, idxthree.lastIndexOf("/"));
			else idxone=idxone.substring(a+1,idxone.length());
			idxthree=idxthree.substring(0,a);
		}
		//Creates the date that will be used in the calendar
		//America: month/day/year Europe: day/month/year
		TimeZone timezone = TimeZone.getDefault();
		if("EST" == timezone.getDisplayName() || "CST" == timezone.getDisplayName() || "PST" == timezone.getDisplayName() || "MST" == timezone.getDisplayName()) {
			int month = Integer.parseInt(idxone);
			int day = Integer.parseInt(idxtwo);
			int year = Integer.parseInt(idxthree);
		} else {
			int day = Integer.parseInt(idxone);
			int month = Integer.parseInt(idxtwo);
			int year = Integer.parseInt(idxthree);
		}

	}
}


