package multimonster.converter;

import multimonster.common.Format;
import multimonster.common.pipe.Pipe;
import multimonster.common.plugin.PlugIn;
import multimonster.converter.exceptions.ConverterException;

/**
 * This abstract class has to be extended in order to write a plugin for
 * the converter component.
 * The 'work' is done in the <code>run</code> method (Runnable intarface).
 * The plugin works in it's own 'thread'.
 * 
 * @author Holger Velke (sihovelk)
 */
public abstract class ConverterPlugIn extends PlugIn implements Runnable {	
	protected Pipe output;
	protected Pipe input;

	/**
	 * The standart constuctor is the only constructor that will be used
	 * instanciating a plugin. For initialisation use the <code>init</code>
	 * method
	 */
	public ConverterPlugIn(){}
	
	/**
	 * @param output The output to set.
	 */
	public void setOutput(Pipe output) {
		this.output = output;
	}

	/**
	 * @param input The input to set.
	 */
	public void setInput(Pipe input) {
		this.input = input;
	}
	
	/**
	 * used for format-specific initailization of the plugin.
	 * 
	 * @param input the input foramt
	 * @param output the output format
	 * @throws ConverterException if the init fails
	 */
	abstract public void init(Format input, Format output) throws ConverterException;
}
