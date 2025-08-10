package org.xmlpull.v1.builder;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public interface XmlSerializable {
	void serialize(XmlSerializer xmlSerializer1) throws IOException;
}
