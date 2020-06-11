package fr.stemprado.apps.mocktar.exceptions;

public class SequenceException extends RuntimeException {
    
    private static final long serialVersionUID = -8562274904226413552L;

    public SequenceException(String errMsg) {
		super(errMsg);
	}
}