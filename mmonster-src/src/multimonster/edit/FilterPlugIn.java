package multimonster.edit;

import multimonster.common.pipe.Pipe;
import multimonster.common.plugin.PlugIn;

public abstract class FilterPlugIn extends PlugIn implements Runnable{
	
	private boolean finished = false;
	
	public FilterPlugIn(){		
	}
  
    abstract public void setInput(Pipe input);
    abstract public void setOutput(Pipe output);
 
    public synchronized void setFinished(){
    	this.finished = true;
    	notifyAll();
    }
    
    public synchronized void waitForFinishing() throws InterruptedException{
    	while (!finished){
    		wait();
    	}
    }
}
