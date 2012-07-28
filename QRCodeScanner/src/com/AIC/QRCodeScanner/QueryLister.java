package com.AIC.QRCodeScanner;

public class QueryLister {
	
	private String QueryList = "";
	private String buffer = "";
	
	public String clear = "All commands have been cleared.";
	
	public String getQueryList() {
	    return(QueryList);
	}
	  
	public void setQueryList(String Commandlist) {
		buffer = Commandlist;
	    this.QueryList = this.QueryList + buffer;
	    //or this.Commandlist = Commandlist;
	    //this should work since the CommandList is a list already.
	    	    
	}
	
	public void clearList() {
		this.QueryList = "";
	}

}
