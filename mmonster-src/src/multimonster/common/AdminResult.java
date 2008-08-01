package multimonster.common;

import java.io.Serializable;

/**
 * A very generic container to bring back the result of an administrative command.
 * this could be a list of settings or the acknowledge that a plugin was deployed successful or anything else.
 * for that reason the resultID is needed, in order to get to know what is inside of "result".
 * perhaps this ID is never used because receiver of this AdminResult knows what he expects. 
 */
public class AdminResult implements Serializable{ 

	private int resultID;
	private Object[] result;

	public AdminResult(int id, Object[] result){
		this.resultID = id;
		this.result = result;
	}
	
	public int getResultID() {
		return resultID;
    }

    public Object getResult(int index) {
		if (result != null){
			if ((index >= 0) && (index < result.length)){
				return result[index];
			}		
		} 		
		return null;

    }

 }
