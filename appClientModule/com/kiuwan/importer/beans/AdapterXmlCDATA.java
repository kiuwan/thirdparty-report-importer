package com.kiuwan.importer.beans;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AdapterXmlCDATA extends XmlAdapter<String, String> {

    @Override
    public String marshal(String value) throws Exception {
        //return "<![CDATA[" + value + "]]>";
        return value;
    }
    @Override
    public String unmarshal(String value) throws Exception {
        return value;
    }

}