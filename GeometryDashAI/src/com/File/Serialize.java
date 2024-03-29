package com.File;

public abstract class Serialize {
    public abstract String serialize(int tabSize);
    public String addStringProperty(String name,String value,int tabSize,boolean newline,boolean comma)
    {
        return addTabs(tabSize) + "\"" + name + "\": " + "\"" + value + "\"" + addEding(newline, comma);
    }
    public String addIntProperty(String name,int value,int tabSize,boolean newline,boolean comma)
    {
        return addTabs(tabSize) + "\"" + name + "\": " + value +addEding(newline, comma);
    }
    public String addFloatProperty(String name,float value,int tabSize,boolean newline,boolean comma)
    {
        return addTabs(tabSize) + "\""+name+"\": "+value + "f" + addEding(newline,comma);
    }
    public String addDoubleProperty(String name,double value,int tabSize,boolean newline,boolean comma)
    {
        return addTabs(tabSize) + "\""+name+"\": "+value + addEding(newline,comma);
    }
    public String addBooleanProperty(String name,boolean value,int tabSize,boolean newline,boolean comma)
    {
        return addTabs(tabSize) + "\""+name+"\": "+value + addEding(newline,comma);
    }

    public String beginObjectProperty(String name,int tabSize)
    {
        return addTabs(tabSize) + "\"" + name + "\": {" +addEding(true,false);
    }
    public String closeObjectProperty(int tabSize)
    {
        return addTabs(tabSize) + "}";
    }
    public String addTabs(int tabSize)
    {
        return "\t".repeat(tabSize);
    }
    public String addEding(boolean newline,boolean comma)
    {
        String str = "";
        if(comma) str+=",";
        if(newline) str+="\n";
        return str;
    }
}
