/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.xmlbeans.impl.binding.bts;

import org.apache.xmlbeans.impl.binding.bts.BindingType;

import javax.xml.namespace.QName;

/**
 * Represents builtin bindings.
 */
public class BuiltinBindingLoader extends BaseBindingLoader {

  // ========================================================================
  // Constants

  private static final String xsns = "http://www.w3.org/2001/XMLSchema";

  private static final BuiltinBindingLoader INSTANCE = new BuiltinBindingLoader();

  // ========================================================================
  // Factory

  public static BindingLoader getInstance() {
    return INSTANCE;
  }

  // ========================================================================
  // Private methods

  private void addMapping(String xmlType, String javaName, boolean fromJavaDefault, boolean fromXmlDefault) {
    XmlTypeName xn = XmlTypeName.forTypeNamed(new QName(xsns, xmlType));
    JavaTypeName jn = JavaTypeName.forString(javaName);
    BindingTypeName btName = BindingTypeName.forPair(jn, xn);
    BindingType bType = new BuiltinBindingType(btName);

    addBindingType(bType);
    if (fromJavaDefault) {
      if (bType.getName().getXmlName().getComponentType() == XmlTypeName.ELEMENT)
        addElementFor(bType.getName().getJavaName(), bType.getName());
      else
        addTypeFor(bType.getName().getJavaName(), bType.getName());
    }
    if (fromXmlDefault) {
      if (bType.getName().getJavaName().isXmlObject())
        addXmlObjectFor(bType.getName().getXmlName(), bType.getName());
      else
        addPojoFor(bType.getName().getXmlName(), bType.getName());
    }
  }

  private void addPojoTwoWay(String xmlType, String javaName) {
    addMapping(xmlType, javaName, true, true);
  }

  private void addPojoXml(String xmlType, String javaName) {
    addMapping(xmlType, javaName, false, true);
  }

  private void addPojoJava(String xmlType, String javaName) {
    addMapping(xmlType, javaName, true, false);
  }

  private void addPojo(String xmlType, String javaName) {
    addMapping(xmlType, javaName, false, false);
  }

  private BuiltinBindingLoader() {
    // todo: should each builtin binding type know about it's print/parse methods?

    addPojoXml("anySimpleType", "java.lang.String");

    addPojoTwoWay("anyType", "java.lang.Object");

    addPojoTwoWay("string", "java.lang.String");
    addPojoXml("normalizedString", "java.lang.String");
    addPojoXml("token", "java.lang.String");
    addPojoXml("language", "java.lang.String");
    addPojoXml("Name", "java.lang.String");
    addPojoXml("NCName", "java.lang.String");
    addPojoXml("NMTOKEN", "java.lang.String");
    addPojoXml("NMTOKENS", "java.lang.String[]");
    addPojoXml("ID", "java.lang.String");
    addPojoXml("IDREF", "java.lang.String");
    addPojoXml("IDREFS", "java.lang.String[]");
    addPojoXml("ENTITY", "java.lang.String");
    addPojoXml("ENTITIES", "java.lang.String[]");

    addPojoXml("duration", "java.lang.String");
    addPojoJava("duration", "org.apache.xmlbeans.GDuration");

    addPojoTwoWay("dateTime", "java.util.Calendar");
    addPojoJava("dateTime", "java.util.Date");
    addPojoXml("time", "java.util.Calendar");
    addPojoXml("date", "java.util.Calendar");
    addPojo("date", "java.util.Date");
    addPojoXml("gYearMonth", "java.lang.String");
    addPojo("gYearMonth", "java.util.Calendar");
    addPojoXml("gYear", "java.lang.String");
    addPojo("gYear", "java.util.Calendar");
    addPojo("gYear", "int");
    addPojoXml("gMonthDay", "java.lang.String");
    addPojo("gMonthDay", "java.util.Calendar");
    addPojoXml("gMonth", "java.lang.String");
    addPojo("gMonth", "java.util.Calendar");
    addPojo("gMonth", "int");
    addPojoXml("gDay", "java.lang.String");
    addPojo("gDay", "java.util.Calendar");
    addPojo("gDay", "int");

    addPojoTwoWay("boolean", "boolean");
    addPojoTwoWay("base64Binary", "byte[]");
    addPojoJava("base64Binary", "java.io.InputStream");
    addPojoXml("hexBinary", "byte[]");
    addPojo("hexBinary", "java.io.InputStream");
    addPojoTwoWay("float", "float");
    addPojoTwoWay("double", "double");
    addPojoTwoWay("decimal", "java.math.BigDecimal");
    addPojoTwoWay("integer", "java.math.BigInteger");
    addPojoTwoWay("long", "long");
    addPojoTwoWay("int", "int");
    addPojoTwoWay("short", "short");
    addPojoTwoWay("byte", "byte");
    addPojoXml("nonPositiveInteger", "java.math.BigInteger");
    addPojoXml("negativeInteger", "java.math.BigInteger");
    addPojoXml("nonNegativeInteger", "java.math.BigInteger");
    addPojoXml("positiveInteger", "java.math.BigInteger");
    addPojoXml("unsignedLong", "java.math.BigInteger");
    addPojoXml("unsignedInt", "long");
    addPojoXml("unsignedShort", "int");
    addPojoXml("unsignedByte", "short");
    addPojoXml("anyURI", "java.lang.String");
    addPojoJava("anyURI", "java.net.URI");
    addPojoTwoWay("QName", "javax.xml.namespace.QName");
    addPojoXml("NOTATION", "java.lang.String");

    addPojoJava("float", Float.class.getName());
    addPojoJava("double", Double.class.getName());
    addPojoJava("long", Long.class.getName());
    addPojoJava("int", Integer.class.getName());
    addPojoJava("short", Short.class.getName());
    addPojoJava("byte", Byte.class.getName());
    addPojoJava("boolean", Boolean.class.getName());
    addPojoJava("unsignedInt", Long.class.getName());
    addPojoJava("unsignedShort", Integer.class.getName());
    addPojoJava("unsignedByte", Short.class.getName());

    //TODO: deal with char and java.lang.Character

  }

}
