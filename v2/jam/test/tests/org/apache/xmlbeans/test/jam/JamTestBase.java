/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache
*    XMLBeans", nor may "Apache" appear in their name, without prior
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2003 BEA Systems
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/
package org.apache.xmlbeans.test.jam;

import junit.framework.TestCase;
import org.apache.xmlbeans.impl.jam.*;
import org.apache.xmlbeans.impl.jam.xml.JamXmlWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

/**
 * <p>Abstract base class for basic jam test cases.  These test cases work
 * against an abstract JamService - they don't care how the java types
 * were loaded.  Extending classes are responsible for implementing the
 * getService() method which should create the service from sources, or
 * classes, or whatever is appropriate.</p>
 *
 * @author Patrick Calahan &lt;email: pcal-at-bea-dot-com&gt;
 */
public abstract class JamTestBase extends TestCase {

  // ========================================================================
  // Constants


  protected static final String
          DUMMY = "org.apache.xmlbeans.test.jam.dummyclasses";

  //this array must contain the names of all of the test classes under
  //dummyclasses
  private static final String[] ALL_CLASSES = {
    DUMMY+".ejb.IEnv",
    DUMMY+".ejb.MyEjbException",
    DUMMY+".ejb.TraderEJB",
    DUMMY+".ejb.TradeResult",

    DUMMY+".jsr175.AnnotatedClass",
    DUMMY+".jsr175.RFEAnnotation",
    DUMMY+".jsr175.RFEAnnotationImpl",

    DUMMY+".Base",
    DUMMY+".Baz",
    DUMMY+".Foo",
    DUMMY+".FooImpl",
    DUMMY+".HeavilyCommented",
    DUMMY+".MyException"
  };


  // this needs to correspond to the methods on the FooImpl dummyclass
  private static final String[][] FOOIMPL_METHODS = {
    {"public",                   "int",      "getId",  null,   null},

    {"public",                   "void",     "setId",  "int id",null},

    {"private final static",     "void",     "setId2",  "double id",null},

    {"protected synchronized ",  "void",     "setId3",  "double id, double id2",null},

    {"protected abstract",       "void",     "setId4",  "double id, double id2, double id3",null},

    {"",             "java.lang.String[][]", "methodDealingWithArrays",  "int[] foo, java.lang.Object[] bar",null},

    {"protected abstract",       "void",     "iThrowExceptions",  "int p1, java.lang.String p2",
     "java.lang.IllegalArgumentException," +
          "java.lang.NoSuchMethodError," +
          DUMMY+".MyException,"+
          DUMMY+".ejb.MyEjbException,"+
          "java.net.MalformedURLException,"+
          "java.lang.OutOfMemoryError,"+
          "java.lang.NullPointerException"
    }
  };

  // this needs to correspond to the methods on the FooImpl dummyclass
  private static final String[] HEAVILY_COMMENTS = {
    "A simple comment.",
    "A comment which\nspans\n\nseveral\n\n\nlines."
  };
  /**
   * A comment which
   * spans
   *
   * several
   *
   *
   * lines.
   */

  private static final boolean VERBOSE = false;

  // ========================================================================
  // Variables

  private JamService mResult = null;
  private JamClassLoader mLoader = null;

  // ========================================================================
  // Constructors

  public JamTestBase() {
    super("JamTestBase");
  }

  public JamTestBase(String casename) {
    super(casename);
  }

  // ========================================================================
  // Abstract methods

  /**
   * Called during setup() to get the JamService object to test against.
   */
  protected abstract JamService getResultToTest() throws Exception;

  //kind of a quick hack for now, should remove this and make sure that
  //even the classes case make the annotations available using a special
  //JStore
  protected abstract boolean isAnnotationsAvailable();

  //kind of a quick hack for now, should remove this and make sure that
  //even the classes case make the annotations available using a special
  //JStore
  protected abstract boolean isParameterNamesKnown();

  protected abstract boolean isCommentsAvailable();

  // ========================================================================
  // Utility methods

  /**
   * Returns the directory in which the sources for the dummyclasses live.
   */
  protected File[] getDummyclassesSourcepath() {
    return new File[] {new File("dummyclasses")};
  }

  /**
   * Returns the directory into which the dummyclasses have been compiled.
   */
  protected File[] getDummyclassesClassPath() {
    return new File[] {new File("../../build/jam/test/dummyclasses")};
  }

  // ========================================================================
  // TestCase implementation

  public void setUp() throws Exception {
    mResult = getResultToTest();
    mLoader = mResult.getClassLoader();
  }

  // ========================================================================
  // Test methods

  public void testAllClassesAvailable() {
    JClass[] classes = mResult.getAllClasses();
    List classNames = new ArrayList(classes.length);
    for(int i=0; i<classes.length; i++) {
      resolved(classes[i]);
      classNames.add(classes[i].getQualifiedName());
    }
    List expected = Arrays.asList(ALL_CLASSES);
    assertTrue("result does not contain all expected classes",
               classNames.containsAll(expected));
    assertTrue("result contains more than expected classes",
               expected.containsAll(classNames));
  }

  private void dump(JClass j) {
    Writer out = new PrintWriter(System.out,true);
    JamXmlWriter jxw = null;
    try {
      jxw = new JamXmlWriter(out);
      jxw.write(j);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }


  public void test175Annotations() throws IOException, XMLStreamException {
    JClass clazz = resolved(mLoader.loadClass(DUMMY+".jsr175.AnnotatedClass"));
    dump(clazz);
  }

  public void testRecursiveResolve() {
    resolveCheckRecursively(mResult.getAllClasses(),new HashSet());
  }


  /**
   * Test comment parsing on the HeavilyCommented dummy class.
   */
  public void testComments() {
    if (!isCommentsAvailable()) return;
    JClass hcImpl = mLoader.loadClass(DUMMY+".HeavilyCommented");
    JMethod[] methods = hcImpl.getDeclaredMethods();
    for(int i=0; i<methods.length; i++) {
      JComment comment = methods[i].getComment();
      assertTrue("'"+comment.getText()+"'\ndoes not match expected\n'" +
                 HEAVILY_COMMENTS[i]+"'",
                 HEAVILY_COMMENTS[i].equals(comment.getText()));
    }
  }


  /**
   * Verify that FooImpl has the correct methods with the correct
   * number of parameters and correct return types.
   */
  public void testFooImplMethods() {
    JClass fooImpl = resolved(mLoader.loadClass(DUMMY+".FooImpl"));
    GoldenInvokable[] methods = GoldenInvokable.createArray(FOOIMPL_METHODS);
    GoldenInvokable.doComparison(fooImpl.getDeclaredMethods(),
                              methods,isParameterNamesKnown(),this);
  }


  public void testInterfaceIsAssignableFrom()
  {
    JClass fooImpl = resolved(mLoader.loadClass(DUMMY+".FooImpl"));
    JClass foo = resolved(mLoader.loadClass(DUMMY+".Foo"));
    assertTrue("Foo should be assignableFrom FooImpl",
               foo.isAssignableFrom(fooImpl));
    assertTrue("FooImpl should not be assignableFrom Foo",
               !fooImpl.isAssignableFrom(foo));
  }

  public void testClassIsAssignableFrom()
  {
    JClass fooImpl = resolved(mLoader.loadClass(DUMMY+".FooImpl"));
    JClass base = resolved(mLoader.loadClass(DUMMY+".Base"));
    assertTrue("Base should be assignableFrom FooImpl",
               base.isAssignableFrom(fooImpl));
    assertTrue("FooImpl should not be assignableFrom Base",
               !fooImpl.isAssignableFrom(base));
  }

  public void testClassIsAssignableFromDifferentClassLoaders()
  {
    JClass baz = resolved(mLoader.loadClass(DUMMY+".Baz"));
    JClass runnable = resolved(mLoader.loadClass("java.lang.Runnable"));
    assertTrue("Runnable should be assignableFrom Baz",
               runnable.isAssignableFrom(baz));
    assertTrue("Baz should not be assignableFrom Runnable",
               !baz.isAssignableFrom(runnable));
  }

  public void testAnnotationPresent()
  {
    if (!isAnnotationsAvailable()) return;
    String ANN = "ejbgen:remote-method";
    JClass ejb = resolved(mLoader.loadClass(DUMMY+".ejb.TraderEJB"));
    JMethod method = ejb.getMethods()[0];
    assertTrue(method.getQualifiedName()+" does not have expected "+ANN+
               " annotation",
               method.getAnnotation(ANN) != null);
  }

  public void testAnnotationValue()
  {
    if (!isAnnotationsAvailable()) return;
    JClass ejb = resolved(mLoader.loadClass(DUMMY+".ejb.TraderEJB"));
    JMethod ejbBuy = ejb.getMethods()[0];
    String CLASS_ANN = "ejbgen:remote-method@isolation-level";
    String CLASS_ANN_VALUE = "Serializable";
    verifyAnnotationValue(ejbBuy,CLASS_ANN,CLASS_ANN_VALUE);
  }


  public void testAnnotationsAndInheritance()
  {
    JClass ejb = resolved(mLoader.loadClass(DUMMY+".ejb.TraderEJB"));
    JClass ienv = resolved(ejb.getInterfaces()[0]);
    JMethod ejbBuy = ejb.getMethods()[0];
    JMethod ienvBuy = ienv.getMethods()[0];
    String INTER_ANN = "ejbgen:remote-method@transaction-attribute";
    String INTER_ANN_VALUE = "NotSupported";
    String CLASS_ANN = "ejbgen:remote-method@isolation-level";
    String CLASS_ANN_VALUE = "Serializable";

    verifyAnnotationAbsent(ejbBuy,INTER_ANN);
    verifyAnnotationAbsent(ienvBuy,CLASS_ANN);

    if (isAnnotationsAvailable()) {
      verifyAnnotationValue(ienvBuy,INTER_ANN,INTER_ANN_VALUE);
      verifyAnnotationValue(ejbBuy,CLASS_ANN,CLASS_ANN_VALUE);
    } else {
      verifyAnnotationAbsent(ienvBuy,INTER_ANN);
      verifyAnnotationAbsent(ejbBuy,CLASS_ANN);
    }
  }


  // ========================================================================
  // Private methods

  private void resolveCheckRecursively(JClass[] clazzes, Set resolved) {
    for(int i=0; i<clazzes.length; i++) {
      resolveCheckRecursively(clazzes[i],resolved);
    }
  }

  private void resolveCheckRecursively(JClass clazz, Set set) {
    if (clazz == null || set.contains(clazz)) return;
    assertTrue("'"+clazz.getQualifiedName()+"' is not resolved",
               !clazz.isUnresolvedType());
    if (VERBOSE) System.out.println("checking "+clazz.getQualifiedName());
    set.add(clazz);
    resolveCheckRecursively(clazz.getSuperclass(),set);
    resolveCheckRecursively(clazz.getInterfaces(),set);
    {
      //check methods
      JMethod[] methods = clazz.getDeclaredMethods();
      for(int i=0; i<methods.length; i++) {
        resolveCheckRecursively(methods[i].getReturnType(),set);
        JParameter[] params = methods[i].getParameters();
        for(int j=0; j<params.length; j++) {
          resolveCheckRecursively(params[j].getType(),set);
        }
      }
    }
    {
      //check constructors
      JConstructor[] ctors = clazz.getConstructors();
      for(int i=0; i<ctors.length; i++) {
        JParameter[] params = ctors[i].getParameters();
        for(int j=0; j<params.length; j++) {
          resolveCheckRecursively(params[j].getType(),set);
        }
      }
    }
    {
      //check fields
      JField[] fields = clazz.getFields();
      for(int i=0; i<fields.length; i++) {
        resolveCheckRecursively(fields[i].getType(),set);
      }
    }
  }


  private JClass resolved(JClass c) {
    assertTrue("class "+c.getQualifiedName()+" is not resolved",
               !c.isUnresolvedType());
    return c;
  }

  private void verifyAnnotationValue(JAnnotatedElement j, String valueId, String val) {
    JAnnotationValue v = j.getAnnotationValue(valueId);
    assertTrue(/*j.getParent().getQualifiedName()+" '"+*/
            j.getQualifiedName()+"' is missing expected annotation value '"+valueId+"'",
            v != null);
    assertTrue(j.getQualifiedName()+"  annotation '"+valueId+"' does not equal "+
               val,val.equals(v.asString().trim()));
  }

  private void verifyAnnotationAbsent(JAnnotatedElement j, String ann) {
    JAnnotation a = j.getAnnotation(ann);
    assertTrue("'"+j.getQualifiedName()+"' expected to NOT have annotation '"+ann+"'",
                a == null);
  }
}