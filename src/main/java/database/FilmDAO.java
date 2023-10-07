package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;

import models.Film;

public class FilmDAO {
	
	Film oneFilm = null;
	Connection conn = null;
    Statement stmt = null;
	String user = "safilama";
    String password = "fingsteR2";
    // Note none default port used, 6306 not 3306
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/"+user;

	public FilmDAO() {}

	
	private void openConnection(){
		// loading jdbc driver for mysql
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) { System.out.println(e); }

		// connecting to database
		try{
			// connection string for demos database, username demos, password demos
 			conn = DriverManager.getConnection(url, user, password);
		    stmt = conn.createStatement();
		} catch(SQLException se) { System.out.println(se); }	   
    }
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Film getNextFilm(ResultSet rs){
    	Film thisFilm=null;
		try {
			thisFilm = new Film(
					rs.getInt("id"),
					rs.getString("title"),
					rs.getInt("year"),
					rs.getString("director"),
					rs.getString("stars"),
					rs.getString("review"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisFilm;		
	}
	
	
	
   public ArrayList<Film> getAllFilms(){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
   }
   
   public ArrayList<Film> getFilmByTitle(String title){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "SELECT * FROM films WHERE title LIKE '%"+title+"%'";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    stmt.close();  
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
  }
   
   public ArrayList<Film> getFilmByYear(int year){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "SELECT * FROM films WHERE year = '"+year+"'"; 
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
}
   
   public ArrayList<Film> getFilmByStar(String star){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "SELECT * FROM films WHERE stars LIKE '%"+star+"%'"; 
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
 }

   public Film getFilmByID(int id){
	   
		openConnection();
		oneFilm=null;
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films where id="+id;
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return oneFilm;
   }
   
   
   
   public boolean deleteFilm(int id) throws SQLException {

		boolean b = false;
		try {
			String selectSQL = "delete from films where id = " + id;
			System.out.println(selectSQL);
			openConnection();
			b = stmt.execute(selectSQL);
			closeConnection();
			b = true;
		} catch (SQLException s) {
			throw new SQLException("Film not deleted");
		}
		return b;
	}
   
   public boolean editFilm(int id, String title,int year, String director, String stars, String review) throws SQLException {

		boolean b = false;
		try {
			String selectSQL = "update films set title= '"+title+"' ,year= '"+year+"',director= '"+director+"', stars='"+stars+"', review='"+review+"' where id=" + id;
			System.out.println(selectSQL);
			openConnection();
			b = stmt.execute(selectSQL);
			closeConnection();
			b = true;
		} catch (SQLException s) {
			throw new SQLException("Failed to edit film");
		}
		return b;
	}
   
   public boolean addFilm(Film f) throws SQLException {

		boolean b = false;
		try {
			String selectSQL = "insert into films (title,year,director,stars,review) values ('" + f.getTitle() + "','" + f.getYear() + "','" + f.getDirector() +"','" +f.getStars()+"','"+f.getReview()+"');";
			System.out.println(selectSQL);
			openConnection();
			b = stmt.execute(selectSQL);
			closeConnection();
			b = true;
		} catch (SQLException s) {
			throw new SQLException("Failed to add film");
		}
		return b;
		
		
	}
   
   
   
   
}
