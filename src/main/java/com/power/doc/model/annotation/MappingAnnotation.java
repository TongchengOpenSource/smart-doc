package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class MappingAnnotation {

  private String annotationName;

  private String annotationFullyName;

  private String valueProp;

  private String nameProp;

  public String getAnnotationName() {
    return annotationName;
  }

  public void setAnnotationName(String annotationName) {
    this.annotationName = annotationName;
  }

  public String getValueProp() {
    return valueProp;
  }

  public void setValueProp(String valueProp) {
    this.valueProp = valueProp;
  }

  public String getNameProp() {
    return nameProp;
  }

  public void setNameProp(String nameProp) {
    this.nameProp = nameProp;
  }

  public String getAnnotationFullyName() {
    return annotationFullyName;
  }

  public void setAnnotationFullyName(String annotationFullyName) {
    this.annotationFullyName = annotationFullyName;
  }
}
