package de.enough.polish;

public interface ErrorHandler {
	
	public void handleBuildFailure( String deviceIdentifier, String locale, Throwable exception );

}
