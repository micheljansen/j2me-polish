package de.enough.polish.postcompile.io;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import de.enough.bytecode.ASMClassLoader;
import de.enough.polish.util.StringUtil;

import junit.framework.TestCase;

public class SerializationVisitorTest
  extends TestCase
{
  public void test()
    throws Exception
  {
	  doTest("de/enough/polish/postcompile/io/TestSerialization_boolean");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_byte");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_char");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_int");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_double");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_float");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_long");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_short");
	  
	  doTest("de/enough/polish/postcompile/io/TestSerialization_booleanArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_byteArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_charArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_doubleArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_floatArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_intArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_longArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_shortArray");

	  doTest("de/enough/polish/postcompile/io/TestSerialization_Integer");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_String");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_Serializable");

	  doTest("de/enough/polish/postcompile/io/TestSerialization_empty");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_static");

    doTest("de/enough/polish/postcompile/io/TestSerialization_StringArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableArray");
    
	  doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance$AbstractSerializable");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance$InnerSerializable");

	  doTest("de/enough/polish/postcompile/io/TestSerialization_constructor");

    doTest("de/enough/polish/postcompile/io/TestSerialization_complex1");
    doTest("de/enough/polish/postcompile/io/TestSerialization_complex2");
  }

  private void doTest(String className)
    throws ClassNotFoundException
  {
	  ASMClassLoader loader;
	  ClassNode clazz;
	  StringWriter result;

	  String expected, postcompiled;
    
    String classNameTemplate;
    int pos = className.indexOf('$');
    
    if (pos >= 0)
      {
        StringBuffer sb = new StringBuffer();
        
        sb.append(className.substring(0, pos));
        sb.append("_template");
        sb.append(className.substring(pos));
        classNameTemplate = sb.toString();
      }
    else
      {
        classNameTemplate = className + "_template";
      }
	  
	  loader = new ASMClassLoader();
	  clazz = loader.loadClass(classNameTemplate);
	  result = new StringWriter();
	  clazz.accept(new TraceClassVisitor(new PrintWriter(result)));
	  expected = StringUtil.replace( result.toString(), "_template", "");
	  
	  loader = new ASMClassLoader();
	  clazz = loader.loadClass(className);
	  result = new StringWriter();
	  clazz.accept(new SerializationVisitor(new TraceClassVisitor(new PrintWriter(result)), loader, null));
	  postcompiled = result.toString();

	  assertEquals(expected, postcompiled);
  }
}
