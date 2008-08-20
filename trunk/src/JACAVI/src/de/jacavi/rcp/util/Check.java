/**
 * 
 */
package de.jacavi.rcp.util;




/**
 * Design by Contracts
 * <p>
 * Each Method generates an Exception or an assert if the contract is broken
 * <p>
 * Use like the following example
 * <p>
 * <code>
 * public int doSomething(int i,List<String> list)
 * {
 *      int retVal=0;
 *      //precondition
 *      Check.Require(i>0 && list.size()>0,"i may be > 0 and list must have at least one element");
 *      //other code
 *      //set retVal
 *      //postcondition
 *      Check.Ensure(retVal>10 && retVal<100,"retVal may be between 10 and 100");
 *      return retVal;
 *  
 * }
 * </code>
 * 
 * @author Florian Roth
 */
public class Check {

    private static boolean useExceptions = true;

    // No instance
    private Check() {}

    /**
     * @param exceptions
     *            <p>
     *            true and Exceptions are on otherwise asserts are used
     */
    public static void useExceptions(boolean exceptions) {
        useExceptions = exceptions;
    }

    /**
     * @param assertion
     *            <p>
     *            The contract to proof
     */
    public static void Require(boolean assertion) {
        if(useExceptions) {
            if(!assertion)
                throw new PreconditionException("Precondition failed.");
        } else
            assert assertion: "Precondition failed.";
    }

    public static void Require(boolean assertion, String message) {
        if(useExceptions) {
            if(!assertion)
                throw new PreconditionException("Precondition failed: " + message);
        } else
            assert assertion: "Precondition failed: " + message;
    }

    public static void Require(boolean assertion, String message, Exception inner) {
        if(useExceptions) {
            if(!assertion)
                throw new PreconditionException(message, inner);
        } else
            assert assertion: "Precondition failed: " + message + "\n" + inner.getMessage();
    }

    public static void Ensure(boolean assertion) {
        if(useExceptions) {
            if(!assertion)
                throw new PostconditionException("Postcondition failed.");
        } else
            assert assertion: "Postcondition failed.";
    }

    public static void Ensure(boolean assertion, String message) {
        if(useExceptions) {
            if(!assertion)
                throw new PostconditionException(message);
        } else
            assert assertion: "Postcondition failed: " + message;
    }

    public static void Ensure(boolean assertion, String message, Exception inner) {
        if(useExceptions) {
            if(!assertion)
                throw new PostconditionException(message, inner);
        } else
            assert assertion: "Postcondition failed: " + message + "\n" + inner.getMessage();
    }
}



@SuppressWarnings("serial")
class DesignByContractException extends RuntimeException {
    public DesignByContractException() {}

    public DesignByContractException(String message) {
        super(message);
    }

    public DesignByContractException(String message, Throwable inner) {
        super(message, inner);
    }
}



@SuppressWarnings("serial")
class PreconditionException extends DesignByContractException {

    public PreconditionException() {
        super();
    }

    public PreconditionException(String message) {
        super(message);
    }

    public PreconditionException(String message, Throwable inner) {
        super(message, inner);
    }
}



@SuppressWarnings("serial")
class PostconditionException extends DesignByContractException {
    public PostconditionException() {}

    public PostconditionException(String message) {
        super(message);
    }

    public PostconditionException(String message, Throwable inner) {
        super(message, inner);
    }

}
