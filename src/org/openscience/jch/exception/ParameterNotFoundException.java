//*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
package org.openscience.jch.exception;
//
public class ParameterNotFoundException extends Exception {
    
    public ParameterNotFoundException(String message) {
        super(message);
    }

    public ParameterNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    public String getMessage(){
        return super.getMessage();
    }
}


