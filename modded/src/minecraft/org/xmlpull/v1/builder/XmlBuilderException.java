package org.xmlpull.v1.builder;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XmlBuilderException extends RuntimeException {
	protected Throwable detail;

	public XmlBuilderException(String s) {
		super(s);
	}

	public XmlBuilderException(String s, Throwable thrwble) {
		super(s);
		this.detail = thrwble;
	}

	public Throwable getDetail() {
		return this.detail;
	}

	public String getMessage() {
		return this.detail == null ? super.getMessage() : super.getMessage() + "; nested exception is: \n\t" + this.detail.getMessage();
	}

	public void printStackTrace(PrintStream ps) {
		if(this.detail == null) {
			super.printStackTrace(ps);
		} else {
			synchronized(ps) {
				ps.println(super.getMessage() + "; nested exception is:");
				this.detail.printStackTrace(ps);
			}
		}

	}

	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	public void printStackTrace(PrintWriter pw) {
		if(this.detail == null) {
			super.printStackTrace(pw);
		} else {
			synchronized(pw) {
				pw.println(super.getMessage() + "; nested exception is:");
				this.detail.printStackTrace(pw);
			}
		}

	}
}
