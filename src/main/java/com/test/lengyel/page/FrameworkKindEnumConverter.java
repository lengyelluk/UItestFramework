package com.test.lengyel.page;

import com.test.lengyel.gui.FrameworkKindEnum;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FrameworkKindEnumConverter implements Converter {

    public static FrameworkKindEnumConverter getInstance() {
    return new FrameworkKindEnumConverter();
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        context.convertAnother(value, new EnumConverter());
    }

    //method that return the enum value by string
    //if value equals return the correct enum value
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    	String value = reader.getValue();
    	if(value == null){
    		return null;
    	} else if(value != null && value.length() == 0){
    		return null;
    	} else {
    		return FrameworkKindEnum.valueOf(reader.getValue().toUpperCase());
    	}
    }

    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return FrameworkKindEnum.class.isAssignableFrom(clazz);
    }
}